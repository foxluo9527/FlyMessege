package com.example.flymessagedome.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.model.AppVersionBean;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.CheckBlackListModel;
import com.example.flymessagedome.model.GroupCreatorModel;
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.model.GroupMemberModel;
import com.example.flymessagedome.model.GroupMembersModel;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.model.PrivacyModel;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.ui.activity.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {
    public static final String TAG="FlyMessageDome";
    public static Login Login(String u_name,String pass){
        Login login=null;
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/login?username="+u_name+"&pass="+pass);
        if (resultJson!=null){
            login= JSON.parseObject(resultJson.toString(),Login.class);
        }
        return login;
    }
    public static Users getUserMsg(int id, String loginToken){
        Users users=null;
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/getUserByID?object_u_id="+id+"&loginToken="+loginToken);
        if (resultJson!=null){
            users= JSON.parseObject(resultJson.toString(),Users.class);
        }
        return users;
    }
    public static Users getUserMsg(String u_name, String loginToken){
        Users users=null;
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/getUserByName?u_name="+u_name+"&loginToken="+loginToken);
        if (resultJson!=null){
            users= JSON.parseObject(resultJson.toString(),Users.class);
        }
        return users;
    }
    public static Base changePass(String u_pass){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/changePass?u_pass="+u_pass+"&loginToken="+loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(),Base.class);
        }
        return base;
    }
    public static Base checkUserPhone(String phone){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/checkUserPhone?phone="+phone+"&loginToken="+loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(),Base.class);
        }
        return base;
    }
    public static SearchUserModel.ResultBean getUserByPhone(String phone){
        SearchUserModel base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/searchUser?queryString="+phone+"&pageSize=1&pageIndex=1&loginToken="+loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(),SearchUserModel.class);
        }
        if (base.code==Constant.SUCCESS){
            if (base.getResult().size()>0){
                return base.getResult().get(0);
            }
        }
        return null;
    }
    public static Base checkUserName(String u_name){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/checkUserName?u_name="+u_name+"&loginToken="+loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(),Base.class);
        }
        return base;
    }
    public static Base changeUserName(String u_name){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/updateUserMsg?brithday="+ LoginActivity.loginUser.getU_brithday() +"&u_name="+u_name+"&loginToken="+loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(),Base.class);
        }
        return base;
    }
    public static Base changePhone(String u_phone){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/changePhone?u_phone="+u_phone+"&loginToken="+loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(),Base.class);
        }
        return base;
    }
    public static GroupModel getGroupMsg(int id, String loginToken){
        GroupModel groupModel =null;
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"group/getGroupMsg?g_id="+id+"&loginToken="+loginToken);
        if (resultJson!=null){
            groupModel = JSON.parseObject(resultJson.toString(), GroupModel.class);
        }
        return groupModel;
    }
    public static GroupCreatorModel getGroupCreator(long id, String loginToken){
        GroupCreatorModel groupModel =null;
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"group/getGroupCreator?g_id="+id+"&loginToken="+loginToken);
        if (resultJson!=null){
            groupModel = JSON.parseObject(resultJson.toString(), GroupCreatorModel.class);
        }
        return groupModel;
    }
    public static PrivacyModel getPrivacy(){
        PrivacyModel privacy=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/getUserPrivacy?loginToken="+loginToken);
        if (resultJson!=null){
            privacy= JSON.parseObject(resultJson.toString(),PrivacyModel.class);
        }
        return privacy;
    }
    public static Base changePrivacy(PrivacyModel.PrivacyBean privacy){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"user/changeUserPrivacy?show_u_message="+privacy.getShow_u_message()+"&show_online_state="+privacy.getShow_online_state()+"&loginToken="+loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(),Base.class);
        }
        return base;
    }
    public static GroupModel getGroupMsg(long g_id){
        GroupModel base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"group/getGroupMsg?g_id="+g_id+"&loginToken="+loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(),GroupModel.class);
        }
        return base;
    }
    public static GroupMemberModel getGroupMember(int id,int u_id, String loginToken){
        GroupMemberModel groupMember=null;
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"group/getGroupMemberById?g_id="+id+"&object_u_id="+u_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            groupMember= JSON.parseObject(resultJson.toString(), GroupMemberModel.class);
        }
        return groupMember;
    }
    public static GroupMembersModel getGroupMembers(int id, String loginToken, int pageSize, int pageNum){
        GroupMembersModel groupMember=null;
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"groupModel/getGroupMember?g_id="+id+"&loginToken="+
                loginToken+"&pageSize="+pageSize+"&pageIndex="+pageNum);
        if (resultJson!=null){
            groupMember= JSON.parseObject(resultJson.toString(), GroupMembersModel.class);
        }
        return groupMember;
    }
    public static Base changeRMKName(long f_id,String name){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"friend/changeFriendRemarkName?f_id="+f_id+"&loginToken="+
                loginToken+"&f_remarks_name="+name);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static CheckBlackListModel checkBlackList(long object_u_id){
        CheckBlackListModel base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"friend/checkBlackList?object_u_id="+object_u_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), CheckBlackListModel.class);
        }
        return base;
    }
    public static Base addBlackList(long object_u_id){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"friend/addBlackList?object_u_id="+object_u_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base delGroup(long g_id){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"group/delGroup?g_id="+g_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base changeGroupMemberRemarkName(long g_id,String name){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"group/changeGroupMemberRemarkName?g_id="+g_id+"&g_nick_name="+name+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base exitGroup(long g_id,long del_u_id){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"group/delGroupMember?g_id="+g_id+"&del_u_id="+del_u_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base addGroupMember(long g_id){
        Base base=null;
//        http://www.foxluo.cn/FlyMessage/group/addGroupMember?loginToken=5c4f1912-9841-48b5-ae77-47a596481b9e&g_id=6
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        if (loginToken==null){
            return null;
        }
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"group/addGroupMember?g_id="+g_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base delBlackList(long object_u_id){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"friend/delBlackList?object_u_id="+object_u_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base delFriend(long f_id){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"friend/delFriend?f_id="+f_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base addFriendRequest(long object_u_id,String rq_content,String rq_remarks_name){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"friend/addFriendRequest?rq_object_u_id="+object_u_id+"&loginToken="+
                loginToken+"&rq_content="+rq_content+"&rq_remarks_name="+rq_remarks_name);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base acceptFriendRequest(long rq_id){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"friend/acceptFriendRequest?rq_id="+rq_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static Base delFriendRequest(long rq_id){
        Base base=null;
        String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
        JSONObject resultJson=OkHttpGet(Constant.API_BASE_URL+"friend/delFriendRequest?rq_id="+rq_id+"&loginToken="+
                loginToken);
        if (resultJson!=null){
            base= JSON.parseObject(resultJson.toString(), Base.class);
        }
        return base;
    }
    public static AppVersionBean checkUpdate(String version){
        AppVersionBean versionBean=null;
        try {
            String baseUrl=Constant.APP_UPDATE_API+version;
            JSONObject resultJson=OkHttpGet(baseUrl);
            if (resultJson.optInt("code")==200){
                versionBean=JSON.parseObject(resultJson.toString(),AppVersionBean.class);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return versionBean;
    }
    private static JSONObject OkHttpGet(String url){
        JSONObject resultJson=null;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(50, TimeUnit.SECONDS).build();   //50毫秒内响应
        Request request = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String result = response.body().string();
            Log.e(TAG,"response: " + result);
            try {
                resultJson=new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultJson;
    }
}
