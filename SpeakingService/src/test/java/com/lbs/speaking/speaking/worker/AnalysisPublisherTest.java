package com.lbs.speaking.speaking.worker;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lbs.speaking.config.SpeakingProperties;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class AnalysisPublisherTest {

    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private final SpeakingProperties properties = mock(SpeakingProperties.class);
    private final AnalysisPublisher publisher = new AnalysisPublisher(rabbitTemplate, properties);

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void publishSendsImmediatelyWhenTransactionSynchronizationIsNotActive() {
        SpeakingProperties.Rabbit rabbit = rabbitProperties();
        when(properties.rabbitmq()).thenReturn(rabbit);
        AnalysisRequestedEvent event = new AnalysisRequestedEvent(10L, 2L, 1, false);

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend("exchange", "routing", event);
    }

    @Test
    void publishSendsAfterCommitWhenTransactionSynchronizationIsActive() {
        SpeakingProperties.Rabbit rabbit = rabbitProperties();
        when(properties.rabbitmq()).thenReturn(rabbit);
        AnalysisRequestedEvent event = new AnalysisRequestedEvent(10L, 2L, 1, false);
        TransactionSynchronizationManager.initSynchronization();

        publisher.publish(event);

        verify(rabbitTemplate, never()).convertAndSend("exchange", "routing", event);
        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        synchronizations.forEach(TransactionSynchronization::afterCommit);

        verify(rabbitTemplate).convertAndSend("exchange", "routing", event);
    }

    private SpeakingProperties.Rabbit rabbitProperties() {
        return new SpeakingProperties.Rabbit("exchange", "queue", "routing", "dlx", "dlq", "failed");
    }
}
