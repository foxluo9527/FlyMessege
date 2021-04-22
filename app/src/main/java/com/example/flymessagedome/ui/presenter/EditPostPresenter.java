package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.ui.contract.EditPostContract;
import com.example.flymessagedome.utils.Constant;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditPostPresenter extends RxPresenter<EditPostContract.View> implements EditPostContract.Presenter<EditPostContract.View> {
    private FlyMessageApi flyMessageApi;
    private ArrayList<Integer> removeItemIds;
    private int postId;

    @Inject
    public EditPostPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    public void removeItem(int itemId) {
        Subscription rxSubscription = flyMessageApi.delPostItem(postId, itemId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.removeFailed();
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null && result != null) {
                            if (result.code == Constant.SUCCESS) {
                                if (removeItemIds.size() > 0) {
                                    removeItem(removeItemIds.remove(0));
                                } else {
                                    mView.removeSuccess();
                                }
                            } else
                                mView.removeFailed();
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void removeItems(ArrayList<Integer> removeItemIds) {
        this.removeItemIds = removeItemIds;
        removeItem(removeItemIds.remove(0));
    }

    @Override
    public void editContent(int postId, String content) {
        this.postId = postId;
        Subscription rxSubscription = flyMessageApi.editPostContent(postId, content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("编辑帖子内容失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null && result != null) {
                            if (result.code == Constant.SUCCESS) {
                                mView.editSuccess();
                            } else
                                mView.showError(result.msg);
                        }
                    }
                });

        addSubscribe(rxSubscription);
    }
}
