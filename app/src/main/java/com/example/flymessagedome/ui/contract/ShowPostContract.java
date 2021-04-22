package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.Post;

public interface ShowPostContract {
    interface View extends BaseContract.BaseView{
        void initPost(Post.PostBean postBean);
        void zanPostSuccess(int postId);
        void cancelZanPostSuccess(int postId);
        void zanCommentSuccess(int commentId);
        void cancelZanCommentSuccess(int commentId);
        void commentSuccess();
        void deleteSuccess();
        void deleteCommentSuccess();
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getPost(int postId);
        void zanPost(int postId);
        void cancelZanPost(int postId);
        void zanComment(int commentId);
        void cancelCommentZan(int commentId);
        void comment(String content,int postId);
        void replyComment(String content,int commentId,int replyId);
        void deletePost(int postId);
        void deleteComment(int commentId);
        void deleteReply(int replyId);
    }
}
