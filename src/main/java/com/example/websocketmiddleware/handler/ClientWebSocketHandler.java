package com.example.websocketmiddleware.handler;

import com.example.websocketmiddleware.service.MessageRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.List;

public class ClientWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private MessageRoutingService messageRoutingService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String vins = session.getUri().getQuery().split("=")[1];
        List<String> vinList = Arrays.asList(vins.split(","));
        messageRoutingService.registerClient(session, vinList);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理来自客户端的消息（如果需要）
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        messageRoutingService.unregisterClient(session);
    }
}