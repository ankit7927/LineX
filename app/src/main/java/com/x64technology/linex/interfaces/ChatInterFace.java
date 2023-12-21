package com.x64technology.linex.interfaces;

import com.x64technology.linex.models.Message;

public interface ChatInterFace {
    public String activeChat = null;
    void onIncomingMessageActive(Message message);
}
