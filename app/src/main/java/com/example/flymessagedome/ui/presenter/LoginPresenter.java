package com.example.flymessagedome.ui.presenter;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.ui.contract.LoginContract;
import com.example.flymessagedome.utils.Base64Util;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPresenter extends RxPresenter<LoginContract.View> implements LoginContract.Presenter<LoginContract.View>{

    private FlyMessageApi flyMessageApi;

    @Inject
    public LoginPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }


    @Override
    public void login(String userName, String password) {
        Subscription rxSubscription = flyMessageApi.getLogin(userName,password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
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
                    public void onNext(Login login) {
                        if (login != null && mView != null && login.code == Constant.SUCCESS) {
                            mView.loginSuccess(login);
                            SharedPreferencesUtil.getInstance().putString(Constant.U_NAME,userName);
                            SharedPreferencesUtil.getInstance().putString(Constant.U_PASS,password);
                            SharedPreferencesUtil.getInstance().putString(Constant.LOGIN_TOKEN,login.getToken());
                        }else {
                            if (login != null&&login.code == Constant.FAILED && !TextUtils.isEmpty(login.msg)){
                                mView.showError(login.msg);
                            }else {
                                mView.showError();
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void codeLogin(String phone, String code) {
        Subscription rxSubscription = flyMessageApi.noPassLogin(phone,code).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError();
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onNext(Login login) {
                        if (login != null && mView != null && login.code == Constant.SUCCESS) {
                            mView.loginSuccess(login);
                            SharedPreferencesUtil.getInstance().putString(Constant.U_NAME,login.loginUser.getU_name());
                            SharedPreferencesUtil.getInstance().putString(Constant.U_PASS, Base64Util.decode(login.loginUser.getU_pass()));
                            SharedPreferencesUtil.getInstance().putString(Constant.LOGIN_TOKEN,login.getToken());
                        }else {
                            if (login != null&& !TextUtils.isEmpty(login.msg)){
                                mView.showError(login.msg);
                            }else {
                                mView.showError();
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void sendCode(String phone) {
        Subscription rxSubscription = flyMessageApi.sendLoginCode(phone).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError();
                        mView.sendLoginCodeFailed();
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.showError(Constant.CODE_SEND_SUCCESS);//这里是发送成功了的
                            mView.sendLoginCodeSuccess();
                        }else {
                            if (base != null&& !TextUtils.isEmpty(base.msg)){
                                mView.showError(base.msg);
                            }else {
                                mView.showError();
                            }
                            mView.sendLoginCodeFailed();
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
