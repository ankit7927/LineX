package com.x64technology.linex.services;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.models.Contact;
import com.x64technology.linex.models.Message;
import com.x64technology.linex.interfaces.ChatInterFace;
import com.x64technology.linex.utils.Constants;
import com.x64technology.linex.interfaces.MainInterFace;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    static ContactViewModel contactViewModel;
    public static Socket socket;
    static AppPreference appPreference;
    public static MainInterFace mainInterFace;
    public static ChatInterFace chatInterFace;

    public static Socket initSocket(Context context, String token) {
        appPreference = new AppPreference(context);
        contactViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(ContactViewModel.class);

        Map<String, String> test = new HashMap<>();
        test.put("token", token);

        IO.Options options = new IO.Options();
        options.auth = test;
        try {
            socket = IO.socket(Constants.BASE_URL, options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }


  public static void addSocketListeners() {
      socket.on(Socket.EVENT_CONNECT, args -> mainInterFace.onSocketConnect());

      socket.on(Socket.EVENT_DISCONNECT, args -> mainInterFace.onSocketDisconnect());

      socket.on(Socket.EVENT_CONNECT_ERROR, args -> mainInterFace.onSocketConnectError());

      socket.on(Constants.EVENT_CONTACT_REQUEST, args -> {
          JSONObject jsonObject = (JSONObject) args[0];
          Contact contact = new Contact();
          try {
              contact.userId = jsonObject.getString(Constants.SENDER);
              contact.name = jsonObject.getString(Constants.STR_NAME);
              contact.userDp = jsonObject.getString(Constants.STR_DPLINK);
              contact.reqType = Constants.REQUEST_RECEIVED;

          } catch (JSONException e) {
              throw new RuntimeException(e);
          }
          mainInterFace.onConnectionReq(contact);
      });

      socket.on(Constants.EVENT_REQUEST_ACCEPTED, args -> {
          JSONObject jsonObject = (JSONObject) args[0];
          String name, userid, dplink;
          try {
              userid = jsonObject.getString(Constants.SENDER);
              name = jsonObject.getString(Constants.STR_NAME);
              dplink = jsonObject.getString(Constants.STR_DPLINK);
          } catch (JSONException e) {
              throw new RuntimeException(e);
          }
          mainInterFace.onReqAccept(userid, name, dplink);
      });

      socket.on(Constants.EVENT_REQUEST_REJECTED, args -> {
          JSONObject jsonObject = (JSONObject) args[0];
          String userid;
          try {
              userid = jsonObject.getString(Constants.SENDER);
          } catch (JSONException e) {
              throw new RuntimeException(e);
          }
          mainInterFace.onReqReject(userid);
      });

      socket.on(Constants.EVENT_REQUEST_CANCELED, args -> {
          JSONObject jsonObject = (JSONObject) args[0];
          String userid;
          try {
              userid = jsonObject.getString(Constants.SENDER);
          } catch (JSONException e) {
              throw new RuntimeException(e);
          }
          mainInterFace.onReqCancel(userid);
      });

      socket.on(Constants.EVENT_MESSAGE, args -> {
          JSONObject jsonObject = (JSONObject) args[0];
          Message message = new Message();
          try {
              message.content = jsonObject.getString(Constants.CONTENT);
              message.timestamp = jsonObject.getInt(Constants.TIMESTAMP);
              message.sender = jsonObject.getString(Constants.SENDER);
              message.receiver = jsonObject.getString(Constants.RECEIVER);
          } catch (JSONException e) {
              throw new RuntimeException(e);
          }

          String activeUser = appPreference.appPref.getString(Constants.STR_ACTIVE_USER, "");
          if (activeUser.equals(message.sender)) chatInterFace.onIncomingMessageActive(message);
          else mainInterFace.onIncomingMessage(message);

      });
  }

    public static void removeSocketListeners() {
        socket.off(Socket.EVENT_CONNECT);
        socket.off(Socket.EVENT_DISCONNECT);
        socket.off(Socket.EVENT_CONNECT_ERROR);
        socket.off();
    }
}
