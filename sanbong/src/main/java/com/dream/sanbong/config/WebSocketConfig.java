package com.dream.sanbong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-support")
            .setAllowedOriginPatterns("http://localhost:5173", "http://localhost:3000", "*")
            .withSockJS();
            
        // Thêm dòng này để cho phép kết nối Native WebSocket trực tiếp (không qua SockJS)
        registry.addEndpoint("/ws-support")
            .setAllowedOriginPatterns("*");
    }
}