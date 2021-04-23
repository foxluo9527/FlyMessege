package com.example.flymessagedome.ui.presenter;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.FriendsBeanDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.contract.SearchFriendContract;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import java.util.ArrayList;

import javax.inject.Inject;

public class SearchFriendPresenter extends RxPresenter<SearchFriendContract.View> implements SearchFriendContract.Presenter<SearchFriendContract.View> {
    FriendsBeanDao friendsBeanDao = FlyMessageApplication.getInstances().getDaoSession().getFriendsBeanDao();
    UserBeanDao userBeanDao = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();

    @Inject
    public SearchFriendPresenter(FlyMessageApi flyMessageApi) {
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getFriendList(String searchString) {
        if (LoginActivity.loginUser == null) {
            mView.showError("请先登录");
            return;
        }
        new AsyncTask<Void, Void, ArrayList<FriendsBean>>() {
            @Override
            protected ArrayList<FriendsBean> doInBackground(Void... voids) {
                try {
                    ArrayList<FriendsBean> friendsBeans = (ArrayList<FriendsBean>) friendsBeanDao.queryBuilder()
                            .where(FriendsBeanDao.Properties.F_source_u_id.eq(LoginActivity.loginUser.getU_id()))
                            .orderAsc(FriendsBeanDao.Properties.F_remarks_name)
                            .list();
                    for (int i = 0; i < friendsBeans.size(); i++) {
                        FriendsBean f = friendsBeans.get(i);
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
                        if (f.getFriendUser() == null || (!f.getF_remarks_name().contains(searchString) && !f.getFriendUser().getU_name().contains(searchString) && !f.getFriendUser().getU_nick_name().contains(searchString))) {
                            friendsBeans.remove(i);
                            i--;
                        }
                    }
                    return friendsBeans;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ArrayList<FriendsBean> friendsBeans) {
                try {
                    mView.initFriendList(friendsBeans);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
