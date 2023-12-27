package com.x64technology.linex.interfaces;

import com.x64technology.linex.models.Contact;
import com.x64technology.linex.models.Message;

public interface MainInterFace {
    void onSocketConnect();
    void onSocketConnectError();
    void onSocketDisconnect();


    // request methods
    void onConnectionReq(Contact contact);
    void onReqAccept(String userid, String name, String dplink);
    void onReqReject(String userid);
    void onReqCancel(String userid);

    // message methods
    void onIncomingMessage(Message message);
}
