package com.example.flymessagedome.ui.contract;

import android.content.Context;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.Message;

import java.util.ArrayList;

public interface MessageRecordContract {
    interface View extends BaseContract.BaseView{
        void initMessages(ArrayList<Message> messages);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getMessages(int pageSize,int pageIndex,long object_u_id);
        void delMessage(Message message);
    }
}
