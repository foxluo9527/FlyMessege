package com.example.flymessagedome.ui.contract;

import android.content.Context;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.BlackListModel;

import java.util.ArrayList;

public interface BlackListContract {
    interface View extends BaseContract.BaseView{
        void initBlackList(ArrayList<BlackListModel.BlackListsBean> blackListsBeans);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getBlackList(int pageSize,int pageIndex);
        void removeBlackList(BlackListModel.BlackListsBean blackListsBean);
    }
}
