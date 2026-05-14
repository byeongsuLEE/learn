package com.lbs.speaking.speaking.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GeminiAnalysisParser {

    private final ObjectMapper objectMapper;

    public GeminiAnalysisParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public LlmAnalysisPort.LlmAnalysisResult parse(String rawResponse) {
        try {
            GeminiPayload payload = objectMapper.readValue(extractJsonObject(rawResponse), GeminiPayload.class);
            validate(payload);
            return new LlmAnalysisPort.LlmAnalysisResult(
                    payload.improvedText(),
                    payload.issues().stream()
                            .map(issue -> new LlmAnalysisPort.Issue(
                                    issue.type(),
                                    issue.startIndex(),
                                    issue.endIndex(),
                                    issue.original(),
                                    issue.suggestion(),
                                    issue.explanation(),
                                    issue.highlightable()
                            ))
                            .toList(),
                    payload.renderBlocks().stream()
                            .map(block -> new LlmAnalysisPort.RenderBlock(
                                    block.kind(),
                                    block.text(),
                                    block.color(),
                                    block.issueIndex()
                            ))
                            .toList(),
                    toFeedback(payload.feedback())
            );
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_FAILED, exception);
        }
    }

    private LlmAnalysisPort.Feedback toFeedback(GeminiFeedback feedback) {
        if (feedback == null) {
            return defaultFeedback();
        }
        List<LlmAnalysisPort.Metric> metrics = new ArrayList<>();
        if (feedback.metrics() != null) {
            metrics.addAll(feedback.metrics().stream()
                    .map(metric -> new LlmAnalysisPort.Metric(
                            metric.key(),
                            metric.label(),
                            clampScore(metric.score()),
                            metric.comment()
                    ))
                    .toList());
        }
        if (metrics.isEmpty()) {
            return defaultFeedback();
        }
        return new LlmAnalysisPort.Feedback(
                clampScore(feedback.overallScore()),
                StringUtils.hasText(feedback.overallComment()) ? feedback.overallComment() : "답변을 기준으로 피드백을 확인해 보세요.",
                metrics
        );
    }

    private int clampScore(int score) {
        return Math.max(0, Math.min(100, score));
    }

    private LlmAnalysisPort.Feedback defaultFeedback() {
        return new LlmAnalysisPort.Feedback(
                70,
                "구체적인 평가 점수가 없어 기본 피드백을 표시합니다.",
                List.of(
                        new LlmAnalysisPort.Metric("fluency", "유창성", 70, "답변 흐름을 조금 더 자연스럽게 다듬어 보세요."),
                        new LlmAnalysisPort.Metric("vocabulary", "어휘", 70, "상황에 맞는 표현을 조금 더 다양하게 사용해 보세요."),
                        new LlmAnalysisPort.Metric("structure", "답변 구성", 70, "핵심 생각과 이유를 더 분명하게 연결해 보세요."),
                        new LlmAnalysisPort.Metric("relevance", "질문 적합도", 70, "질문에 직접 답하는 내용을 더 보강해 보세요.")
                )
        );
    }

    private String extractJsonObject(String rawResponse) {
        if (!StringUtils.hasText(rawResponse)) {
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
        }

        int start = rawResponse.indexOf('{');
        int end = rawResponse.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
        }
        return rawResponse.substring(start, end + 1);
    }

    private void validate(GeminiPayload payload) {
        if (payload == null
                || !StringUtils.hasText(payload.improvedText())
                || payload.issues() == null
                || payload.renderBlocks() == null) {
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiPayload(
            String improvedText,
            GeminiFeedback feedback,
            List<GeminiIssue> issues,
            List<GeminiRenderBlock> renderBlocks
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiFeedback(
            int overallScore,
            String overallComment,
            List<GeminiMetric> metrics
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiMetric(
            String key,
            String label,
            int score,
            String comment
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiIssue(
            String type,
            int startIndex,
            int endIndex,
            String original,
            String suggestion,
            String explanation,
            boolean highlightable
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiRenderBlock(
            String kind,
            String text,
            String color,
            Integer issueIndex
    ) {
    }
}
