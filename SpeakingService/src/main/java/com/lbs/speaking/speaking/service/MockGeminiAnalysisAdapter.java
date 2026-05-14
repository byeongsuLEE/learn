package com.lbs.speaking.speaking.service;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "speaking.gemini", name = "enabled", havingValue = "false", matchIfMissing = true)
public class MockGeminiAnalysisAdapter implements LlmAnalysisPort {

    @Override
    public LlmAnalysisResult analyze(String question, String transcript) {
        String improved = transcript.trim().isEmpty()
                ? "I need more details to answer this question clearly."
                : transcript.trim();

        return new LlmAnalysisResult(
                improved,
                List.of(),
                List.of(new RenderBlock("improved", improved, "blue", null)),
                new Feedback(
                        80,
                        "답변을 잘 완성했어요. 개선 답변을 보며 한 번 더 말해보세요.",
                        List.of(
                                new Metric("fluency", "유창성", 80, "흐름이 자연스러운 편이에요."),
                                new Metric("vocabulary", "어휘", 80, "핵심 표현을 잘 사용했어요."),
                                new Metric("structure", "답변 구성", 80, "답변 구조가 이해하기 쉬워요."),
                                new Metric("relevance", "질문 적합도", 80, "질문에 잘 맞는 답변이에요.")
                        )
                )
        );
    }
}
