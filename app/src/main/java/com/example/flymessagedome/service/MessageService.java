package com.example.flymessagedome.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.FriendRequestDao;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupBeanDao;
import com.example.flymessagedome.bean.GroupMemberDao;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.bean.socketData.SocketFriendRequest;
import com.example.flymessagedome.bean.socketData.SocketLink;
import com.example.flymessagedome.bean.socketData.SocketMessage;
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.model.GroupMemberModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.ui.activity.GroupChatActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.activity.UserChatActivity;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.MWebSocketClient;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.StringUtil;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Date;
import java.util.List;

public class MessageService extends Service {
    private static final String TAG = "飞讯聊天";
    public Context context;
    public HttpProxyCacheServer proxy;

    public static final String SOCKET_SERVICE_ACTION="socketServiceAction";

    public static final String MSG_TYPE="messageType";

    //服务连接成功
    public static final int SERVICE_CONNECTED=0;
    //服务连接断开
    public static final int SERVICE_DISCONNECT=1;
    //收到消息
    public static final int RECEIVE_USER_MESSAGE=2;
    //收到好友验证
    public static final int RECEIVE_FRIEND_REQUEST=3;
    //异地登陆
    public static final int LOG_OUT=4;

    public MWebSocketClient client;

    private RemoteViews remoteView;
    private NotificationManager manager;
    private Notification notification;
    private String notificationChannelID = "30";

    //每隔3秒进行一次对长连接的心跳检测
    public static final long HEART_BEAT_RATE = 3* 1000;
    boolean keepHeart=true;
    
    UserBeanDao userBeanDao= FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();
    FriendRequestDao friendRequestDao=FlyMessageApplication.getInstances().getDaoSession().getFriendRequestDao();
    MessageDao messageDao=FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    GroupBeanDao groupBeanDao=FlyMessageApplication.getInstances().getDaoSession().getGroupBeanDao();
    GroupMemberDao groupMemberDao=FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao();
    ChatDao chatDao=FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    private MessageServiceBinder binder=new MessageServiceBinder();

