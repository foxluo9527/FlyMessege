package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.Post;

public interface ShowPostContract {
    interface View extends BaseContract.BaseView{
        void initPost(Post.PostBean postBean);
        void zanPostSuccess(int postId);
        void cancelZanPostSuccess(int postId);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getPost(int postId);
        void zanPost(int postId);
        void cancelZanPost(int postId);
        void comment(String content,int postId);
        void replyComment(String content,int commentId,int replyId);
        void deletePost(int postId);
    }
}
