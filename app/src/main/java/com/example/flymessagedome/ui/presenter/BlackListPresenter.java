package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.BlackListModel;
import com.example.flymessagedome.ui.contract.BlackListContract;
import com.example.flymessagedome.utils.Constant;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BlackListPresenter extends RxPresenter<BlackListContract.View> implements BlackListContract.Presenter<BlackListContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public BlackListPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getBlackList(int pageSize, int pageIndex) {
        Subscription rxSubscription = flyMessageApi.getBlackList(pageSize, pageIndex).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BlackListModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取黑名单失败，请稍后重试");
                    }

                    @Override
                    public void onNext(BlackListModel blackListModel) {
                        if (blackListModel != null && mView != null && blackListModel.code == Constant.SUCCESS) {
                            mView.initBlackList((ArrayList<BlackListModel.BlackListsBean>) blackListModel.getBlackLists());
                        } else if (blackListModel != null) {
                            mView.showError(blackListModel.msg);
                        } else {
                            mView.showError("获取黑名单失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void removeBlackList(BlackListModel.BlackListsBean blackListsBean) {
        Subscription rxSubscription = flyMessageApi.delBlackList(blackListsBean.getObject_u_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("移除黑名单失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.complete();
                        } else if (base != null) {
                            mView.showError(base.msg);
                        } else {
                            mView.showError("移除黑名单失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
