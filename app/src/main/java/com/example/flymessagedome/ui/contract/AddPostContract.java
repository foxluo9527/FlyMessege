package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;

public interface AddPostContract {
    interface View extends BaseContract.BaseView {
        void addSuccess(int postId);

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void addPost(String content);
    }
}
