package com.lbs.speaking.speaking.worker;

public record AnalysisRequestedEvent(Long recordId, Long userId, int attempt) {
}
