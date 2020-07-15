package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.FriendRequest;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.model.FriendRequestModel;
import com.example.flymessagedome.model.Login;

import java.util.ArrayList;

public interface FriendRequestContract {
    interface View extends BaseContract.BaseView{
        void initFriendRequest(ArrayList<FriendRequestModel.FriendRequestsBean> frq_list);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void delFriendRequest(FriendRequestModel.FriendRequestsBean fr);
        void agreeFriendRequest(FriendRequestModel.FriendRequestsBean fr);
        void getFriendRequests(int pageSize,int pageIndex);
    }
}
