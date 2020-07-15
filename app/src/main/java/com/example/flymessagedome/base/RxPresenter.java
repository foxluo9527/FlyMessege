package com.example.flymessagedome.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Li on 2019/8/26.
 * 基于Rx的Presenter封装,控制订阅的生命周期
 * unsubscribe() 这个方法很重要，
 * 因为在 subscribe() 之后， Observable 会持有 Subscriber 的引用，
 * 这个引用如果不能及时被释放，将有内存泄露的风险。
 */

public class

RxPresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    protected T mView;
    protected CompositeSubscription compositeSubscription;

    protected void unSubscribe(){
        if (compositeSubscription != null){
            compositeSubscription.unsubscribe();
        }
    }

    protected void addSubscribe(Subscription subscription){
        if (compositeSubscription == null){
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(subscription);
    }

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        unSubscribe();
    }
}
