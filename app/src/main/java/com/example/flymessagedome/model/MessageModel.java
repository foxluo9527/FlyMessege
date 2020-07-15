package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.Message;

import java.util.List;

public class MessageModel extends Base {

    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
