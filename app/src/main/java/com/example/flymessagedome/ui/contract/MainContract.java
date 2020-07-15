package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.service.MessageService;

public interface MainContract {
    interface View extends BaseContract.BaseView{
        void reConnectFailed(Login login);
        void initGroupMsg(GroupBean group);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void reConnectService(MessageService.MessageServiceBinder serviceBinder);
        void getGroupMsg(int g_id);
    }

}
