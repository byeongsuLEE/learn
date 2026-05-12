package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class IssueIndexCorrectorTest {

    private final IssueIndexCorrector corrector = new IssueIndexCorrector();

    @Test
    void correctsInvalidIndexesBySearchingOriginalText() {
        var issue = new LlmAnalysisPort.Issue(
                "GRAMMAR",
                0,
                4,
                "go school",
                "go to school",
                "Missing preposition.",
                true
        );

        List<LlmAnalysisPort.Issue> result = corrector.correct("I go school every day.", List.of(issue));

        assertThat(result.get(0).startIndex()).isEqualTo(2);
        assertThat(result.get(0).endIndex()).isEqualTo(11);
        assertThat(result.get(0).highlightable()).isTrue();
    }

    @Test
    void disablesHighlightWhenIssueCannotBeLocated() {
        var issue = new LlmAnalysisPort.Issue(
                "GRAMMAR",
                0,
                4,
                "missing text",
                "replacement",
                "Not found.",
                true
        );

        List<LlmAnalysisPort.Issue> result = corrector.correct("I go to school.", List.of(issue));

        assertThat(result.get(0).highlightable()).isFalse();
    }
}
