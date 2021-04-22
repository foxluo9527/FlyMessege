package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;

import java.util.ArrayList;

public interface EditPostContract {
    interface View extends BaseContract.BaseView {
        void editSuccess();
        void removeFailed();
        void removeSuccess();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void removeItems(ArrayList<Integer> removeItemIds);

        void editContent(int postId, String content);
    }
}
