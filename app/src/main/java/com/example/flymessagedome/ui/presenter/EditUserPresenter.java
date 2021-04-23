package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.contract.EditUserContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditUserPresenter extends RxPresenter<EditUserContract.View> implements EditUserContract.Presenter<EditUserContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public EditUserPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void updateUserMsg(UserBean userBean) {
        if (userBean != null) {
            Subscription rxSubscription = flyMessageApi.updateUserMsg(userBean.getU_sex(), userBean.getU_nick_name(), userBean.getU_brithday(), userBean.getU_position()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Base>() {
                        @Override
                        public void onCompleted() {
                            mView.complete();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mView.showError("修改信息失败");
                        }

                        @Override
                        public void onNext(Base login) {
                            if (login != null && mView != null && login.code == Constant.SUCCESS) {
                                mView.complete();
                                LoginActivity.loginUser.setU_sex(userBean.getU_sex());
                                LoginActivity.loginUser.setU_nick_name(userBean.getU_nick_name());
                                LoginActivity.loginUser.setU_position(userBean.getU_position());
                                LoginActivity.loginUser.setU_brithday(userBean.getU_brithday());
                                FlyMessageApplication.getInstances().getDaoSession().getUserDao().update(LoginActivity.loginUser);
                            } else {
                                mView.showError(login.msg);
                            }
                        }
                    });
            addSubscribe(rxSubscription);
        } else {
            mView.showError("修改信息失败");
        }
    }

}
