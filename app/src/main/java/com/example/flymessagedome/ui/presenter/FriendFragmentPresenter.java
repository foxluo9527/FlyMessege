package com.example.flymessagedome.ui.presenter;

import android.os.AsyncTask;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.FriendRequest;
import com.example.flymessagedome.bean.FriendRequestDao;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.FriendsBeanDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.FriendGetModel;
import com.example.flymessagedome.model.FriendRequestModel;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.contract.FriendContract;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FriendFragmentPresenter extends RxPresenter<FriendContract.View> implements FriendContract.Presenter<FriendContract.View> {
    private FlyMessageApi flyMessageApi;
    ArrayList<FriendsBean> friendsBeans;
    ArrayList<FriendRequest> friendRequests;
    FriendsBeanDao friendsBeanDao = FlyMessageApplication.getInstances().getDaoSession().getFriendsBeanDao();
    FriendRequestDao friendRequestDao = FlyMessageApplication.getInstances().getDaoSession().getFriendRequestDao();
    UserBeanDao userBeanDao = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();
    int nowPage = 1;

    @Inject
    public FriendFragmentPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getFriendList() {
        if (LoginActivity.loginUser == null) {
            mView.loginFailed(null);
            return;
        }
        friendsBeans = (ArrayList<FriendsBean>) friendsBeanDao.queryBuilder()
                .where(FriendsBeanDao.Properties.F_source_u_id.eq(LoginActivity.loginUser.getU_id()))
                .orderAsc(FriendsBeanDao.Properties.F_remarks_name)
                .list();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (FriendsBean f :
                        friendsBeans) {
                    if (f.getFriendUser() == null) {
                        UserBean user = userBeanDao.load((long) f.getF_object_u_id());
                        if (user == null) {
                            user = HttpRequest.getUserMsg((int) f.getF_object_u_id(), SharedPreferencesUtil.getInstance().getString("loginToken")).getUser();
                            user.setF_id(f.getF_id());
                            userBeanDao.insertOrReplace(user);
                        }
                        f.setFriendUser(user);
                        friendsBeanDao.update(f);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mView.initFriendList(friendsBeans);
            }
        }.execute();
        mView.initFriendList(friendsBeans);
    }

    @Override
    public void getFriends(int pageSize, int pageIndex) {
        Subscription rxSubscription = flyMessageApi.getFriends(20, nowPage).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendGetModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFailed(null);
                    }

                    @Override
                    public void onNext(FriendGetModel friendGetModel) {
                        if (friendGetModel != null && mView != null) {
                            switch (friendGetModel.code) {
                                case Constant.FAILED:
                                    mView.showError(friendGetModel.msg);
                                    break;
                                case Constant.NOT_LOGIN:
                                case Constant.TOKEN_EXCEED:
                                    String u_name = SharedPreferencesUtil.getInstance().getString(Constant.U_NAME);
                                    String u_pass = SharedPreferencesUtil.getInstance().getString(Constant.U_PASS);
                                    if (u_name != null && u_pass != null) {
                                        login(u_name, u_pass);
                                    } else {
                                        mView.loginFailed(null);
                                        friendsBeans = null;
                                    }
                                    break;
                                case Constant.SUCCESS:
                                    friendsBeanDao.queryBuilder()
                                            .where(FriendsBeanDao.Properties.F_source_u_id.eq(LoginActivity.loginUser.getU_id()))
                                            .buildDelete()
                                            .executeDeleteWithoutDetachingEntities();
                                    friendsBeans = (ArrayList<FriendsBean>) friendGetModel.getFriends();
                                    for (FriendsBean f :
                                            friendsBeans) {
                                        f.__setDaoSession(FlyMessageApplication.getInstances().getDaoSession());
                                        friendsBeanDao.insertOrReplace(f);
                                    }
                                    friendsBeans = (ArrayList<FriendsBean>) friendsBeanDao.queryBuilder().where(FriendsBeanDao.Properties.F_source_u_id.eq(LoginActivity.loginUser.getU_id())).list();
                                    if (friendsBeans != null && friendsBeans.size() == 20) {
                                        getFriends(20, pageIndex + 1);
                                    } else {
                                        getFriendList();
                                    }
                                    break;
                            }
                        } else {
                            mView.loginFailed(null);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void login(String userName, String password) {
        Subscription rxSubscription = flyMessageApi.getLogin(userName, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFailed(null);
                    }

                    @Override
                    public void onNext(Login login) {
                        if (login != null && mView != null && login.code == Constant.SUCCESS) {
                            SharedPreferencesUtil.getInstance().putString(Constant.U_NAME, userName);
                            SharedPreferencesUtil.getInstance().putString(Constant.U_PASS, password);
                            SharedPreferencesUtil.getInstance().putString(Constant.LOGIN_TOKEN, login.getToken());
                            MainActivity.serviceBinder.connect();
                        } else {
                            mView.loginFailed(login);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void delFriend(FriendsBean friendsBean) {
        Subscription rxSubscription = flyMessageApi.delFriend(friendsBean.getF_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFailed(null);
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            friendsBeanDao.delete(friendsBean);
                            ToastUtils.showToast("删除好友成功");
                            getFriendList();
                        } else if (base != null && base.code == Constant.FAILED) {
                            mView.showError(base.msg);
                        } else {
                            mView.loginFailed(null);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void refreshFriendRequests(int pageSize, int pageIndex) {
        if (pageIndex == 1)
            friendRequests = new ArrayList<>();
        Subscription rxSubscription = flyMessageApi.getFriendRequests(pageSize, pageIndex).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendRequestModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFailed(null);
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
                                    if (u_name != null && u_pass != null) {
                                        login(u_name, u_pass);
                                    } else {
                                        mView.loginFailed(null);
                                        friendRequests = null;
                                    }
                                    break;
                                case Constant.SUCCESS:
                                    ArrayList<FriendRequestModel.FriendRequestsBean> friendRequestsBeans = (ArrayList<FriendRequestModel.FriendRequestsBean>) friendRequestModel.getFriendRequests();
                                    for (FriendRequestModel.FriendRequestsBean f :
                                            friendRequestsBeans) {
                                        friendRequests.add(new FriendRequest(f.getRq_id(), f.getRq_object_u_id(), f.getRq_source_u_id(), f.isIsAccept(), f.getRq_remarks_name(), f.getRq_content(), f.getRq_receive_state(), f.getTime()));
                                    }
                                    if (friendRequests != null && friendRequests.size() == 20) {
                                        refreshFriendRequests(20, pageIndex + 1);
                                    } else {
                                        mView.initFriendRequest(friendRequests);
                                    }
                                    break;
                            }
                        } else {
                            mView.loginFailed(null);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public FriendsBean getFriendBean(int position) {
        if (friendsBeans != null) {
            friendsBeans = (ArrayList<FriendsBean>) friendsBeanDao.queryBuilder()
                    .where(FriendsBeanDao.Properties.F_source_u_id.eq(LoginActivity.loginUser.getU_id()))
                    .orderAsc(FriendsBeanDao.Properties.F_remarks_name)
                    .list();

        }
        if (friendsBeans == null || friendsBeans.size() == 0)
            return null;
        return friendsBeans.get(position);
    }
}
