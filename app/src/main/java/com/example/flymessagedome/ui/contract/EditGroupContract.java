package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.GroupBean;

public interface EditGroupContract {
    interface View extends BaseContract.BaseView{
        void editSuccess();
        void headSuccess(String head);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void editGroup(GroupBean groupBean);
        void changeGroupHead(int g_id,String filePath);
    }
}
