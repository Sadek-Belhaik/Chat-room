package com.udacity.chatroomproject.chat;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session   WebSocket Session
 */

@Component
@ServerEndpoint("/chat/{username}")
public class WebSocketChatServer {

    /**
     * All chat sessions.
     */

    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    private static void sendMessageToAll(String msg) throws IOException, EncodeException{
        for (Session session: onlineSessions.values()){
            synchronized (session) {
                try {
                    session.getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Open connection, 1) add session, 2) add user.
     * Once user enter to the chat room
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        if(onlineSessions.containsKey(session.getId()))
        {
            return;
        }
        onlineSessions.put(username, session);
        sendMessageToAll(Message.jsonConverter("ENTER", username, "joined the chat.", onlineSessions.size()));
    }

    /**
     * Send message, 1) get username and session, 2) send message to all.
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) throws IOException, EncodeException {
        Message message = (Message) JSON.parseObject(jsonStr, Message.class);
        sendMessageToAll(Message.jsonConverter("CHAT", message.getUsername(), message.getMessage(), onlineSessions.size()));
    }

    /**
     * Close connection, 1) remove session, 2) update user.
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        onlineSessions.remove(username, session);
        sendMessageToAll(Message.jsonConverter("LEAVE", username, "left the chat.", onlineSessions.size()));
    }

    /**
     * Print exception.
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

}
