package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.PhoneInfo;
import com.example.flymessagedome.model.SearchUserModel;

import java.util.ArrayList;

public interface AddContract {
    interface View extends BaseContract.BaseView{
        void initResult(SearchUserModel.ResultBean resultBean,PhoneInfo info);
        void initChecked();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void checkPhone(PhoneInfo info,boolean last);
    }
}
