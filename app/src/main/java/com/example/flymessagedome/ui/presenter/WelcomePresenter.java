package com.example.flymessagedome.ui.presenter;

import android.text.TextUtils;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.One;
import com.example.flymessagedome.ui.contract.WelcomeContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WelcomePresenter extends RxPresenter<WelcomeContract.View> implements WelcomeContract.Presenter<WelcomeContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public WelcomePresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getOneData() {
        Subscription rxSubscription = flyMessageApi.getOne(Constant.ONE_API + "?key=92fa8d59057fdd134be4cf4a4db4e9da").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<One>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError();
                    }

                    @Override
                    public void onNext(One one) {
                        if (one != null && mView != null && one.code == Constant.SUCCESS) {
                            mView.showOne(one.getNewslist().get(0));
                        } else {
                            if (one != null && !TextUtils.isEmpty(one.msg)) {
                                mView.showError(one.msg);
                            } else {
                                mView.showError();
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
