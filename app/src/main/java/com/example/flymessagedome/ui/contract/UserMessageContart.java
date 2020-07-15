package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.User;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.model.Login;

import java.util.ArrayList;

public interface UserMessageContart {
    interface View extends BaseContract.BaseView{
        void initData();
        void initChat(String filePath);
        void initUserMsg();
        void initDataFailed();
        void initMessage(ArrayList<Message> messages);
        void loginFailed(Login login);
        void sendFailed(Long m_id);
        void sendSuccess();
        void initEntryTextToView(String text);
        void initBlackList(boolean inBlacklist);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getUserMsg(long userId,boolean reset);
        void getMessages(UserBean user,boolean read);
        void login(String userName, String password);
        void sendUserMessage(String fileUrl, UserBean user, int m_content_type, String content);
        void inserting(UserBean user, String content);
        void getEntryText();
        void initUserChat(UserBean user);
        void delMessage(UserBean userBean,int position);
        void reSendMessage(Message message,UserBean userBean);
        void addBlackList(UserBean userBean);
        void checkBlackList(UserBean userBean);
    }
}
