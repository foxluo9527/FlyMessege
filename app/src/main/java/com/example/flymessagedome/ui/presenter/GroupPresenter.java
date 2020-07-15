package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupBeanDao;
import com.example.flymessagedome.model.BlackListModel;
import com.example.flymessagedome.model.GroupListModel;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.contract.GroupContract;
import com.example.flymessagedome.utils.Constant;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupPresenter extends RxPresenter<GroupContract.View> implements GroupContract.Presenter<GroupContract.View>{
    private FlyMessageApi flyMessageApi;
    GroupBeanDao groupBeanDao= FlyMessageApplication.getInstances().getDaoSession().getGroupBeanDao();
    @Inject
    public GroupPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
        getGroupsByPage(20,1);
    }
    @Override
    public void getGroups() {
        ArrayList<ArrayList<GroupBean>> groups=new ArrayList<>();
        ArrayList<GroupBean> myGroupBeans= (ArrayList<GroupBean>) groupBeanDao.queryBuilder()
                .where(GroupBeanDao.Properties.Login_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(GroupBeanDao.Properties.IsCreater.eq(true))
                .list();
        ArrayList<GroupBean> joinGroups=(ArrayList<GroupBean>) groupBeanDao.queryBuilder()
                .where(GroupBeanDao.Properties.Login_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(GroupBeanDao.Properties.IsCreater.eq(false))
                .list();
        groups.add(myGroupBeans);
        groups.add(joinGroups);
        mView.initGroups(groups);
    }
    public void getGroupsByPage(int pageSize, int position){
        Subscription rxSubscription = flyMessageApi.getUserGroups(pageSize,position).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GroupListModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取群聊失败，请稍后重试");
                    }

                    @Override
                    public void onNext(GroupListModel groupListModel) {
                        if (groupListModel != null && mView != null && groupListModel.code == Constant.SUCCESS) {
                            if (position==1){
                                groupBeanDao.queryBuilder().where(GroupBeanDao.Properties.Login_u_id.eq(LoginActivity.loginUser.getU_id()))
                                        .buildDelete()
                                        .executeDeleteWithoutDetachingEntities();
                            }
                            ArrayList<GroupBean> groupBeans= (ArrayList<GroupBean>) groupListModel.getGroups();
                            for (GroupBean group: groupBeans) {
                                group.setLogin_u_id(LoginActivity.loginUser.getU_id());
                                groupBeanDao.insertOrReplace(group);
                            }
                            if (groupBeans.size()==pageSize){
                                getGroupsByPage(pageSize,position+1);
                            }else {
                                getGroups();
                            }
                        }else if(groupListModel!=null){
                            mView.showError(groupListModel.msg);
                        }else {
                            mView.showError("获取群聊失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
