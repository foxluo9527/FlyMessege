package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.FriendsBean;

import java.util.List;

public class FriendGetModel extends Base {

    private List<FriendsBean> friends;

    public List<FriendsBean> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendsBean> friends) {
        this.friends = friends;
    }


}
