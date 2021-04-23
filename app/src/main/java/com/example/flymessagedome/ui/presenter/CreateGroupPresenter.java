package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.CreateGroupResult;
import com.example.flymessagedome.ui.contract.CreateGroupContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateGroupPresenter extends RxPresenter<CreateGroupContract.View> implements CreateGroupContract.Presenter<CreateGroupContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public CreateGroupPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void createGroup(String g_name, String g_introduce) {
        Subscription rxSubscription = flyMessageApi.createGroup(g_name, g_introduce).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CreateGroupResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("创建群聊失败，请稍后重试");
                    }

                    @Override
                    public void onNext(CreateGroupResult createGroupResult) {
                        if (createGroupResult != null && mView != null && createGroupResult.code == Constant.SUCCESS) {
                            mView.createSuccess(createGroupResult.getG_id());
                        } else if (createGroupResult != null) {
                            mView.showError(createGroupResult.msg);
                        } else {
                            mView.showError("创建群聊失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void changeGroupHead(int g_id, String filePath) {
        Subscription rxSubscription = flyMessageApi.changeGroupHead(g_id, filePath).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("修改群聊头像失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.headSuccess();
                        } else if (base != null) {
                            mView.showError(base.msg);
                        } else {
                            mView.showError("修改群聊头像失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
