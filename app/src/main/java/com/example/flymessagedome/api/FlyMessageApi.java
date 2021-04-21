package com.example.flymessagedome.api;

import com.example.flymessagedome.model.AddPostResult;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.BlackListModel;
import com.example.flymessagedome.model.ChangeBgModel;
import com.example.flymessagedome.model.ChangeGroupHeadModel;
import com.example.flymessagedome.model.ChangeHeadModel;
import com.example.flymessagedome.model.CheckBlackListModel;
import com.example.flymessagedome.model.CreateGroupResult;
import com.example.flymessagedome.model.FriendGetModel;
import com.example.flymessagedome.model.FriendRequestModel;
import com.example.flymessagedome.model.GroupListModel;
import com.example.flymessagedome.model.GroupMemberModel;
import com.example.flymessagedome.model.GroupMembersModel;
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.model.HeadModel;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.model.MessageModel;
import com.example.flymessagedome.model.One;
import com.example.flymessagedome.model.Post;
import com.example.flymessagedome.model.PostListResult;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.model.SendMessageModel;
import com.example.flymessagedome.model.UserSignModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.model.Weather;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class FlyMessageApi {
    private static final String CONNECTION = "connection";
    private static final String HOST = "host";
    private static final String KEEP_ALIVE = "keep-alive";
    private static final String PROXY_CONNECTION = "proxy-connection";
    private static final String TRANSFER_ENCODING = "transfer-encoding";
    private static final String TE = "te";
    private static final String ENCODING = "encoding";
    private static final String UPGRADE = "upgrade";
    private static final List<String> HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList(
            CONNECTION,
            HOST,
            KEEP_ALIVE,
            PROXY_CONNECTION,
            TE,
            TRANSFER_ENCODING,
            ENCODING,
            UPGRADE);
    public static FlyMessageApi instance;
    private FlyMessageApiService fmService;

    public FlyMessageApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.API_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())// 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create())// 添加Gson转换器
                .client(okHttpClient)
                .build();
        fmService = retrofit.create(FlyMessageApiService.class);
    }

    public static FlyMessageApi getInstance(OkHttpClient okHttpClient) {
        if (instance == null) {
            instance = new FlyMessageApi(okHttpClient);
        }
        return instance;
    }

    public Observable<Login> getLogin(String userName, String password) {
        return fmService.login(userName, password);
    }

    public Observable<Base> sendLoginCode(String phone) {
        return fmService.sendLoginCode(phone);
    }

    public Observable<Login> noPassLogin(String phone, String code) {
        return fmService.noPassLogin(phone, code);
    }

    public Observable<Users> getUsersMsg(int id, String loginToken) {
        return fmService.getUsersMsg(id, loginToken);
    }

    public Observable<Base> checkUserPhone(String phone, String loginToken) {
        return fmService.checkUserPhone(phone, loginToken);
    }

    public Observable<SearchUserModel> searchUser(String content, int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.searchUser(loginToken, pageSize, pageIndex, content);
    }

    public Observable<GroupListModel> queryGroup(String content, int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.queryGroup(loginToken, pageSize, pageIndex, content);
    }

    public Observable<Users> getUsersMsg(String u_name, String loginToken) {
        return fmService.getUsersMsg(u_name, loginToken);
    }

    public Observable<GroupModel> getGroupMsg(int g_id, String loginToken) {
        return fmService.getGroupMsg(g_id, loginToken);
    }

    public Observable<MessageModel> getMessage(String loginToken, int pageSize, int pageIndex) {
        return fmService.getMessage(loginToken, pageSize, pageIndex);
    }

    public Observable<GroupListModel> getUserGroups(int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getUserGroups(loginToken, pageSize, pageIndex);
    }

    public Observable<MessageModel> getRecordMessage(int pageSize, int pageIndex, long object_u_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getRecordMessage(loginToken, pageSize, pageIndex, object_u_id);
    }

    public Observable<Base> delMessage(long m_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.delMessage(m_id, loginToken);
    }

    public Observable<GroupMemberModel> getGroupMember(int g_id, int u_id, String loginToken) {
        return fmService.getGroupMember(g_id, u_id, loginToken);
    }

    public Observable<GroupMembersModel> getGroupMembers(int g_id, int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getGroupMembers(g_id, loginToken, pageSize, pageIndex);
    }

    public Observable<CreateGroupResult> createGroup(String g_name, String g_introduce) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.createGroup(g_name, g_introduce, loginToken);
    }

    public Observable<Base> updateGroupMsg(long g_id, String g_name, String g_introduce) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.updateGroupMsg(g_id, g_name, g_introduce, loginToken);
    }

    public Observable<ChangeGroupHeadModel> changeGroupHead(long g_id, String fileUrl) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        String url = Constant.API_BASE_URL + "group/changeGroupHead?loginToken=" + loginToken + "&g_id=" + g_id;
        File file = new File(fileUrl);
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part
        MultipartBody.Part body = null;
        try {
            body = MultipartBody.Part.createFormData("headImage", URLEncoder.encode(file.getName(), "utf-8"), requestFile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fmService.changeGroupHead(url, body);
    }

    public Observable<Base> delGroupMember(long g_id, long del_u_id, String loginToken) {
        return fmService.delGroupMember(g_id, del_u_id, loginToken);
    }

    public Observable<Base> changeGroupMemberRemarkName(long g_id, String g_nick_name, String loginToken) {
        return fmService.changeGroupMemberRemarkName(g_id, g_nick_name, loginToken);
    }

    public Observable<One> getOne(String url) {
        return fmService.getOne(url);
    }

    public Observable<Weather> getWeather(String url) {
        return fmService.getWeather(url);
    }

    public Observable<SendMessageModel> sendUserMessage(String fileUrl, String loginToken, int m_source_id, int m_object_id, int m_content_type, String m_content) {
        String url = Constant.API_BASE_URL + "message/sendMessage?loginToken=" + loginToken + "&m_source_id=" + m_source_id + "&m_type=0&m_content_type=" + m_content_type
                + "&m_content=" + m_content + "&m_object_id=" + m_object_id;
        if (fileUrl != null) {
            File file = new File(fileUrl);
            // 创建 RequestBody，用于封装构建RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part
            MultipartBody.Part body = null;
            try {
                body = MultipartBody.Part.createFormData("messageFile", URLEncoder.encode(file.getName(), "utf-8"), requestFile);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return fmService.sendMessage(url, body);
        } else {
            return fmService.sendMessage(url);
        }
    }

    public Observable<ChangeHeadModel> changeHead(String fileUrl) {
//        bgImage
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        String url = Constant.API_BASE_URL + "user/changeHead?loginToken=" + loginToken;
        File file = new File(fileUrl);
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part
        MultipartBody.Part body = null;
        try {
            body = MultipartBody.Part.createFormData("headImage", URLEncoder.encode(file.getName(), "utf-8"), requestFile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fmService.changeHead(url, body);
    }

    public Observable<ChangeBgModel> changeBgImg(String fileUrl) {
//        bgImage
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        String url = Constant.API_BASE_URL + "user/changeBgImg?loginToken=" + loginToken;
        File file = new File(fileUrl);
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part
        MultipartBody.Part body = null;
        try {
            body = MultipartBody.Part.createFormData("bgImage", URLEncoder.encode(file.getName(), "utf-8"), requestFile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fmService.changeBgImg(url, body);
    }

    public Observable<Base> updateUserMsg(String u_sex, String u_nick_name, long brithday, String u_position) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.updateUserMsg(loginToken, u_sex, u_nick_name, brithday, u_position);
    }

    public Observable<Base> addUserSign(String s_content) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.addUserSign(loginToken, s_content);
    }

    public Observable<Base> delUserSign(int s_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.delUserSign(loginToken, s_id);
    }

    public Observable<UserSignModel> getUserSigns(int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getUserSigns(loginToken, pageSize, pageIndex);
    }

    public Observable<HeadModel> getUserHeads(int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getUserHeads(loginToken, pageSize, pageIndex);
    }

    public Observable<Base> changeOldHead(int h_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.changeOldHead(h_id, loginToken);
    }

    public Observable<Base> delUserHead(int h_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.delUserHead(h_id, loginToken);
    }

    public Observable<SendMessageModel> sendGroupMessage(String fileUrl, String loginToken, long m_source_id, long m_source_g_id, int m_content_type, String m_content) {
        String url = Constant.API_BASE_URL + "message/sendMessage?loginToken=" + loginToken + "&m_source_id=" + m_source_id + "&m_type=1&m_content_type=" + m_content_type
                + "&m_source_g_id=" + m_source_g_id + "&m_content=" + m_content;
        if (fileUrl != null) {
            File file = new File(fileUrl);
            // 创建 RequestBody，用于封装构建RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part
            MultipartBody.Part body = null;
            try {
                body = MultipartBody.Part.createFormData("messageFile", URLEncoder.encode(file.getName(), "utf-8"), requestFile);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return fmService.sendMessage(url, body);
        } else {
            return fmService.sendMessage(url);
        }
    }

    public Observable<CheckBlackListModel> checkBlackList(long u_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.checkBlackList(u_id, loginToken);
    }

    public Observable<Base> delFriend(long f_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.delFriend(loginToken, f_id);
    }

    public Observable<Base> addBlackList(long u_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.addBlackList(u_id, loginToken);
    }

    public Observable<Base> delBlackList(long u_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.delBlackList(u_id, loginToken);
    }

    public Observable<BlackListModel> getBlackList(int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getUserBlackList(loginToken, pageSize, pageIndex);
    }

    public Observable<FriendGetModel> getFriends(int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getFriends(loginToken, pageSize, pageIndex);
    }

    public Observable<FriendRequestModel> getFriendRequests(int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getFriendRequest(loginToken, pageSize, pageIndex);
    }

    public Observable<Base> acceptFriendRequest(int rq_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.acceptFriendRequest(loginToken, rq_id);
    }

    public Observable<Base> delFriendRequest(int rq_id) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.delFriendRequest(loginToken, rq_id);
    }

    public Observable<AddPostResult> addPost(String content) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.addPost(content, loginToken);
    }

    public Observable<PostListResult> getPosts(int pageSize, int pageIndex) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getPosts(loginToken, pageSize, pageIndex);
    }

    public Observable<Base> zanPost(int postId) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.zanPost(loginToken, postId);
    }
    public Observable<Post> getPost(int postId) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.getPost(loginToken, postId);
    }
    public Observable<Base> cancelZanPost(int postId) {
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        return fmService.cancelZanPost(loginToken, postId);
    }
}
