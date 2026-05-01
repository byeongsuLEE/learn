package com.lbs.user.chat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${chat.broker.host:rabbitmq}")
    private String brokerHost;

    @Value("${chat.broker.port:61613}")
    private int brokerPort;

    @Value("${chat.broker.login:guest}")
    private String brokerLogin;

    @Value("${chat.broker.passcode:guest}")
    private String brokerPasscode;

    @Autowired
    private StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");

        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(brokerHost)
                .setRelayPort(brokerPort)
                .setSystemLogin(brokerLogin)
                .setSystemPasscode(brokerPasscode)
                .setClientLogin(brokerLogin)
                .setClientPasscode(brokerPasscode)
                .setSystemHeartbeatSendInterval(10000)
                .setSystemHeartbeatReceiveInterval(10000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
