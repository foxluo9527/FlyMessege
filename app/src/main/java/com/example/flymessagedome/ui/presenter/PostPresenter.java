package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取帖子信息失败");
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
                                mView.showError("获取帖子信息失败");
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

    @Override
    public void zanComment(int commentId) {
        Subscription rxSubscription = flyMessageApi.zanComment(commentId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("点赞评论失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null) {
                            if (result != null) {
                                if (result.code == Constant.SUCCESS) {
                                    mView.zanCommentSuccess(commentId);
                                } else
                                    mView.showError(result.msg);
                            } else {
                                mView.showError("点赞评论失败");
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void cancelCommentZan(int commentId) {
        Subscription rxSubscription = flyMessageApi.cancelZanComment(commentId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("取消点赞评论失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null) {
                            if (result != null) {
                                if (result.code == Constant.SUCCESS) {
                                    mView.cancelZanCommentSuccess(commentId);
                                } else
                                    mView.showError(result.msg);
                            } else {
                                mView.showError("取消点赞评论失败");
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void comment(String content, int postId) {
        Subscription rxSubscription = flyMessageApi.addPostComment(postId, content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("评论帖子失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null && result != null) {
                            if (result.code == Constant.SUCCESS) {
                                mView.commentSuccess();
                            } else
                                mView.showError(result.msg);
                        }
                    }
                });

        addSubscribe(rxSubscription);
    }

    @Override
    public void replyComment(String content, int commentId, int replyId) {
        Subscription rxSubscription = flyMessageApi.replyPostComment(commentId, replyId, content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("回复评论失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null && result != null) {
                            if (result.code == Constant.SUCCESS) {
                                mView.commentSuccess();
                            } else
                                mView.showError(result.msg);
                        }
                    }
                });

        addSubscribe(rxSubscription);
    }

    @Override
    public void deletePost(int postId) {
        Subscription rxSubscription = flyMessageApi.delPost(postId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("删除帖子失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null && result != null) {
                            if (result.code == Constant.SUCCESS) {
                                mView.deleteSuccess();
                            }
                            mView.showError(result.msg);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void deleteComment(int commentId) {
        Subscription rxSubscription = flyMessageApi.delPostComment(commentId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("删除评论失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null && result != null) {
                            if (result.code == Constant.SUCCESS) {
                                mView.deleteCommentSuccess();
                            }
                            mView.showError(result.msg);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void deleteReply(int replyId) {
        Subscription rxSubscription = flyMessageApi.delCommentReply(replyId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("删除评论回复失败");
                    }

                    @Override
                    public void onNext(Base result) {
                        if (mView != null && result != null) {
                            if (result.code == Constant.SUCCESS) {
                                mView.deleteCommentSuccess();
                            }
                            mView.showError(result.msg);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
