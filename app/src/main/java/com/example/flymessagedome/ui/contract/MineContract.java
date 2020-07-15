package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;

import java.util.ArrayList;

public interface MineContract {
    interface View extends BaseContract.BaseView{
        void initHeadImg(String headUrl);
        void initBgImg(String bgUrl);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void changeHead(String filePath);
        void changeBgImg(String filePath);
    }
}
