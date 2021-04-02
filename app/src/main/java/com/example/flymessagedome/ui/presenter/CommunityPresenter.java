package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Weather;
import com.example.flymessagedome.ui.contract.CommunityContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommunityPresenter extends RxPresenter<CommunityContract.View> implements CommunityContract.Presenter<CommunityContract.View> {
    private FlyMessageApi flyMessageApi;

    @Inject
    public CommunityPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getWeather(String cityCode) {
        Subscription rxSubscription = flyMessageApi.getWeather("http://api.tianapi.com/txapi/tianqi/index?key=" + Constant.api_key + "&city=" + cityCode).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        if (weather != null && mView != null && weather.getCode() == Constant.SUCCESS && weather.getNewslist().size() > 0) {
                            mView.initWeather(weather.getNewslist().get(0));
                        } else if (mView != null) {
                            mView.showError(weather.getMsg());
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
