package com.aj.aladdin.domain.components.messages;

import android.util.Log;

class Message {

    String message;
    String senderID;
    String createdAt;

    public Message(String message, String senderID, String createdAt) {
        this.message = message;
        this.senderID = senderID;
        this.createdAt = createdAt;
    }


    public String getMessage() {
        return message;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return message+" "+senderID+" "+createdAt;
    }
}