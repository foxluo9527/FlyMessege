package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupBeanDao;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.ChangeGroupHeadModel;
import com.example.flymessagedome.model.CreateGroupResult;
import com.example.flymessagedome.ui.contract.EditGroupContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditGroupPresenter extends RxPresenter<EditGroupContract.View> implements EditGroupContract.Presenter<EditGroupContract.View>{
    private FlyMessageApi flyMessageApi;
    @Inject
    public EditGroupPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void editGroup(GroupBean groupBean) {
        Subscription rxSubscription = flyMessageApi.updateGroupMsg(groupBean.getG_id(),groupBean.getG_name(),groupBean.getG_introduce()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("修改群聊信息失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.editSuccess();
                        }else if(base!=null){
                            mView.showError(base.msg);
                        }else {
                            mView.showError("修改群聊信息失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void changeGroupHead(int g_id, String filePath) {
        Subscription rxSubscription = flyMessageApi.changeGroupHead(g_id,filePath).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChangeGroupHeadModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("修改群聊头像失败，请稍后重试");
                    }

                    @Override
                    public void onNext(ChangeGroupHeadModel base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            if(base.getHeadUrl()==null){
                                mView.headSuccess(filePath);
                            }else
                            mView.headSuccess(base.getHeadUrl());
                        }else if(base!=null){
                            mView.showError(base.msg);
                        }else {
                            mView.showError("修改群聊头像失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