    private NotificationManager notificationManager;
    private String notificationId   = "keep_app_live";
    private String notificationName = "APP后台运行中";

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        proxy=FlyMessageApplication.getProxy(context);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_DEFAULT);
            //不震动
            channel.enableVibration(false);
            //静音
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
        //创建服务后,五秒内调用该方法
        startForeground(Integer.MAX_VALUE-2, getNotification());
    }
    /**
     * 获取通知(Android8.0后需要)
     * @return
     */
    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("飞讯聊天")
                .setContentIntent(getIntent())
                .setContentText("后台运行中");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        return builder.build();
    }

    /**
     * 点击后,直接打开app(之前的页面),不跳转特定activity
     * @return
     */
    private PendingIntent getIntent() {
        Intent msgIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getPackageName());//获取启动Activity
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                1,
                msgIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MessageServiceBinder extends Binder{
        public boolean connect(){
            try {
                getClient();
                if (client!=null){
                    client.connectBlocking();
                    keepHeart=true;
                    keepHeart();
                    sendMessageBroadcast(SERVICE_CONNECTED);
                    return true;
                }else{
                    return false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        public void closeConnect() {
            try {
                if (null != client) {
                    client.close();
                    sendMessageBroadcast(SERVICE_DISCONNECT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client = null; //避免重复实例化WebSocketClient对象
                keepHeart=false;
            }
        }
        private void keepHeart() {
            new Thread(keepHeartRunable).start();
        }
        Runnable keepHeartRunable=new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (client!=null&&keepHeart){
                            if (client.isClosed()){
                                client.reconnectBlocking();
                            }
                        }else {
                            break;
                        }
                        Thread.sleep(HEART_BEAT_RATE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        public boolean getConnectState(){
            return client!=null&&client.isOpen();
        }
        public void cancelNotify(int notifyId){
            if (manager!=null)
            manager.cancel(notifyId);
        }
        public void cancelAll(){
            if (manager!=null)
            manager.cancelAll();
        }
    }

    public void getClient(){
        if (!TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getString(Constant.LOGIN_TOKEN))){
            URI uri = URI.create(Constant.SOCKET_BASE_URL+ "?loginToken="+SharedPreferencesUtil.getInstance().getString(Constant.LOGIN_TOKEN));
            client = new MWebSocketClient(uri) {
                @Override
                public void onMessage(String message) {
                    //对接收到的消息message进行处理
                    JSONObject messageJson= null;
                    try {
                        messageJson = new JSONObject(message);
                        Log.e(TAG,messageJson.toString());
                        switch (messageJson.optInt("msgType")){
                            case 1:
                                //普通消息
                                SocketMessage messageBean= JSON.parseObject(message,SocketMessage.class);
                                receiveMessage(messageBean);
                                break;
                            case 2:
                                //添加好友消息
                                SocketFriendRequest friendRequest=JSON.parseObject(message,SocketFriendRequest.class);
                                friendRequestDao.insertOrReplace(friendRequest.getContent());
                                sendMessageBroadcast(RECEIVE_FRIEND_REQUEST);
                                break;
                            case 3:
                                //链接消息
                                SocketLink link=JSON.parseObject(message,SocketLink.class);
                                break;
                            case 4:
                                //下线通知
                                String loginToken=SharedPreferencesUtil.getInstance().getString("loginToken");
                                if (LoginActivity.loginUser!=null&&messageJson.optInt("userId")==LoginActivity.loginUser.getU_id()&&
                                !messageJson.optString("loginToken").equals(loginToken))
                                    sendMessageBroadcast(LOG_OUT);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    super.onOpen(handshakedata);
                    sendMessageBroadcast(SERVICE_CONNECTED);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    super.onClose(code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {
                    super.onError(ex);
                    sendMessageBroadcast(SERVICE_DISCONNECT);
                }
            };
        }
    }
    Users users=null;
    GroupModel groupModel =null;
    GroupMemberModel groupMember=null;
    //接收到用户的消息
    public void receiveMessage(SocketMessage messageBean){
        Message message=messageBean.getContent();
        if (message.getM_object_id()!=LoginActivity.loginUser.getU_id()){
            return;
        }
        message.setLogin_u_id(LoginActivity.loginUser.getU_id());
        String loginToken=SharedPreferencesUtil.getInstance().getString(Constant.LOGIN_TOKEN);
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (userBeanDao.load((long)message.getM_source_id())==null){
                    users=HttpRequest.getUserMsg(message.getM_source_id(),loginToken);
                }
                if (message.getM_type()==1&&groupBeanDao.load((long)message.getM_source_g_id())==null){
                    groupModel =HttpRequest.getGroupMsg(message.getM_source_g_id(),loginToken);
                    if (groupMemberDao.queryBuilder()
                            .where(GroupMemberDao.Properties.G_id.eq(message.getM_source_g_id()))
                            .where(GroupMemberDao.Properties.U_id.eq(message.getM_source_id()))
                            .list().size()==0){
                        groupMember=HttpRequest.getGroupMember(message.getM_source_g_id(),message.getM_source_id(),loginToken);
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                if (users != null && users.code == Constant.SUCCESS){
                    userBeanDao.insertOrReplace(users.getUser());
                    users=null;
                }
                if (groupModel != null && groupModel.code == Constant.SUCCESS) {
                    groupBeanDao.insertOrReplace(groupModel.getGroup());
                    groupModel =null;
                    if (groupMember!=null && groupMember.code == Constant.SUCCESS){
                        groupMemberDao.insertOrReplace(groupMember.getGroup_member());
                    }
                }
                initMessageNotification(message);
            }
        }.execute();
    }
    //构建用户消息推送
    public void initMessageNotification(Message message){
        message.setM_id(null);
        message.setIsSend(true);
        sendMessageBroadcast(RECEIVE_USER_MESSAGE);
        manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "我的消息";
            String description = "好友/群聊消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(notificationChannelID, name, importance);
            mChannel.setDescription(description);
//            mChannel.setAllowBubbles(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setSound(null,null);
            mChannel.enableLights(true);//是否显示通知指示灯
            if (SharedPreferencesUtil.getInstance().getBoolean("msgVibrator",true)){
                mChannel.enableVibration(true);//是否振动
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            }
            manager.createNotificationChannel(mChannel);
        }
        remoteView=new RemoteViews(getPackageName(), R.layout.message_notification_view);

        String content= StringUtil.formatFileMessage(message.getM_content());
        UserBean userBean=userBeanDao.load((long)message.getM_source_id());
        if (userBean==null){
            return;
        }
        if (message.getM_type()==0){//用户消息
            List<Message> messages;
            messages=messageDao.queryBuilder()
                .where(MessageDao.Properties.M_source_id.eq(message.getM_source_id()))
                .where(MessageDao.Properties.M_type.eq(0))
                .where(MessageDao.Properties.M_receive_state.eq(0))
                .list();
            //添加或更新消息列表
            Chat userChat=chatDao.queryBuilder()
                    .where(ChatDao.Properties.Chat_type.eq(0))
                    .where(ChatDao.Properties.Source_id.eq(userBean.getU_id()))
                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                    .unique();
            if (userChat==null){
                userChat=new Chat();
                userChat.setChat_show_remind(true);
                userChat.setChat_up(false);
                userChat.setChat_type(0);
                userChat.setSource_g_id(0);
                userChat.setSource_id(userBean.getU_id());
                userChat.setObject_u_id(LoginActivity.loginUser.getU_id());
            }
            userChat.setChat_reshow(true);
            userChat.setChat_content(content);
            userChat.setChat_head(userBean.getU_head_img());
            userChat.setChat_m_count(messages.size()+1);
            userChat.setChat_name(userBean.getU_nick_name());
            userChat.setTime(new Date(message.getM_send_time()));
            chatDao.insertOrReplace(userChat);
            userChat=chatDao.queryBuilder()
                    .where(ChatDao.Properties.Chat_type.eq(0))
                    .where(ChatDao.Properties.Source_id.eq(userBean.getU_id()))
                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                    .unique();
            message.setCId(userChat.getC_id());
            messageDao.insertOrReplace(message);
            remoteView.setTextViewText(R.id.tv_u_name,userBean.getU_nick_name()+"("+(messages.size()+1)+"条新消息)");
            remoteView.setTextViewText(R.id.tv_msg_content,content);
            remoteView.setImageViewResource(R.id.notify_head_img,R.drawable.icon);
            if (userChat.getChat_show_remind()&&SharedPreferencesUtil.getInstance().getBoolean("allowNotify",true)) {
                Intent actionIntent=new Intent(this, UserChatActivity.class);
                actionIntent.putExtra("userId",userBean.getU_id());
                actionIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                Intent mainIntent=new Intent(this, MainActivity.class);
                Intent[] intents=new Intent[2];
                intents[0]=mainIntent;
                intents[1]=actionIntent;
                PendingIntent intent = PendingIntent.getActivities(this, 0, intents, PendingIntent.FLAG_CANCEL_CURRENT);
                int onChatUId=SharedPreferencesUtil.getInstance().getInt("onChatUserId",-1);
                if (onChatUId!=message.getM_source_id()||!isRunningForeground(FlyMessageApplication.getInstances().getApplicationContext()))
                    notifyUserMessage(userBean.getU_head_img(), (int) message.getCId(),intent);
            }
        }else {//群聊消息
            List<Message> messages=messageDao.queryBuilder()
                    .where(MessageDao.Properties.M_type.eq(1))
                    .where(MessageDao.Properties.M_receive_state.eq(0))
                    .where(MessageDao.Properties.M_source_g_id.eq(message.getM_source_g_id()))
                    .list();
            GroupBean groupBean=groupBeanDao.load((long)message.getM_source_g_id());
            if (groupBean==null){
                return;
            }
            if (groupMember!=null && groupMember.code == Constant.SUCCESS){
                content=groupMember.getGroup_member().getG_nick_name()+":"+content;
                groupMember=null;
            }else {
                content=userBean.getU_nick_name()+":"+content;
            }
            Chat userChat=chatDao.queryBuilder()
                    .where(ChatDao.Properties.Chat_type.eq(1))
                    .where(ChatDao.Properties.Source_g_id.eq(groupBean.getG_id()))
                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                    .unique();
            if (userChat==null){
                userChat=new Chat();
                userChat.setChat_show_remind(true);
                userChat.setChat_up(false);
                userChat.setChat_type(1);
                userChat.setSource_id(0);
                userChat.setSource_g_id(groupBean.getG_id());
                userChat.setObject_u_id(LoginActivity.loginUser.getU_id());
            }
            userChat.setChat_reshow(true);
            userChat.setChat_content(content);
            userChat.setChat_head(groupBean.getG_head_img());
            userChat.setChat_m_count(messages.size()+1);
            userChat.setChat_name(groupBean.getG_name());
            userChat.setTime(new Date(message.getM_send_time()));
            chatDao.insertOrReplace(userChat);
            userChat=chatDao.queryBuilder()
                    .where(ChatDao.Properties.Chat_type.eq(1))
                    .where(ChatDao.Properties.Source_g_id.eq(groupBean.getG_id()))
                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                    .unique();
            message.setCId(userChat.getC_id());
            messageDao.insertOrReplace(message);
            remoteView.setTextViewText(R.id.tv_u_name,groupBean.getG_name()+"("+(messages.size()+1)+"条新消息)");
            remoteView.setTextViewText(R.id.tv_msg_content,content);
            remoteView.setImageViewResource(R.id.notify_head_img,R.drawable.icon);
            if (userChat.getChat_show_remind()&&SharedPreferencesUtil.getInstance().getBoolean("allowNotify",true)){
                Intent actionIntent=new Intent(this, GroupChatActivity.class);
                actionIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                actionIntent.putExtra("groupId",groupBean.getG_id());
                Intent mainIntent=new Intent(this, MainActivity.class);
                Intent[] intents=new Intent[2];
                intents[0]=mainIntent;
                intents[1]=actionIntent;
                PendingIntent intent = PendingIntent.getActivities(this, 0, intents, PendingIntent.FLAG_CANCEL_CURRENT);
                int onChatUId=SharedPreferencesUtil.getInstance().getInt("onChatGroupId",-1);
                if (onChatUId!=message.getM_source_g_id()||!isRunningForeground(FlyMessageApplication.getInstances().getApplicationContext()))
                    notifyUserMessage(groupBean.getG_head_img(),(int) message.getCId(),intent);
            }
        }
    }
    //发送用户消息推送
    public void notifyUserMessage(String headUrl,int notifyId,PendingIntent pendingIntent){

        headUrl=proxy.getProxyUrl(headUrl);
        try {
            Glide.with(context).asBitmap().load(headUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (resource.getHeight()>resource.getWidth()){
                                resource=Bitmap.createBitmap(resource,0,(resource.getHeight()-resource.getWidth())/2,resource.getWidth(),resource.getWidth());
                            }else if (resource.getWidth()>resource.getHeight()){
                                resource=Bitmap.createBitmap(resource,(resource.getWidth()-resource.getHeight())/2,0,resource.getHeight(),resource.getHeight());
                            }
                            remoteView.setImageViewBitmap(R.id.notify_head_img,resource);
                            NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),notificationChannelID)
                                    .setSmallIcon(R.drawable.icon)
                                    .setOngoing(false)
                                    .setContent(remoteView)
                                    .setPriority(NotificationCompat.PRIORITY_LOW)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent);
                            if (SharedPreferencesUtil.getInstance().getBoolean("msgVoice",true)){
                                Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.message);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();
                            }
                            if (SharedPreferencesUtil.getInstance().getBoolean("msgVibrator",true)){
                                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                vibrator.vibrate(500);
                            }
                            notification =builder.build();
                            manager.notify(notifyId,notification);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();

        }finally {
        }
    }

    //发送广播
    public void sendMessageBroadcast(int msgType){
        Intent actionIntent = new Intent(SOCKET_SERVICE_ACTION);
        actionIntent.putExtra(MSG_TYPE,msgType);
        context.sendBroadcast(actionIntent);
    }
    /**
     * 判断本应用是否已经位于最前端
     *
     * @param context
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        //枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
