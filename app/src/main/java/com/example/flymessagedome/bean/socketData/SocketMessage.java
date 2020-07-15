package com.example.flymessagedome.bean.socketData;

import com.example.flymessagedome.bean.Message;

public class SocketMessage extends BaseSocketData {
    public Message content;

    public Message getContent() {
        return content;
    }

    public void setContent(Message content) {
        this.content = content;
    }
}
