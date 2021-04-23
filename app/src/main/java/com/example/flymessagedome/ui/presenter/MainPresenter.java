package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.service.MessageService;
import com.example.flymessagedome.ui.contract.MainContract;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter<MainContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public MainPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void reConnectService(MessageService.MessageServiceBinder serviceBinder) {
        String u_name = SharedPreferencesUtil.getInstance().getString(Constant.U_NAME);
        String u_pass = SharedPreferencesUtil.getInstance().getString(Constant.U_PASS);
        Subscription rxSubscription = flyMessageApi.getLogin(u_name, u_pass).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.reConnectFailed(null);
                    }

                    @Override
                    public void onNext(Login login) {
                        if (login != null && mView != null && login.code == Constant.SUCCESS) {
                            SharedPreferencesUtil.getInstance().putString(Constant.LOGIN_TOKEN, login.getToken());
                            serviceBinder.connect();
                        } else {
                            mView.reConnectFailed(login);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void getGroupMsg(int g_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        Subscription rxSubscription = flyMessageApi.getGroupMsg(g_id, loginToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GroupModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.reConnectFailed(null);
                    }

                    @Override
                    public void onNext(GroupModel groupModel) {
                        if (groupModel != null && mView != null && groupModel.code == Constant.SUCCESS) {
                            mView.initGroupMsg(groupModel.getGroup());
                        } else if (groupModel != null) {
                            mView.showError(groupModel.msg);
                        } else {
                            mView.showError("获取群聊信息失败");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
