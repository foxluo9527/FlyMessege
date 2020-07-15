package com.example.flymessagedome.ui.contract;

import android.content.Context;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.model.SearchUserModel;

import java.util.ArrayList;

public interface SearchContract {
    interface View extends BaseContract.BaseView{
        void initSearchResult(ArrayList<SearchUserModel.ResultBean> users);
        void loginFailed(Login login);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void login(String userName, String password);
        void search(String content,int pageSize,int pageNum);
    }
}
