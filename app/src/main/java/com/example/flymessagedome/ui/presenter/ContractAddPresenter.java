package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.PhoneInfo;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.ui.contract.AddContract;
import com.example.flymessagedome.utils.Constant;
import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContractAddPresenter extends RxPresenter<AddContract.View> implements AddContract.Presenter<AddContract.View>{
    private FlyMessageApi flyMessageApi;
    @Inject
    public ContractAddPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void checkPhone(PhoneInfo info,boolean last) {
        getUser(info);
        if (!last)
            mView.initChecked();
        else
            mView.complete();
    }
    private void getUser(PhoneInfo info){
        Subscription rxSubscription = flyMessageApi.searchUser(info.getPhone(),1,1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchUserModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SearchUserModel searchUserModel) {
                        if (searchUserModel != null && mView != null && searchUserModel.code == Constant.SUCCESS) {
                            if (searchUserModel.getResult().size()>0){
                                mView.initResult(searchUserModel.getResult().get(0),info);
                            }
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
