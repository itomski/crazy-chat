package de.lubowiecki.crazychat;

import com.google.gson.Gson;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
public class CrazyMessageHandler extends TextWebSocketHandler {

    // Collections.synchronizedList macht aus einer List eine Thread-Sichere-Liste
    // WebSocketSession = Verbindung zum Client
    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private Map<String, WebSocketSession> sessionMap = Collections.synchronizedMap(new HashMap<>());

    private static final Gson GSON = new Gson();

    @Override
    public void handleMessage(WebSocketSession senderSession, WebSocketMessage<?> message) throws Exception {
        //super.handleMessage(senderSession, message);

        // getPayload() liefert den Inhalt der Message, hier JSON
        if(message.getPayload().toString().startsWith("connect:")) {
            // TODO Payload maskieren
            String userName = message.getPayload().toString().substring(8);
            sessionMap.put(userName, senderSession);
        }
        else {
            // Message wird an alle angemeldeten WebSocketsSessions verschickt
            sendToAllConnected(message);

            /*
            WebSocketSession targetSession = sessionMap.get("Peter");
            if(targetSession != null) {
                targetSession.sendMessage(message);
            }
            */
        }
    }

    private void sendToAllConnected(WebSocketMessage msg) throws IOException {
        for (WebSocketSession s : sessions) {
            s.sendMessage(msg);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //super.afterConnectionEstablished(session);
        // Bei einer Map müsste bei Connecten noch der Name mitgeschickt werden

        // Wenn sich ein neuer Client anmeldet
        sessions.add(session);
        CustomMessage msg = new CustomMessage();
        msg.setName("chat");
        msg.setMessage("User connected");
        msg.getUsers().addAll(sessionMap.keySet());
        sendToAllConnected(new TextMessage(GSON.toJson(msg)));
        System.out.println("Verbunden: " + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //super.afterConnectionClosed(session, status);
        sessions.remove(session);
        CustomMessage msg = new CustomMessage();
        msg.setName("chat");
        msg.setMessage("User disconnected");
        msg.getUsers().addAll(sessionMap.keySet());
        sendToAllConnected(new TextMessage(GSON.toJson(msg)));
        System.out.println("Getrennt: " + session);
    }
}
