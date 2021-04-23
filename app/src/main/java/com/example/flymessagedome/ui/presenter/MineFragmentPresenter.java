package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.ChangeBgModel;
import com.example.flymessagedome.model.ChangeHeadModel;
import com.example.flymessagedome.ui.contract.MineContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MineFragmentPresenter extends RxPresenter<MineContract.View> implements MineContract.Presenter<MineContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public MineFragmentPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void changeHead(String filePath) {
        Subscription rxSubscription = flyMessageApi.changeHead(filePath).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChangeHeadModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("修改头像失败");
                    }

                    @Override
                    public void onNext(ChangeHeadModel changeHeadModel) {
                        if (changeHeadModel != null && mView != null && changeHeadModel.code == Constant.SUCCESS) {
                            mView.initHeadImg(changeHeadModel.getHeadUrl());
                        } else if (changeHeadModel != null) {
                            mView.showError(changeHeadModel.msg);
                        } else {
                            mView.showError("修改头像失败");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void changeBgImg(String filePath) {
        Subscription rxSubscription = flyMessageApi.changeBgImg(filePath).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChangeBgModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("修改名片背景失败");
                    }

                    @Override
                    public void onNext(ChangeBgModel changeHeadModel) {
                        if (changeHeadModel != null && mView != null && changeHeadModel.code == Constant.SUCCESS) {
                            mView.initBgImg(changeHeadModel.getBgUrl());
                        } else if (changeHeadModel != null) {
                            mView.showError(changeHeadModel.msg);
                        } else {
                            mView.showError("修改名片背景失败");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
