package com.lbs.speaking.speaking.worker;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.config.SpeakingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final SpeakingProperties properties;

    public void publish(AnalysisRequestedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    properties.rabbitmq().exchange(),
                    properties.rabbitmq().routingKey(),
                    event
            );
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.MESSAGE_PUBLISH_FAILED, exception);
        }
    }
}
