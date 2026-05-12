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
                List.of(new RenderBlock("improved", improved, "blue", null))
        );
    }
}
