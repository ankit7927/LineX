package com.x64technology.linex.services;

import android.content.Context;

import com.x64technology.linex.R;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    Context context;
    IO.Options options;
    DBService dbService;
    public static Socket socket;

    public SocketManager(Context context) {
        this.context = context;
        dbService = new DBService(context);
    }

    public Socket initSocket(String token) {
        Map<String, String> test = new HashMap<>();
        test.put("token", token);

        options = new IO.Options();
        options.auth = test;
        try {
            socket = IO.socket("http://192.168.43.30:3000", options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }


  public void addSocketListeners(Socket socket) {
      socket.on(Socket.EVENT_CONNECT, args -> System.out.println("connected to socket"));

      socket.on(Socket.EVENT_DISCONNECT, args -> System.out.println("discounted"));

      socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
          @Override
          public void call(Object... args) {
              JSONObject jsonObject = (JSONObject) args[0];
              System.out.println(args[0]);
              System.out.println(jsonObject);
              System.out.println("got connection error");
          }
      });

      socket.on(context.getString(R.string.event_contact_request), args -> {
          System.out.println("got connection req");
          JSONObject jsonObject = (JSONObject) args[0];
          String senderUsername, senderDp;
          try {
              senderUsername = jsonObject.getString("senderUsername");
              senderDp = jsonObject.getString("senderDpLink");
          } catch (JSONException e) {
              throw new RuntimeException(e);
          }
          dbService.insertContact(Constants.STR_UNKNOWN, senderUsername, Constants.STR_UNKNOWN, senderDp, Constants.REQUEST_RECEIVED);
      });

      socket.on(context.getString(R.string.event_request_accepted), args -> System.out.println(args[0]));

      socket.on(context.getString(R.string.event_request_rejected), args -> System.out.println(args[0]));
  }

    public void removeSocketListeners(Socket socket) {
      socket.off();
    }
}
