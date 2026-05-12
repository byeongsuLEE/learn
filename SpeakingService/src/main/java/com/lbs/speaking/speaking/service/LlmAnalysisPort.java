package com.lbs.speaking.speaking.service;

import java.util.List;

public interface LlmAnalysisPort {

    LlmAnalysisResult analyze(String question, String transcript);

    record LlmAnalysisResult(
            String improvedText,
            List<Issue> issues,
            List<RenderBlock> renderBlocks
    ) {
    }

    record Issue(
            String type,
            int startIndex,
            int endIndex,
            String original,
            String suggestion,
            String explanation,
            boolean highlightable
    ) {
    }

    record RenderBlock(
            String kind,
            String text,
            String color,
            Integer issueIndex
    ) {
    }
}
