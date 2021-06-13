package com.example.sharecar;

import java.util.Date;

public class Message {

    private String messageText;
    private long messageTime;
    private User receiver, sender;

    public Message(String messageText, User receiver, User sender) {
        this.messageText = messageText;
        this.receiver = receiver;
        this.sender = sender;

        messageTime = new Date().getTime();
    }

    public Message(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageText='" + messageText + '\'' +
                ", messageTime=" + messageTime +
                ", receiver=" + receiver +
                ", sender=" + sender +
                '}';
    }
}
