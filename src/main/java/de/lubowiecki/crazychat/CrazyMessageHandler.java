package de.lubowiecki.crazychat;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class CrazyMessageHandler extends TextWebSocketHandler {

    // Collections.synchronizedList macht aus einer List eine Thread-Sichere-Liste
    // WebSocketSession = Verbindung zum Client
    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private Map<String, WebSocketSession> sessionMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void handleMessage(WebSocketSession senderSession, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(senderSession, message);

        System.out.println();
        if(message.getPayload().toString().startsWith("connect:")) {
            String userName = message.getPayload().toString().substring(8);
            sessionMap.put(userName, senderSession);
        }
        else {
            // Message wird an alle angemeldeten WebSocketsSessions verschickt
            for (WebSocketSession s : sessions) {
                s.sendMessage(message);
            }

            /*
            WebSocketSession targetSession = sessionMap.get("Peter");
            if(targetSession != null) {
                targetSession.sendMessage(message);
            }
            */
        }
    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        // Bei einer Map müsste bei Connecten noch der Name mitgeschickt werden

        // Wenn sich ein neuer Client anmeldet
        sessions.add(session);
        System.out.println("Verbunden: " + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session);
        System.out.println("Getrennt: " + session);
    }
}
