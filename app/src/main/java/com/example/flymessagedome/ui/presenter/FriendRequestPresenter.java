package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.FriendRequestModel;
import com.example.flymessagedome.ui.contract.FriendRequestContract;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FriendRequestPresenter extends RxPresenter<FriendRequestContract.View> implements FriendRequestContract.Presenter<FriendRequestContract.View> {
    ArrayList<FriendRequestModel.FriendRequestsBean> friendRequests;
    private final FlyMessageApi flyMessageApi;

    @Inject
    public FriendRequestPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void delFriendRequest(FriendRequestModel.FriendRequestsBean friendRequestsBean) {
        Subscription rxSubscription = flyMessageApi.delFriendRequest(friendRequestsBean.getRq_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("删除好友申请失败");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            getFriendRequests(20, 1);
                            mView.showError("删除好友申请成功");
                            friendRequests.remove(friendRequestsBean);
                            mView.initFriendRequest(friendRequests);
                        } else if (base != null) {
                            mView.showError(base.msg);
                        } else {
                            mView.showError("删除好友申请失败");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void agreeFriendRequest(FriendRequestModel.FriendRequestsBean frBean) {
        Subscription rxSubscription = flyMessageApi.acceptFriendRequest(frBean.getRq_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("通过好友申请失败");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.showError("通过好友申请成功");

                            for (FriendRequestModel.FriendRequestsBean fr :
                                    friendRequests) {
                                if (fr.getRq_source_u_id() == frBean.getRq_source_u_id()) {
                                    delFriendRequest(fr);
                                }
                            }

                            for (int i = 0; i < friendRequests.size(); i++) {
                                if (friendRequests.get(i).getRq_source_u_id() == frBean.getRq_source_u_id()) {
                                    friendRequests.remove(friendRequests.get(i));
                                    i--;
                                }
                            }

                            mView.initFriendRequest(friendRequests);
                        } else if (base != null) {
                            mView.showError(base.msg);
                        } else {
                            mView.showError("通过好友申请失败");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void getFriendRequests(int pageSize, int pageIndex) {
        if (pageIndex == 1)
            friendRequests = new ArrayList<>();
        Subscription rxSubscription = flyMessageApi.getFriendRequests(pageSize, pageIndex).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendRequestModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.initFriendRequest(null);
                    }

                    @Override
                    public void onNext(FriendRequestModel friendRequestModel) {
                        if (friendRequestModel != null && mView != null) {
                            switch (friendRequestModel.code) {
                                case Constant.FAILED:
                                    mView.showError(friendRequestModel.msg);
                                    break;
                                case Constant.NOT_LOGIN:
                                case Constant.TOKEN_EXCEED:
                                    String u_name = SharedPreferencesUtil.getInstance().getString(Constant.U_NAME);
                                    String u_pass = SharedPreferencesUtil.getInstance().getString(Constant.U_PASS);
                                    mView.initFriendRequest(null);
                                    if (u_name == null || u_pass == null) {
                                        friendRequests = null;
                                    }
                                    break;
                                case Constant.SUCCESS:
                                    friendRequests.addAll(friendRequestModel.getFriendRequests());
                                    if (friendRequests != null && friendRequests.size() == 20) {
                                        getFriendRequests(20, pageIndex + 1);
                                    } else {
                                        mView.initFriendRequest(friendRequests);
                                    }
                                    break;
                            }
                        } else {
                            mView.initFriendRequest(null);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
