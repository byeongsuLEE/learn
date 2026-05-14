package com.lbs.speaking.speaking.worker;

public record AnalysisRequestedEvent(Long recordId, Long userId, int attempt, boolean force) {

    public AnalysisRequestedEvent(Long recordId, Long userId, int attempt) {
        this(recordId, userId, attempt, false);
    }
}
