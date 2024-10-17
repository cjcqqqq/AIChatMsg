package com.example.websocketmiddleware.config;

import com.example.websocketmiddleware.handler.ClientWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${server.port}")
    private int serverPort;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(clientWebSocketHandler(), "/ws")
                .setAllowedOrigins("*");
    }

    @Bean
    public ClientWebSocketHandler clientWebSocketHandler() {
        return new ClientWebSocketHandler();
    }
}
