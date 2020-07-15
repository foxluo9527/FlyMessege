package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.GroupBean;

import java.util.ArrayList;

public interface SearchGroupContract {
    interface View extends BaseContract.BaseView{
        void initSearchResult(ArrayList<GroupBean> groupBeans);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void search(String content,int pageSize,int pageNum);
    }
}
