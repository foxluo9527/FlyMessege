package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.UserSignModel;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.contract.UserSignContract;
import com.example.flymessagedome.utils.Constant;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserSignPresenter extends RxPresenter<UserSignContract.View> implements UserSignContract.Presenter<UserSignContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public UserSignPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getUserSign(int pageSize, int pageIndex) {
        Subscription rxSubscription = flyMessageApi.getUserSigns(pageSize, pageIndex).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserSignModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取用户签名失败，请稍后重试");
                    }

                    @Override
                    public void onNext(UserSignModel userSignModel) {
                        if (userSignModel != null && mView != null && userSignModel.code == Constant.SUCCESS) {
                            mView.initUserSign((ArrayList<UserSignModel.SignBean>) userSignModel.getSign());
                        } else if (userSignModel != null) {
                            mView.showError(userSignModel.msg);
                        } else {
                            mView.showError("获取用户签名失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void delUserSign(UserSignModel.SignBean blackListsBean) {
        Subscription rxSubscription = flyMessageApi.delUserSign(blackListsBean.getS_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("删除用户签名失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.complete();
                        } else if (base != null) {
                            mView.showError(base.msg);
                        } else {
                            mView.showError("删除用户签名失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void addUserSign(String content) {
        Subscription rxSubscription = flyMessageApi.addUserSign(content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("添加用户签名失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            LoginActivity.loginUser.setU_sign(content);
                            mView.complete();
                        } else if (base != null) {
                            mView.showError(base.msg);
                        } else {
                            mView.showError("添加用户签名失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
