package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.model.PostListResult;
import com.example.flymessagedome.model.Weather;

import java.util.List;

public interface UserCommunityContract {
    interface View extends BaseContract.BaseView{
        void initPostList(List<PostListResult.PostsBean> result);
        void addPostList(List<PostListResult.PostsBean> result);
        void zanPostSuccess(int postId);
        void cancelZanPostSuccess(int postId);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getPosts(int userId,int pageIndex);
        void zanPost(int postId);
        void cancelZanPost(int postId);
    }
}
