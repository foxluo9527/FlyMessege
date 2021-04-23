package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.contract.SearchContract;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchPresenter extends RxPresenter<SearchContract.View> implements SearchContract.Presenter<SearchContract.View> {
    private final FlyMessageApi flyMessageApi;
    ArrayList<SearchUserModel.ResultBean> searchUsers;

    @Inject
    public SearchPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void login(String userName, String password) {
        Subscription rxSubscription = flyMessageApi.getLogin(userName, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFailed(null);
                    }

                    @Override
                    public void onNext(Login login) {
                        if (login != null && mView != null && login.code == Constant.SUCCESS) {
                            SharedPreferencesUtil.getInstance().putString(Constant.U_NAME, userName);
                            SharedPreferencesUtil.getInstance().putString(Constant.U_PASS, password);
                            SharedPreferencesUtil.getInstance().putString(Constant.LOGIN_TOKEN, login.getToken());
                            MainActivity.serviceBinder.connect();
                        } else {
                            mView.loginFailed(login);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void search(String content, int pageSize, int pageNum) {
        searchUsers = new ArrayList<>();
        Subscription rxSubscription = flyMessageApi.searchUser(content, pageSize, pageNum).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchUserModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取数据失败，请稍后重试");
                    }

                    @Override
                    public void onNext(SearchUserModel userModel) {
                        if (userModel != null && mView != null && userModel.code == Constant.SUCCESS) {
                            searchUsers = (ArrayList<SearchUserModel.ResultBean>) userModel.getResult();
                            mView.initSearchResult(searchUsers);
                        } else if (userModel != null) {
                            mView.showError(userModel.msg);
                        } else {
                            mView.showError("获取数据失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
