package com.lbs.speaking.speaking.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IssueIndexCorrector {

    public List<LlmAnalysisPort.Issue> correct(String originalText, List<LlmAnalysisPort.Issue> issues) {
        List<LlmAnalysisPort.Issue> corrected = new ArrayList<>();
        for (LlmAnalysisPort.Issue issue : issues) {
            if (isValid(originalText, issue)) {
                corrected.add(issue);
                continue;
            }

            int start = originalText.indexOf(issue.original());
            if (start >= 0) {
                corrected.add(new LlmAnalysisPort.Issue(
                        issue.type(),
                        start,
                        start + issue.original().length(),
                        issue.original(),
                        issue.suggestion(),
                        issue.explanation(),
                        true
                ));
            } else {
                corrected.add(new LlmAnalysisPort.Issue(
                        issue.type(),
                        issue.startIndex(),
                        issue.endIndex(),
                        issue.original(),
                        issue.suggestion(),
                        issue.explanation(),
                        false
                ));
            }
        }
        return corrected;
    }

    private boolean isValid(String originalText, LlmAnalysisPort.Issue issue) {
        int start = issue.startIndex();
        int end = issue.endIndex();
        return start >= 0
                && end <= originalText.length()
                && start < end
                && originalText.substring(start, end).equals(issue.original());
    }
}
