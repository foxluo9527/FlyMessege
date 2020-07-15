package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.model.Login;

import java.util.ArrayList;

public interface GroupMessageContract {
    interface View extends BaseContract.BaseView{
        void initData();
        void initGroupMsg();
        void initChat(String filePath);
        void initDataFailed();
        void initMessage(ArrayList<Message> messages);
        void sendFailed(Long m_id);
        void sendSuccess();
        void initEntryTextToView(String text);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getGroupMsg(long groupId,boolean reset);
        void getMessages(GroupBean group, boolean read);
        void sendUserMessage(String fileUrl,GroupBean group, int m_content_type, String content);
        void inserting(GroupBean group, String content);
        void getEntryText();
        void initUserChat(GroupBean group);
        void delMessage(GroupBean group,int position);
        void reSendMessage(Message message,GroupBean group);
    }
}
