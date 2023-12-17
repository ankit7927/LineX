package com.x64technology.linex.services;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    IO.Options options;
    private Socket socket;

    public Socket initSocket(String token) {
        Map<String, String> test = new HashMap<>();
        test.put("token", token);
        options = new IO.Options();
        options.auth = test;
        {
            try {
                socket = IO.socket("http://192.168.43.30:3000", options);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return socket;
    }

    public Socket getSocket() {
        return socket;
    }


  public static void addSocketListeners(Socket socket) {
      socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
          @Override
          public void call(Object... args) {
              System.out.println("connected to socket");
          }
      });

      socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
          @Override
          public void call(Object... args) {
              System.out.println("discounted");
          }
      });

      socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
          @Override
          public void call(Object... args) {
              System.out.println("got connection error");
          }
      });
  }

    public static void removeSocketListeners(Socket socket) {
      socket.off();
    }
}
