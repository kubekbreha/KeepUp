package com.grizzly.keepup.chat;

import java.util.Date;

/**
 * Created by kubek on 1/17/18.
 */

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private Long messageTime;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }
}
