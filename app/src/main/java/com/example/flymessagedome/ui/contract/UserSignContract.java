package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.UserSignModel;

import java.util.ArrayList;

public interface UserSignContract {
    interface View extends BaseContract.BaseView{
        void initUserSign(ArrayList<UserSignModel.SignBean> signBeans);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getUserSign(int pageSize,int pageIndex);
        void delUserSign(UserSignModel.SignBean blackListsBean);
        void addUserSign(String content);
    }
}
