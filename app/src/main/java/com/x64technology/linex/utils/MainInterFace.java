package com.x64technology.linex.utils;

import com.x64technology.linex.models.Contact;
import com.x64technology.linex.models.Message;

public interface MainInterFace {
    void onSocketConnect();
    void onSocketConnectError();


    // request methods
    void onConnectionReq(Contact contact);
    void onReqAccept(String userid, String name, String dplink);
    void onReqReject(String userid);
    void onReqCancel(String userid);

    void onIncomingMessage(Message message);
}
