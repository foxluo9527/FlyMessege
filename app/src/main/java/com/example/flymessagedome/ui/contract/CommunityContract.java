package com.example.flymessagedome.ui.contract;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.PhoneInfo;
import com.example.flymessagedome.model.PostListResult;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.model.Weather;

import java.util.ArrayList;
import java.util.List;

public interface CommunityContract {
    interface View extends BaseContract.BaseView{
        void initWeather(Weather.Newslist weather);
        void addPostSuccess(int postId);
        void initPostList(List<PostListResult.PostsBean> result);
        void addPostList(List<PostListResult.PostsBean> result);
        void zanPostSuccess(int postId);
        void cancelZanPostSuccess(int postId);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getWeather(String cityCode);
        void addPost(String postContent);
        void getPosts(int pageIndex);
        void zanPost(int postId);
        void cancelZanPost(int postId);
    }
}
