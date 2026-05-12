package com.lbs.speaking.speaking.service;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.config.SpeakingProperties;
import com.lbs.speaking.speaking.infrastructure.entity.DailyQuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.QuestionEntity;
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

    private final DailyQuestionJpaRepository dailyQuestionRepository;
    private final QuestionJpaRepository questionRepository;
    private final RedisLockService redisLockService;
    private final SpeakingProperties properties;

    @Transactional
    public DailyQuestionEntity getOrCreateTodayQuestion() {
        LocalDate today = LocalDate.now(properties.zoneId());
        return dailyQuestionRepository.findByQuestionDate(today)
                .orElseGet(() -> redisLockService.withLock(
                        "speaking:lock:daily-question:" + today,
                        Duration.ofSeconds(5),
                        () -> createTodayQuestion(today),
                        () -> dailyQuestionRepository.findByQuestionDate(today)
                                .orElseGet(() -> createTodayQuestion(today))
                ));
    }

    private DailyQuestionEntity createTodayQuestion(LocalDate today) {
        return dailyQuestionRepository.findByQuestionDate(today)
                .orElseGet(() -> {
                    try {
                        QuestionEntity question = pickCandidate();
                        return dailyQuestionRepository.save(DailyQuestionEntity.create(today, question));
                    } catch (DataIntegrityViolationException exception) {
                        return dailyQuestionRepository.findByQuestionDate(today)
                                .orElseThrow(() -> exception);
                    }
                });
    }

    private QuestionEntity pickCandidate() {
        List<Long> usedIds = dailyQuestionRepository.findUsedQuestionIds();
        List<QuestionEntity> candidates = usedIds.isEmpty()
                ? questionRepository.findByActiveTrue()
                : questionRepository.findByActiveTrueAndIdNotIn(usedIds);

        if (candidates.isEmpty()) {
            candidates = questionRepository.findByActiveTrue();
        }
        if (candidates.isEmpty()) {
            throw new BusinessException(ErrorCode.TODAY_QUESTION_NOT_FOUND);
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }
}
