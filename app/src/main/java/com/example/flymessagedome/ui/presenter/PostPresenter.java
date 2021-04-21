package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Post;
import com.example.flymessagedome.ui.contract.ShowPostContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostPresenter extends RxPresenter<ShowPostContract.View> implements ShowPostContract.Presenter<ShowPostContract.View> {
    private FlyMessageApi flyMessageApi;

    @Inject
    public PostPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getPost(int postId) {
        Subscription rxSubscription = flyMessageApi.getPost(postId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("取消点赞帖子失败");
                    }

                    @Override
                    public void onNext(Post result) {
                        if (mView != null) {
                            if (result != null) {
                                if (result.code == Constant.SUCCESS) {
                                    mView.initPost(result.getPost());
                                } else
                                    mView.showError(result.msg);
                            } else {
                                mView.showError("取消点赞帖子失败");
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
