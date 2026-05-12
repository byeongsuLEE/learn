package com.lbs.speaking.speaking.worker;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import com.lbs.speaking.speaking.service.AnalysisProcessingService;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;

class AnalysisWorkerTest {

    private final AnalysisProcessingService processingService = mock(AnalysisProcessingService.class);
    private final AnalysisPublisher analysisPublisher = mock(AnalysisPublisher.class);
    private final AnalysisWorker worker = new AnalysisWorker(processingService, analysisPublisher);
    private final Channel channel = mock(Channel.class);

    @Test
    void acksMessageAfterSuccessfulProcessing() throws Exception {
        AnalysisRequestedEvent event = new AnalysisRequestedEvent(10L, 2L, 1);

        worker.handle(event, channel, 99L);

        verify(processingService).process(10L);
        verify(channel).basicAck(99L, false);
        verify(analysisPublisher, never()).publish(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void republishesNextAttemptAndAcksOriginalMessageWhenProcessingFailsBelowLimit() throws Exception {
        AnalysisRequestedEvent event = new AnalysisRequestedEvent(10L, 2L, 2);
        doThrow(new IllegalStateException("timeout")).when(processingService).process(10L);

        worker.handle(event, channel, 99L);

        verify(analysisPublisher).publish(new AnalysisRequestedEvent(10L, 2L, 3));
        verify(processingService, never()).fail(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());
        verify(channel).basicAck(99L, false);
    }

    @Test
    void marksRecordFailedAndAcksOriginalMessageWhenRetryLimitIsReached() throws Exception {
        AnalysisRequestedEvent event = new AnalysisRequestedEvent(10L, 2L, 3);
        doThrow(new IllegalStateException("timeout")).when(processingService).process(10L);

        worker.handle(event, channel, 99L);

        verify(processingService).fail(10L, "timeout");
        verify(analysisPublisher, never()).publish(org.mockito.ArgumentMatchers.any());
        verify(channel).basicAck(99L, false);
    }
}
