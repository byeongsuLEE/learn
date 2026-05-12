package com.lbs.speaking.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange speakingAnalysisExchange(SpeakingProperties properties) {
        return new DirectExchange(properties.rabbitmq().exchange(), true, false);
    }

    @Bean
    public Queue speakingAnalysisQueue(SpeakingProperties properties) {
        return QueueBuilder.durable(properties.rabbitmq().queue())
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName(properties))
                .withArgument("x-dead-letter-routing-key", deadLetterRoutingKey(properties))
                .build();
    }

    @Bean
    public Binding speakingAnalysisBinding(@Qualifier("speakingAnalysisQueue") Queue speakingAnalysisQueue,
                                           @Qualifier("speakingAnalysisExchange") DirectExchange speakingAnalysisExchange,
                                           SpeakingProperties properties) {
        return BindingBuilder
                .bind(speakingAnalysisQueue)
                .to(speakingAnalysisExchange)
                .with(properties.rabbitmq().routingKey());
    }

    @Bean
    public DirectExchange speakingAnalysisDeadLetterExchange(SpeakingProperties properties) {
        return new DirectExchange(deadLetterExchangeName(properties), true, false);
    }

    @Bean
    public Queue speakingAnalysisDeadLetterQueue(SpeakingProperties properties) {
        return QueueBuilder.durable(properties.rabbitmq().deadLetterQueue()).build();
    }

    @Bean
    public Binding speakingAnalysisDeadLetterBinding(
                                                    @Qualifier("speakingAnalysisDeadLetterQueue") Queue speakingAnalysisDeadLetterQueue,
                                                    @Qualifier("speakingAnalysisDeadLetterExchange") DirectExchange speakingAnalysisDeadLetterExchange,
                                                    SpeakingProperties properties) {
        return BindingBuilder
                .bind(speakingAnalysisDeadLetterQueue)
                .to(speakingAnalysisDeadLetterExchange)
                .with(deadLetterRoutingKey(properties));
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory manualAckRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    private String deadLetterExchangeName(SpeakingProperties properties) {
        return properties.rabbitmq().deadLetterExchange();
    }

    private String deadLetterRoutingKey(SpeakingProperties properties) {
        return properties.rabbitmq().deadLetterRoutingKey();
    }
}
