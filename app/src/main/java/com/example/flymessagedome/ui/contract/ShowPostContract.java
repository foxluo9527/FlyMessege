package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.Post;

public interface ShowPostContract {
    interface View extends BaseContract.BaseView{
        void initPost(Post.PostBean postBean);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getPost(int postId);
    }
}
