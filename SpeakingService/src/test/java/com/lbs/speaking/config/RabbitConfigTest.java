package com.lbs.speaking.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

class RabbitConfigTest {

    private final RabbitConfig rabbitConfig = new RabbitConfig();
    private final SpeakingProperties properties = new SpeakingProperties(
            "Asia/Seoul",
            10,
            20_971_520,
            180,
            List.of("audio/webm"),
            new SpeakingProperties.Rabbit(
                    "speaking.analysis.exchange",
                    "speaking.analysis.queue",
                    "speaking.analysis.requested",
                    "speaking.analysis.dlx",
                    "speaking.analysis.dlq",
                    "speaking.analysis.failed"
            ),
            new SpeakingProperties.Jobs("0 */10 * * * *", "0 0 * * * *", 15, 24),
            new SpeakingProperties.Gemini(false, "https://generativelanguage.googleapis.com", "", "gemini-2.5-flash-lite", 30)
    );

    @Test
    void analysisQueueIsBoundToDeadLetterExchange() {
        Queue queue = rabbitConfig.speakingAnalysisQueue(properties);

        assertThat(queue.getName()).isEqualTo("speaking.analysis.queue");
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", "speaking.analysis.dlx")
                .containsEntry("x-dead-letter-routing-key", "speaking.analysis.failed");
    }

    @Test
    void createsConfiguredDeadLetterQueue() {
        Queue queue = rabbitConfig.speakingAnalysisDeadLetterQueue(properties);

        assertThat(queue.getName()).isEqualTo("speaking.analysis.dlq");
        assertThat(queue.isDurable()).isTrue();
    }

    @Test
    void bindsAnalysisQueueToConfiguredRoutingKey() {
        Queue queue = rabbitConfig.speakingAnalysisQueue(properties);
        DirectExchange exchange = rabbitConfig.speakingAnalysisExchange(properties);

        Binding binding = rabbitConfig.speakingAnalysisBinding(queue, exchange, properties);

        assertThat(binding.getDestination()).isEqualTo("speaking.analysis.queue");
        assertThat(binding.getExchange()).isEqualTo("speaking.analysis.exchange");
        assertThat(binding.getRoutingKey()).isEqualTo("speaking.analysis.requested");
    }

    @Test
    void bindsDeadLetterQueueToDerivedDeadLetterRoutingKey() {
        Queue queue = rabbitConfig.speakingAnalysisDeadLetterQueue(properties);
        DirectExchange exchange = rabbitConfig.speakingAnalysisDeadLetterExchange(properties);

        Binding binding = rabbitConfig.speakingAnalysisDeadLetterBinding(queue, exchange, properties);

        assertThat(binding.getDestination()).isEqualTo("speaking.analysis.dlq");
        assertThat(binding.getExchange()).isEqualTo("speaking.analysis.dlx");
        assertThat(binding.getRoutingKey()).isEqualTo("speaking.analysis.failed");
    }

    @Test
    void manualAckContainerUsesManualAckAndDoesNotRequeueRejectedMessages() {
        ConnectionFactory connectionFactory = org.mockito.Mockito.mock(ConnectionFactory.class);
        MessageConverter messageConverter = rabbitConfig.messageConverter();

        SimpleRabbitListenerContainerFactory factory =
                rabbitConfig.manualAckRabbitListenerContainerFactory(connectionFactory, messageConverter);

        assertThat(ReflectionTestUtils.getField(factory, "connectionFactory")).isSameAs(connectionFactory);
        assertThat(ReflectionTestUtils.getField(factory, "messageConverter")).isSameAs(messageConverter);
        assertThat(ReflectionTestUtils.getField(factory, "acknowledgeMode")).isEqualTo(AcknowledgeMode.MANUAL);
        assertThat(ReflectionTestUtils.getField(factory, "defaultRequeueRejected")).isEqualTo(false);
    }
}
