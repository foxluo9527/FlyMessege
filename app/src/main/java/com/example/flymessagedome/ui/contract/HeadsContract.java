package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.HeadModel;

import java.util.ArrayList;

public interface HeadsContract {
    interface View extends BaseContract.BaseView{
        void initHeads(ArrayList<HeadModel.HeadsBean> headBeans);
        void initLoginHead(String headUrl);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getHeads(int pageSize,int pageIndex);
        void delHead(int h_id);
        void changeOldHead(HeadModel.HeadsBean headsBean);
    }
}
