package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.FriendsBean;

import java.util.ArrayList;

public interface SearchFriendContract {
    interface View extends BaseContract.BaseView{
        void initFriendList(ArrayList<FriendsBean> friendsBeans);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getFriendList(String searchString);
    }
}
