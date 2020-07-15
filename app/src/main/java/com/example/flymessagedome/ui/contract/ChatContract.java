package com.example.flymessagedome.ui.contract;

import android.content.Context;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.ui.adapter.ChatAdapter;

public interface ChatContract {
    interface View extends BaseContract.BaseView{
        void initChatList(ChatAdapter chatAdapter);
        void loginFailed(Login login);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getChatList(Context context);
        void delChatList(Chat chat,boolean delMessage);
        void upChatList(Chat chat);
        void cancelUpChat(Chat chat);
        void login(String userName, String password);
        void getMessage(int pageSize,int pageNum);
    }
}
