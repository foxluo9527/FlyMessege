package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.Login;

public interface LoginContract {

    interface View extends BaseContract.BaseView{
        void loginSuccess(Login login);
        void sendLoginCodeSuccess();
        void sendLoginCodeFailed();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void login(String userName, String password);
        void codeLogin(String phone,String code);
        void sendCode(String phone);
    }

}
