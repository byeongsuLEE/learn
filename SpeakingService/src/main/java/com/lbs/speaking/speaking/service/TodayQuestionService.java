package com.lbs.speaking.speaking.service;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.config.SpeakingProperties;
import com.lbs.speaking.speaking.infrastructure.entity.DailyQuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.QuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingQuestionType;
import com.lbs.speaking.speaking.infrastructure.repository.DailyQuestionJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.QuestionJpaRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodayQuestionService {

    private static final String INTERVIEW_CATEGORY = "Interview";

    private final DailyQuestionJpaRepository dailyQuestionRepository;
    private final QuestionJpaRepository questionRepository;
    private final RedisLockService redisLockService;
    private final SpeakingProperties properties;

    @Transactional
    public DailyQuestionEntity getOrCreateTodayQuestion() {
        return getOrCreateTodayQuestion(SpeakingQuestionType.DAILY);
    }

    @Transactional
    public DailyQuestionEntity getOrCreateTodayQuestion(SpeakingQuestionType questionType) {
        LocalDate today = LocalDate.now(properties.zoneId());
        return dailyQuestionRepository.findByQuestionDateAndQuestionType(today, questionType)
                .orElseGet(() -> redisLockService.withLock(
                        "speaking:lock:daily-question:" + questionType + ":" + today,
                        Duration.ofSeconds(5),
                        () -> createTodayQuestion(today, questionType),
                        () -> dailyQuestionRepository.findByQuestionDateAndQuestionType(today, questionType)
                                .orElseGet(() -> createTodayQuestion(today, questionType))
                ));
    }

    private DailyQuestionEntity createTodayQuestion(LocalDate today, SpeakingQuestionType questionType) {
        return dailyQuestionRepository.findByQuestionDateAndQuestionType(today, questionType)
                .orElseGet(() -> {
                    try {
                        QuestionEntity question = pickCandidate(questionType);
                        return dailyQuestionRepository.save(DailyQuestionEntity.create(today, questionType, question));
                    } catch (DataIntegrityViolationException exception) {
                        return dailyQuestionRepository.findByQuestionDateAndQuestionType(today, questionType)
                                .orElseThrow(() -> exception);
                    }
                });
    }

    private QuestionEntity pickCandidate(SpeakingQuestionType questionType) {
        List<Long> usedIds = dailyQuestionRepository.findUsedQuestionIds(questionType);
        List<QuestionEntity> candidates = findCandidates(questionType, usedIds);

        if (candidates.isEmpty()) {
            candidates = findCandidates(questionType, List.of());
        }
        if (candidates.isEmpty()) {
            throw new BusinessException(ErrorCode.TODAY_QUESTION_NOT_FOUND);
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    private List<QuestionEntity> findCandidates(SpeakingQuestionType questionType, List<Long> excludedIds) {
        boolean hasExcluded = !excludedIds.isEmpty();
        if (questionType == SpeakingQuestionType.INTERVIEW) {
            return hasExcluded
                    ? questionRepository.findByActiveTrueAndCategory_NameAndIdNotIn(INTERVIEW_CATEGORY, excludedIds)
                    : questionRepository.findByActiveTrueAndCategory_Name(INTERVIEW_CATEGORY);
        }
        return hasExcluded
                ? questionRepository.findByActiveTrueAndCategory_NameNotAndIdNotIn(INTERVIEW_CATEGORY, excludedIds)
                : questionRepository.findByActiveTrueAndCategory_NameNot(INTERVIEW_CATEGORY);
    }
}
