package com.lbs.speaking.speaking.worker;

import com.lbs.speaking.speaking.service.AnalysisProcessingService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisWorker {

    private static final int MAX_ATTEMPT = 3;

    private final AnalysisProcessingService processingService;
    private final AnalysisPublisher analysisPublisher;

    @RabbitListener(
            queues = "${speaking.rabbitmq.queue}",
            containerFactory = "manualAckRabbitListenerContainerFactory"
    )
    public void handle(AnalysisRequestedEvent event, Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            processingService.process(event.recordId(), event.force());
            channel.basicAck(deliveryTag, false);
        } catch (Exception exception) {
            log.warn("Speaking analysis failed. recordId={}", event.recordId(), exception);
            if (event.attempt() < MAX_ATTEMPT) {
                analysisPublisher.publish(new AnalysisRequestedEvent(event.recordId(), event.userId(), event.attempt() + 1, event.force()));
            } else {
                processingService.fail(event.recordId(), exception.getMessage());
            }
            channel.basicAck(deliveryTag, false);
        }
    }
}
