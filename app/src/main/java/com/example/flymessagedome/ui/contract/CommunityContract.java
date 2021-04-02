package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.PhoneInfo;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.model.Weather;

import java.util.ArrayList;

public interface CommunityContract {
    interface View extends BaseContract.BaseView{
        void initWeather(Weather.Newslist weather);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getWeather(String cityCode);
    }
}
