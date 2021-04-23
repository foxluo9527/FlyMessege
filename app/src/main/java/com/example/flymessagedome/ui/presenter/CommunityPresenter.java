package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.AddPostResult;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.PostListResult;
import com.example.flymessagedome.model.Weather;
import com.example.flymessagedome.ui.contract.CommunityContract;
import com.example.flymessagedome.utils.Constant;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommunityPresenter extends RxPresenter<CommunityContract.View> implements CommunityContract.Presenter<CommunityContract.View> {
    private final FlyMessageApi flyMessageApi;

    @Inject
    public CommunityPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getWeather(String cityCode) {
        Subscription rxSubscription = flyMessageApi.getWeather("http://api.tianapi.com/txapi/tianqi/index?key=" + Constant.api_key + "&city=" + cityCode).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        if (weather != null && mView != null && weather.getCode() == Constant.SUCCESS && weather.getNewslist().size() > 0) {
                            mView.initWeather(weather.getNewslist().get(0));
                        } else if (mView != null) {
                            mView.showError(weather.getMsg());
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void addPost(String postContent) {
        Subscription rxSubscription = flyMessageApi.addPost(postContent).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddPostResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("发表帖子失败");
                    }

                    @Override
                    public void onNext(AddPostResult result) {
                        if (mView != null) {
                            if (result != null) {
                                if (result.code == Constant.SUCCESS) {
                                    mView.addPostSuccess(result.getPostId());
                                }
                                mView.showError(result.msg);
                            } else {
                                mView.showError("发表帖子失败");
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void getPosts(int pageIndex) {
        Subscription rxSubscription = flyMessageApi.getPosts(10, pageIndex).subscribeOn(Schedulers.io())
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
                                            getPosts(pageIndex + 1);
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
