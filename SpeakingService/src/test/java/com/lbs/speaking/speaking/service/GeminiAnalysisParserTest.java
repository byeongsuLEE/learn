package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import org.junit.jupiter.api.Test;

class GeminiAnalysisParserTest {

    private final GeminiAnalysisParser parser = new GeminiAnalysisParser(new ObjectMapper());

    @Test
    void parsesJsonEvenWhenModelAddsTextAroundIt() {
        String response = """
                Sure. Here is the JSON:
                {
                  "improvedText": "I go to school every morning.",
                  "issues": [
                    {
                      "type": "GRAMMAR",
                      "startIndex": 2,
                      "endIndex": 11,
                      "original": "go school",
                      "suggestion": "go to school",
                      "explanation": "Missing preposition.",
                      "highlightable": true
                    }
                  ],
                  "renderBlocks": [
                    {"kind": "original", "text": "go school", "color": "red", "issueIndex": 0},
                    {"kind": "improved", "text": "go to school", "color": "blue", "issueIndex": 0}
                  ]
                }
                Thanks.
                """;

        LlmAnalysisPort.LlmAnalysisResult result = parser.parse(response);

        assertThat(result.improvedText()).isEqualTo("I go to school every morning.");
        assertThat(result.issues()).hasSize(1);
        assertThat(result.issues().get(0).suggestion()).isEqualTo("go to school");
        assertThat(result.renderBlocks()).hasSize(2);
    }

    @Test
    void throwsDomainErrorWhenRequiredFieldsAreMissing() {
        String response = """
                {
                  "issues": [],
                  "renderBlocks": []
                }
                """;

        assertThatThrownBy(() -> parser.parse(response))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
    }

    @Test
    void throwsDomainErrorWhenNoJsonObjectExists() {
        assertThatThrownBy(() -> parser.parse("I cannot answer this."))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
    }

    @Test
    void throwsDomainErrorWhenResponseIsBlank() {
        assertThatThrownBy(() -> parser.parse("   "))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
    }

    @Test
    void throwsDomainErrorWhenJsonIsMalformed() {
        String response = """
                ```json
                {
                  "improvedText": "I went home.",
                  "issues": [],
                  "renderBlocks": [
                }
                ```
                """;

        assertThatThrownBy(() -> parser.parse(response))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
    }

    @Test
    void ignoresUnknownFieldsFromModelPayload() {
        String response = """
                {
                  "improvedText": "I went home.",
                  "summary": "extra model field",
                  "issues": [
                    {
                      "type": "VOCABULARY",
                      "startIndex": 0,
                      "endIndex": 4,
                      "original": "went",
                      "suggestion": "returned",
                      "explanation": "More specific verb.",
                      "highlightable": false,
                      "confidence": 0.9
                    }
                  ],
                  "renderBlocks": []
                }
                """;

        LlmAnalysisPort.LlmAnalysisResult result = parser.parse(response);

        assertThat(result.improvedText()).isEqualTo("I went home.");
        assertThat(result.issues()).hasSize(1);
        assertThat(result.issues().get(0).highlightable()).isFalse();
    }
}
