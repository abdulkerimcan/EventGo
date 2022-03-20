package com.furkankerim.eventgo.Models;

public class Message {
    private String recipient,sender,content,type;

    public Message(String recipient, String sender, String content,String type) {
        this.recipient = recipient;
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    public Message() {
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
