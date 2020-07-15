package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.FriendRequest;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.model.Login;

import java.util.ArrayList;

public interface FriendContract {
    interface View extends BaseContract.BaseView{
        void initFriendList(ArrayList<FriendsBean> friendsBeans);
        void loginFailed(Login login);
        void initFriendRequest(ArrayList<FriendRequest> frq_list);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getFriendList();
        void getFriends(int pageSize,int pageIndex);
        void login(String userName, String password);
        void delFriend(FriendsBean friendsBeans);
        void refreshFriendRequests(int pageSize,int pageIndex);
        FriendsBean getFriendBean(int position);
    }
}
