package com.grizzly.keepup.chat;

import java.util.Date;

/**
 * Created by kubek on 1/17/18.
 */

public class ChatMessage {

    private String mMessageText;
    private String mMessageUser;
    private Long mMessageTime;

    public ChatMessage(String messageText, String messageUser) {
        this.mMessageText = messageText;
        this.mMessageUser = messageUser;
        mMessageTime = new Date().getTime();
    }

    public ChatMessage() {
    }

    public String getmMessageText() {
        return mMessageText;
    }

    public String getmMessageUser() {
        return mMessageUser;
    }

    public Long getmMessageTime() {
        return mMessageTime;
    }

    public void setmMessageText(String messageText) {
        this.mMessageText = messageText;
    }

    public void setmMessageUser(String messageUser) {
        this.mMessageUser = messageUser;
    }

    public void setmMessageTime(Long messageTime) {
        this.mMessageTime = messageTime;
    }
}
