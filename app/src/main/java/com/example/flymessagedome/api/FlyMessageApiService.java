package com.example.flymessagedome.api;

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
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.model.GroupMemberModel;
import com.example.flymessagedome.model.GroupMembersModel;
import com.example.flymessagedome.model.HeadModel;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.model.MessageModel;
import com.example.flymessagedome.model.One;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.model.SendMessageModel;
import com.example.flymessagedome.model.UserSignModel;
import com.example.flymessagedome.model.Users;

import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface FlyMessageApiService {
    /**
     * 登录
     * @param username
     * @param pass
     * @return
     */
    @GET("user/login")
    Observable<Login> login(@Query("username") String username, @Query("pass")String pass);

    /**
     * 发送登录验证码
     * @param phone
     * @return
     */
    @GET("SMS/sendLoginSMS")
    Observable<Base> sendLoginCode(@Query("phone")String phone);

    /**
     * 校验登录验证码(免密登录)
     * @param phone
     * @param code
     * @return
     */
    @GET("SMS/noPassLogin")
    Observable<Login> noPassLogin(@Query("phone")String phone, @Query("code")String code);

    @GET("user/getUserByID")
    Observable<Users> getUsersMsg(@Query("object_u_id")int id,@Query("loginToken")String loginToken);

    @GET("user/getUserByName")
    Observable<Users> getUsersMsg(@Query("u_name")String u_name,@Query("loginToken")String loginToken);

    @GET("group/getGroupMsg")
    Observable<GroupModel> getGroupMsg(@Query("g_id")int g_id, @Query("loginToken")String loginToken);

    @GET("group/getUserGroups")
    Observable<GroupListModel> getUserGroups(@Query("loginToken")String loginToken, @Query("pageSize") int pageSize, @Query("pageIndex") int pageIndex);

    @GET("user/searchUser")
    Observable<SearchUserModel> searchUser(@Query("loginToken") String loginToken,@Query("pageSize")int pageSize,@Query("pageIndex")int pageIndex,@Query("queryString")String queryString);

    @GET("")
    Observable<One> getOne(@Url String url);

    @GET("message/getUserMessage")
    Observable<MessageModel> getMessage(@Query("loginToken")String loginToken,@Query("pageSize") int pageSize,@Query("pageIndex") int pageIndex);

    @GET("message/delMessage")
    Observable<Base> delMessage(@Query("m_id") long m_id,@Query("loginToken")String loginToken);

    @GET("message/getRecordMessage")
    Observable<MessageModel> getRecordMessage(@Query("loginToken")String loginToken,@Query("pageSize") int pageSize,@Query("pageIndex") int pageIndex,@Query("object_u_id")long object_u_id);

    @GET("group/getGroupMemberById")
    Observable<GroupMemberModel> getGroupMember(@Query("g_id") int g_id, @Query("object_u_id") int u_id, @Query("loginToken") String loginToken);

    @GET("group/getGroupMember")
    Observable<GroupMembersModel> getGroupMembers(@Query("g_id") int id, @Query("loginToken") String loginToken, @Query("pageSize") int pageSize, @Query("pageIndex") int pageNum);

    @GET("group/queryGroup")
    Observable<GroupListModel> queryGroup(@Query("loginToken") String loginToken,@Query("pageSize")int pageSize,@Query("pageIndex")int pageIndex,@Query("queryString")String queryString);

    @GET("group/createGroup")
    Observable<CreateGroupResult> createGroup(@Query("g_name") String g_name, @Query("g_introduce") String g_introduce, @Query("loginToken")String loginToken);

    @GET("group/updateGroupMsg")
    Observable<Base> updateGroupMsg(@Query("g_id") long id,@Query("g_name") String g_name, @Query("g_introduce") String g_introduce, @Query("loginToken")String loginToken);

    @Multipart
    @POST("")
    Observable<ChangeGroupHeadModel> changeGroupHead(@Url String url, @Part MultipartBody.Part headImage);

    @GET("group/addGroupMember")
    Observable<Base> addGroupMember(@Query("g_id") long g_id,@Query("loginToken")String loginToken);

    /**
     * 可群成员自己删除自己和群主删除群成员
     * @param g_id
     * @param del_u_id
     * @param loginToken
     * @return
     */
    @GET("group/delGroupMember")
    Observable<Base> delGroupMember(@Query("g_id") long g_id,@Query("del_u_id") long del_u_id,@Query("loginToken")String loginToken);

    /**
     * 修改群成员昵称
     * @param g_id
     * @param g_nick_name
     * @param loginToken
     * @return
     */
    @GET("group/changeGroupMemberRemarkName")
    Observable<Base> changeGroupMemberRemarkName(@Query("g_id") long g_id,@Query("g_nick_name")String g_nick_name,@Query("loginToken")String loginToken);

    @GET("user/getUserHeads")
    Observable<HeadModel> getUserHeads(@Query("loginToken") String loginToken, @Query("pageSize") int pageSize, @Query("pageIndex")int pageIndex);

    @GET("user/delUserHead")
    Observable<Base> delUserHead(@Query("h_id") int h_id,@Query("loginToken")String loginToken);

    @GET("user/changeOldHead")
    Observable<Base> changeOldHead(@Query("h_id") int h_id,@Query("loginToken")String loginToken);

    @GET("user/checkUserPhone")
    Observable<Base> checkUserPhone(@Query("phone") String phone,@Query("loginToken")String loginToken);

    @Multipart
    @POST("")
    Observable<SendMessageModel> sendMessage(@Url String url, @Part MultipartBody.Part messageFile);

    @GET("")
    Observable<SendMessageModel> sendMessage(@Url String url);

    @GET("friend/checkBlackList")
    Observable<CheckBlackListModel> checkBlackList(@Query("object_u_id") long object_u_id,@Query("loginToken")String loginToken);

    @GET("friend/addBlackList")
    Observable<Base> addBlackList(@Query("object_u_id") long object_u_id,@Query("loginToken")String loginToken);

    @GET("friend/delBlackList")
    Observable<Base> delBlackList(@Query("object_u_id") long object_u_id,@Query("loginToken")String loginToken);

    @GET("friend/getUserBlackList")
    Observable<BlackListModel> getUserBlackList(@Query("loginToken") String loginToken,@Query("pageSize") int pageSize,@Query("pageIndex")int pageIndex);

    @GET("friend/getUserFriends")
    Observable<FriendGetModel> getFriends(@Query("loginToken") String loginToken,@Query("pageSize") int pageSize,@Query("pageIndex")int pageIndex);

    @GET("friend/changeFriendRemarkName")
    Observable<Base> changeFriendRemarkName(@Query("loginToken") String loginToken,@Query("f_id") int f_id,@Query("f_remarks_name") String f_remarks_name);

    @GET("friend/delFriend")
    Observable<Base> delFriend(@Query("loginToken") String loginToken,@Query("f_id") long f_id);

    @GET("friend/getFriendRequest")
    Observable<FriendRequestModel> getFriendRequest(@Query("loginToken") String loginToken,@Query("pageSize") int pageSize,@Query("pageIndex") int pageIndex);

    @GET("friend/delFriendRequest")
    Observable<Base> delFriendRequest(@Query("loginToken") String loginToken,@Query("rq_id")int rq_id);

    @GET("friend/addFriendRequest")
    Observable<Base> addFriendRequest(@Query("loginToken") String loginToken,@Query("rq_object_u_id") long u_id,@Query("rq_content") String content,@Query("rq_remarks_name") String remake_name);

    @GET("friend/acceptFriendRequest")
    Observable<Base> acceptFriendRequest(@Query("loginToken") String loginToken,@Query("rq_id")int rq_id);

    @Multipart
    @POST("")
    Observable<ChangeHeadModel> changeHead(@Url String url, @Part MultipartBody.Part headImage);

    @Multipart
    @POST("")
    Observable<ChangeBgModel> changeBgImg(@Url String url, @Part MultipartBody.Part headImage);

    @GET("user/updateUserMsg")
    Observable<Base> updateUserMsg(@Query("loginToken") String loginToken,@Query("u_sex") String u_sex,@Query("u_nick_name") String u_nick_name,@Query("brithday") long brithday,@Query("u_position") String u_position);

    @GET("user/addUserSign")
    Observable<Base> addUserSign(@Query("loginToken") String loginToken,@Query("s_content")String s_content);

    @GET("user/delUserSign")
    Observable<Base> delUserSign(@Query("loginToken") String loginToken,@Query("s_id")int s_id);

    @GET("user/getUserSigns")
    Observable<UserSignModel> getUserSigns(@Query("loginToken") String loginToken, @Query("pageSize") int pageSize, @Query("pageIndex")int pageIndex);
}

