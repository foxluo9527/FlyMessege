package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.Message;

import java.util.ArrayList;

public interface SearchRecordContract {
    interface View extends BaseContract.BaseView{
        void initMessageList(ArrayList<ArrayList<Message>> groupMessages, ArrayList<Chat> chats);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getMessageList(String searchString);
    }
}
