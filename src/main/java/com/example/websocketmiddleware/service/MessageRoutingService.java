package com.example.websocketmiddleware.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageRoutingService {

    private final Map<WebSocketSession, List<String>> clientVinMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void registerClient(WebSocketSession session, List<String> vins) {
        clientVinMap.put(session, vins);
    }

    public void unregisterClient(WebSocketSession session) {
        clientVinMap.remove(session);
    }

    public void routeMessage(String vin, String message) {
        clientVinMap.forEach((session, vins) -> {
            if (vins.contains(vin)) {
                try {
                    ObjectNode jsonMessage = objectMapper.createObjectNode();
                    jsonMessage.put("vin", vin);
                    jsonMessage.set("content", objectMapper.readTree(message));
                    
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(jsonMessage)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
