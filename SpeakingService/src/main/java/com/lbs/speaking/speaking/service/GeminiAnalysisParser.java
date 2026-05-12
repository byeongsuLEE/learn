package com.lbs.speaking.speaking.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
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
                            .toList()
            );
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.LLM_RESPONSE_PARSE_FAILED, exception);
        }
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
            List<GeminiIssue> issues,
            List<GeminiRenderBlock> renderBlocks
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
