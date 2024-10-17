package com.example.websocketmiddleware.client;

import com.example.websocketmiddleware.service.MessageRoutingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.http.HttpHeaders;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Got1fyWebSocketClient extends TextWebSocketHandler {

    @Value("${gotify.ws.url}")
    private String gotifyWsUrl;

    @Value("${gotify.authorization.token}")
    private String authorizationToken;

    @Autowired
    private MessageRoutingService messageRoutingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void connect() {
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketConnectionManager manager = new WebSocketConnectionManager(
                client,
                this,
                gotifyWsUrl
            );
            manager.setHeaders(createHeaders());
            manager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorizationToken);
        return headers;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Connected to Got1fy server");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            List<String> vins = extractVins(jsonNode);
            String content = extractContent(jsonNode);
            
            for (String vin : vins) {
                messageRoutingService.routeMessage(vin, content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("Disconnected from Got1fy server");
    }

    private List<String> extractVins(JsonNode jsonNode) {
        List<String> vins = new ArrayList<>();
        JsonNode extrasNode = jsonNode.path("extras");
        JsonNode vinsNode = extrasNode.path("AIChat::vins");
        if (vinsNode.isArray()) {
            for (JsonNode vinNode : vinsNode) {
                vins.add(vinNode.asText());
            }
        }
        return vins;
    }

    private String extractContent(JsonNode jsonNode) {
        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.put("title", jsonNode.path("title").asText());
        contentNode.put("message", jsonNode.path("message").asText());
        try {
            return objectMapper.writeValueAsString(contentNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
