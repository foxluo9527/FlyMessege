package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;

import java.util.ArrayList;

public interface CreateGroupContract {
    interface View extends BaseContract.BaseView{
        void createSuccess(int g_id);
        void headSuccess();
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void createGroup(String g_name,String g_introduce);
        void changeGroupHead(int g_id,String filePath);
    }
}
