package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.BlackListModel;
import com.example.flymessagedome.model.HeadModel;
import com.example.flymessagedome.ui.contract.HeadsContract;
import com.example.flymessagedome.utils.Constant;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HeadsPresenter extends RxPresenter<HeadsContract.View> implements HeadsContract.Presenter<HeadsContract.View>{
    private FlyMessageApi flyMessageApi;
    @Inject
    public HeadsPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }
    @Override
    public void getHeads(int pageSize, int pageIndex) {
        Subscription rxSubscription = flyMessageApi.getUserHeads(pageSize,pageIndex).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HeadModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取用户头像失败，请稍后重试");
                    }

                    @Override
                    public void onNext(HeadModel headModel) {
                        if (headModel != null && mView != null && headModel.code == Constant.SUCCESS) {
                            mView.initHeads((ArrayList<HeadModel.HeadsBean>) headModel.getHeads());
                        }else if(headModel!=null){
                            mView.showError(headModel.msg);
                        }else {
                            mView.showError("获取用户头像失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void delHead(int h_id) {
        Subscription rxSubscription = flyMessageApi.delUserHead(h_id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("删除头像失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.complete();
                        }else if(base!=null){
                            mView.showError(base.msg);
                        }else {
                            mView.showError("删除头像失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void changeOldHead(HeadModel.HeadsBean headsBean) {
        Subscription rxSubscription = flyMessageApi.changeOldHead(headsBean.getH_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("修改头像失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.initLoginHead(headsBean.getHead_img_link());
                        }else if(base!=null){
                            mView.showError(base.msg);
                        }else {
                            mView.showError("修改头像失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
