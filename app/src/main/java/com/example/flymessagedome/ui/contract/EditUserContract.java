package com.example.flymessagedome.ui.contract;

import android.content.Context;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.UserBean;

public interface EditUserContract {
    interface View extends BaseContract.BaseView{

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void updateUserMsg(UserBean userBean);
    }
}
