package com.example.flymessagedome.bean.socketData;

import com.example.flymessagedome.bean.FriendRequest;

public class SocketFriendRequest extends BaseSocketData{
    public FriendRequest content;

    public FriendRequest getContent() {
        return content;
    }

    public void setContent(FriendRequest content) {
        this.content = content;
    }
}
