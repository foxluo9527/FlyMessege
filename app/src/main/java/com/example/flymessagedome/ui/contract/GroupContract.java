package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.GroupBean;

import java.util.ArrayList;

public interface GroupContract {
    interface View extends BaseContract.BaseView{
        void initGroups(ArrayList<ArrayList<GroupBean>> groupBeans);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getGroups();
    }
}
