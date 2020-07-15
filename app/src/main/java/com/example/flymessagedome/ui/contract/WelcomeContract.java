package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.One;

public interface WelcomeContract {
    interface View extends BaseContract.BaseView{
        void showOne(One.DataBean data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getOneData();
    }
}
