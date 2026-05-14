package com.lbs.speaking.speaking.service;

import java.util.List;

public interface LlmAnalysisPort {

    LlmAnalysisResult analyze(String question, String transcript);

    record LlmAnalysisResult(
            String improvedText,
            List<Issue> issues,
            List<RenderBlock> renderBlocks,
            Feedback feedback
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

    record Feedback(
            int overallScore,
            String overallComment,
            List<Metric> metrics
    ) {
    }

    record Metric(
            String key,
            String label,
            int score,
            String comment
    ) {
    }
}
