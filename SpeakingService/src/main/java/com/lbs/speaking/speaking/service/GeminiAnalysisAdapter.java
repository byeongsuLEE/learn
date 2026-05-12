package com.lbs.speaking.speaking.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.config.SpeakingProperties;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "speaking.gemini", name = "enabled", havingValue = "true")
public class GeminiAnalysisAdapter implements LlmAnalysisPort {

    private final SpeakingProperties properties;
    private final GeminiPromptBuilder promptBuilder;
    private final GeminiAnalysisParser parser;

    @Override
    public LlmAnalysisResult analyze(String question, String transcript) {
        SpeakingProperties.Gemini gemini = properties.gemini();
        if (gemini == null || !StringUtils.hasText(gemini.apiKey())) {
            throw new BusinessException(ErrorCode.LLM_REQUEST_FAILED);
        }

        String prompt = promptBuilder.build(question, transcript);
        GeminiGenerateContentRequest request = new GeminiGenerateContentRequest(
                List.of(new GeminiContent("user", List.of(new GeminiPart(prompt))))
        );

        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl(gemini.endpoint())
                    .requestFactory(new org.springframework.http.client.JdkClientHttpRequestFactory(
                            java.net.http.HttpClient.newBuilder()
                                    .connectTimeout(Duration.ofSeconds(gemini.timeoutSeconds()))
                                    .build()
                    ))
                    .build();

            GeminiGenerateContentResponse response = restClient.post()
                    .uri("/v1beta/models/{model}:generateContent", gemini.model())
                    .header("x-goog-api-key", gemini.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiGenerateContentResponse.class);

            return parser.parse(extractText(response));
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.LLM_REQUEST_FAILED, exception);
        }
    }

    private String extractText(GeminiGenerateContentResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
        }

        GeminiCandidate candidate = response.candidates().get(0);
        if (candidate.content() == null || candidate.content().parts() == null || candidate.content().parts().isEmpty()) {
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_FAILED);
        }

        return candidate.content().parts().stream()
                .map(GeminiPart::text)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_FAILED));
    }

    private record GeminiGenerateContentRequest(List<GeminiContent> contents) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiGenerateContentResponse(List<GeminiCandidate> candidates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiCandidate(GeminiContent content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiContent(String role, List<GeminiPart> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiPart(String text) {
    }
}
