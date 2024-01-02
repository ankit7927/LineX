package com.x64technology.linex.interfaces;

import com.x64technology.linex.models.Contact;

public interface ContactProfile {
    void onContactClicked(Contact contact);

    void onRequestAcceptClicked(Contact contact);

    void onRequestRejectClicked(Contact contact);

    void onRequestCancelClicked(Contact contact);

}
