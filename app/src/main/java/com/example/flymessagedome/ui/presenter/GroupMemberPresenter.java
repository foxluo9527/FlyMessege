package com.example.flymessagedome.ui.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.GroupMemberDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.GroupMembersModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.contract.GroupMembersContract;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupMemberPresenter extends RxPresenter<GroupMembersContract.View> implements GroupMembersContract.Presenter<GroupMembersContract.View>{
    private FlyMessageApi flyMessageApi;
    String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
    GroupMemberDao groupMemberDao= FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao();
    ArrayList<GroupMember> groupMembers=new ArrayList<>();
    GroupMember creator;
    int g_id;
    @Inject
    public GroupMemberPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getGroupMembers(long g_id, Context context) {
        this.g_id=Integer.parseInt(String.valueOf(g_id));
        if (NetworkUtils.isConnected(context)){
            getGroupMembers(20,1);
        }else {
            groupMembers= (ArrayList<GroupMember>) groupMemberDao.queryBuilder()
                    .where(GroupMemberDao.Properties.G_id.eq(g_id))
                    .list();
            creator=groupMemberDao.queryBuilder().where(GroupMemberDao.Properties.G_id.eq(g_id))
                    .where(GroupMemberDao.Properties.Power.eq(1)).unique();
            mView.initGroupMember(groupMembers,creator);
        }
    }

    @Override
    public void delGroupMember(ArrayList<GroupMember> groupMembers) {
        int sum=groupMembers.size();
        int now=1;
        for (GroupMember groupMember:groupMembers) {
            delGroupMember(now,sum,groupMember);
            now++;
        }
    }

    private void delGroupMember(int now,int sum,GroupMember groupMember) {
        Subscription rxSubscription = flyMessageApi.delGroupMember(groupMember.getG_id(),Integer.parseInt(String.valueOf(groupMember.getU_id())),loginToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("删除群用户失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.initDelGroupMember(now, sum);
                            if (now==sum){
                                groupMembers.clear();
                            }
                            groupMemberDao.delete(groupMember);
                        }else if(base!=null){
                            mView.showError(base.msg);
                        }else {
                            mView.showError("删除群用户失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    private void getGroupMembers(int pageNum, int pageIndex) {
        Subscription rxSubscription = flyMessageApi.getGroupMembers(g_id,pageNum,pageIndex).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GroupMembersModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取群成员失败，请稍后重试");
                    }

                    @Override
                    public void onNext(GroupMembersModel groupMembersModel) {
                        if (groupMembersModel != null && mView != null && groupMembersModel.code == Constant.SUCCESS) {
                            for (GroupMember c:groupMembersModel.getGroup_members()) {
                                if (c.getPower()==1){
                                    creator=c;
                                }
                                groupMemberDao.insertOrReplace(c);
                                if (FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().queryBuilder()
                                .where(UserBeanDao.Properties.U_id.eq((long)c.getU_id())).unique()==null){
                                    new AsyncTask<Void,Void, Users>(){

                                        @Override
                                        protected Users doInBackground(Void... voids) {
                                            return HttpRequest.getUserMsg(Integer.parseInt(String.valueOf(c.getU_id())),loginToken);
                                        }

                                        @Override
                                        protected void onPostExecute(Users users) {
                                            super.onPostExecute(users);
                                            FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().insertOrReplace(users.getUser());
                                        }
                                    }.execute();
                                }
                            }
                            groupMembers.addAll(groupMembersModel.getGroup_members());
                            if (groupMembersModel.getGroup_members().size()==20){
                                getGroupMembers(pageNum,pageIndex+1);
                            }else {
                                mView.initGroupMember(groupMembers,creator);
                            }
                        }else if(groupMembersModel!=null){
                            mView.showError(groupMembersModel.msg);
                        }else {
                            mView.showError("获取群成员失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
