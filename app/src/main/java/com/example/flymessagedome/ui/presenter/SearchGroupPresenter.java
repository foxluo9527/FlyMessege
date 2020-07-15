package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.model.GroupListModel;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.ui.contract.SearchGroupContract;
import com.example.flymessagedome.utils.Constant;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchGroupPresenter extends RxPresenter<SearchGroupContract.View> implements SearchGroupContract.Presenter<SearchGroupContract.View>{
    private FlyMessageApi flyMessageApi;
    ArrayList<GroupBean> groupBeans=new ArrayList<>();
    @Inject
    public SearchGroupPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }
    @Override
    public void search(String content, int pageSize, int pageNum) {
        Subscription rxSubscription = flyMessageApi.queryGroup(content,pageSize,pageNum).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GroupListModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取数据失败，请稍后重试");
                    }

                    @Override
                    public void onNext(GroupListModel groupsModel) {
                        if (groupsModel != null && mView != null && groupsModel.code == Constant.SUCCESS) {
                            groupBeans= (ArrayList<GroupBean>) groupsModel.getGroups();
                            mView.initSearchResult(groupBeans);
                        }else if (groupsModel!=null){
                            mView.showError(groupsModel.msg);
                        }else {
                            mView.showError("获取数据失败，请稍后重试");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
