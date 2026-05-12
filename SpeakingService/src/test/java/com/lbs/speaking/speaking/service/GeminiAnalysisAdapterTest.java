package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.config.SpeakingProperties;
import java.util.List;
import org.junit.jupiter.api.Test;

class GeminiAnalysisAdapterTest {

    @Test
    void throwsRequestErrorWhenGeminiPropertiesAreMissing() {
        GeminiAnalysisAdapter adapter = new GeminiAnalysisAdapter(
                properties(null),
                mock(GeminiPromptBuilder.class),
                mock(GeminiAnalysisParser.class)
        );

        assertThatThrownBy(() -> adapter.analyze("question", "answer"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LLM_REQUEST_FAILED);
    }

    @Test
    void throwsRequestErrorWhenApiKeyIsBlank() {
        GeminiAnalysisAdapter adapter = new GeminiAnalysisAdapter(
                properties(new SpeakingProperties.Gemini(
                        true,
                        "https://generativelanguage.googleapis.com",
                        " ",
                        "gemini-2.5-flash",
                        30
                )),
                mock(GeminiPromptBuilder.class),
                mock(GeminiAnalysisParser.class)
        );

        assertThatThrownBy(() -> adapter.analyze("question", "answer"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LLM_REQUEST_FAILED);
    }

    private SpeakingProperties properties(SpeakingProperties.Gemini gemini) {
        return new SpeakingProperties(
                "Asia/Seoul",
                10,
                20_971_520,
                180,
                List.of("audio/webm"),
                new SpeakingProperties.Rabbit("exchange", "queue", "routing", "dlx", "dlq", "dlq-routing"),
                new SpeakingProperties.Jobs("0 */5 * * * *", "0 0 * * * *", 15, 24),
                gemini
        );
    }
}
