package com.bri64.discord.chillbot.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@SuppressWarnings({"unused", "WeakerAccess"})
@WebSocket
public class WebPlayerSocket {

  private Set<Session> users;

  public WebPlayerSocket() {
    this.users = new HashSet<>();
  }

  @OnWebSocketConnect
  public void onConnect(Session session) {
    //System.out.println("user connected");
    users.add(session);
  }

  @OnWebSocketClose
  public void onDisconnect(Session session, int statusCode, String reason) {
    //System.out.println("user disconnected");
    users.remove(session);
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) {
    //System.out.println(message);
    /*if (message.equals("connect")) {
        System.out.println("user added");
      }*/
  }

  public void sendMessage(String message) {
    users.forEach((session) -> {
      try {
        //System.out.println("sending message");
        session.getRemote().sendString(message);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

}
