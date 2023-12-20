package com.x64technology.linex.services;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.x64technology.linex.R;
import com.x64technology.linex.database.contact.ContactViewModel;
import com.x64technology.linex.database.noroom.DBService;
import com.x64technology.linex.models.Contact;
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

    static ContactViewModel contactViewModel;
    public static Socket socket;

    public static Socket initSocket(Context context, String token) {

        contactViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(ContactViewModel.class);

        Map<String, String> test = new HashMap<>();
        test.put("token", token);

        IO.Options options = new IO.Options();
        options.auth = test;
        try {
            socket = IO.socket("http://192.168.43.30:3000", options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }


  public static void addSocketListeners() {
      socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
          @Override
          public void call(Object... args) {
              System.out.println("connected");
          }
      });

      socket.on(Socket.EVENT_DISCONNECT, args -> System.out.println("discounted"));

      socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
          @Override
          public void call(Object... args) {
              System.out.println("Got connection error");
          }
      });

      socket.on(Constants.EVENT_CONTACT_REQUEST, args -> {
          JSONObject jsonObject = (JSONObject) args[0];
          Contact contact = new Contact();
          try {
              contact.userId = jsonObject.getString(Constants.FROM);
              contact.name = jsonObject.getString(Constants.STR_NAME);
              contact.userDp = jsonObject.getString(Constants.STR_DPLINK);
              contact.reqType = Constants.REQUEST_RECEIVED;

          } catch (JSONException e) {
              throw new RuntimeException(e);
          }
          contactViewModel.insert(contact);
      });

      socket.on(Constants.EVENT_REQUEST_ACCEPTED, args -> System.out.println(args[0]));

      socket.on(Constants.EVENT_REQUEST_REJECTED, args -> System.out.println(args[0]));
  }

    public static void removeSocketListeners() {
        socket.off(Socket.EVENT_CONNECT);
        socket.off(Socket.EVENT_DISCONNECT);
        socket.off(Socket.EVENT_CONNECT_ERROR);
        socket.off();
    }
}
