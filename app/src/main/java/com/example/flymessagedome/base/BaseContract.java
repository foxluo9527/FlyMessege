package com.example.flymessagedome.base;

public interface BaseContract {

    interface BasePresenter<T>{

        void attachView(T view);

        void detachView();

    }

    interface BaseView{

        void showError();
        void showError(String msg);

        void complete();
        void tokenExceed();

    }

}
