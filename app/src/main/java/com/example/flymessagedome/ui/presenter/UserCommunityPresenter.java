package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.PostListResult;
import com.example.flymessagedome.ui.contract.UserCommunityContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserCommunityPresenter extends RxPresenter<UserCommunityContract.View> implements UserCommunityContract.Presenter<UserCommunityContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public UserCommunityPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getPosts(int userId, int pageIndex) {
        Subscription rxSubscription = flyMessageApi.getUserPosts(userId, 10, pageIndex).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PostListResult>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取社区帖子失败");
                    }

                    @Override
                    public void onNext(PostListResult result) {
                        if (mView != null) {
                            if (result != null) {
                                if (result.code == Constant.SUCCESS) {
                                    if (pageIndex == 1) {
                                        mView.initPostList(result.getPosts());
                                        if (result.getPosts().size() == 10)
                                            getPosts(userId, pageIndex + 1);
                                    } else {
                                        mView.addPostList(result.getPosts());
                                    }
                                } else
                                    mView.showError(result.msg);
                            } else {
                                mView.showError("获取社区帖子失败");
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void zanPost(int postId) {
        Subscription rxSubscription = flyMessageApi.zanPost(postId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("点赞帖子失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null) {
                            if (result != null) {
                                if (result.code == Constant.SUCCESS) {
                                    mView.zanPostSuccess(postId);
                                } else
                                    mView.showError(result.msg);
                            } else {
                                mView.showError("点赞帖子失败");
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void cancelZanPost(int postId) {
        Subscription rxSubscription = flyMessageApi.cancelZanPost(postId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
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
                    public void onNext(Base result) {
                        if (mView != null) {
                            if (result != null) {
                                if (result.code == Constant.SUCCESS) {
                                    mView.cancelZanPostSuccess(postId);
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
