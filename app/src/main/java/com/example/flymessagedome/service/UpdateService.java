package com.example.flymessagedome.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.example.flymessagedome.BuildConfig;
import com.example.flymessagedome.R;

import java.io.File;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class UpdateService extends Service {
    public static final String UpdateDownPath= Environment.getExternalStorageDirectory()+"/FlyMessage/apk/飞讯聊天.apk";
    private static int notifyId = 30;
    private String notificationChannelID = "30";
    public static String ACTION = "update_service";
    public static String KEY_USR_ACTION = "key_update_action";
    public static final int ACTION_CONTROL = 1;
    boolean isOnUpdating=false;
    RemoteViews remoteView;
    NotificationManager manager;
    Notification notification;
    int p=0;
    long taskId=-1;
    String url;
    public UpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Aria.download(this).register();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyUpdateBinder();
    }
    public class MyUpdateBinder extends Binder{
        public void startUpdate(String url){
            if (taskId!=-1){
                return;
            }
            UpdateService.this.url=url;
            taskId = Aria.download(this)
                    .load(url)     //读取下载地址
                    .ignoreCheckPermissions()
                    .ignoreFilePathOccupy()
                    .setFilePath(UpdateDownPath) //设置文件保存的完整路径
                    .create();
            isOnUpdating=true;
            initNotification();
        }
        public boolean checkOnDownloading(){
            return isOnUpdating;
        }
    }
    public void stopDownload(){
        if (taskId!=-1) {
            Aria.download(this)
                    .load(taskId)     //读取任务id
                    .stop();       // 停止任务
        }
        isOnUpdating=false;
        updateNotification();
    }
    public void continueDownload(){
        if (taskId!=-1){
            Aria.download(this)
                    .load(taskId)     //读取任务id
                    .resume();    // 恢复任务
            updateNotification();
        }else if (url!=null){
            taskId = Aria.download(this)
                    .load(url)     //读取下载地址
                    .ignoreCheckPermissions()
                    .ignoreFilePathOccupy()
                    .setFilePath(UpdateDownPath) //设置文件保存的完整路径
                    .create();
            initNotification();
        }
        isOnUpdating=true;
    }
    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning protected void running(DownloadTask task) {
        if(task.getKey().equals(url)){
            p = task.getPercent();	//任务进度百分比
            updateNotification();
        }
    }
    @Download.onTaskFail void taskFail(DownloadTask task, Exception e) {
        if(task.getKey().equals(url)){
            p=0;
            isOnUpdating=false;
            updateNotification();
        }
    }
    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        //在这里处理任务完成的状态
        if (task.getKey().equals(url)){
            Toast.makeText(getApplicationContext(),"更新下载完成，即将安装",Toast.LENGTH_SHORT).show();
            //安装文件(适配Android7.0)
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", new File(UpdateDownPath));
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(new File(UpdateDownPath)), "application/vnd.android.package-archive");
            }
            getApplicationContext().startActivity(intent);
        }
    }
    private void initNotification(){
        manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification channel";
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel mChannel = new NotificationChannel(notificationChannelID, name, importance);
            mChannel.setDescription(description);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(mChannel);
        }
        remoteView=new RemoteViews(getPackageName(), R.layout.update_notification_view);
        Intent actionIntent=new Intent(ACTION);
        actionIntent.putExtra(KEY_USR_ACTION,ACTION_CONTROL);
        PendingIntent intent = PendingIntent.getBroadcast(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.update_control_view,intent);
        notification =new NotificationCompat.Builder(getApplicationContext(),notificationChannelID)
                .setSmallIcon(R.drawable.icon)
                .setOngoing(false)
                .setCustomBigContentView(remoteView)//设置显示bigView的notification视图
                .setContent(remoteView)//设置普通notification视图
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)//设置最大优先级
                .build();
        manager.notify(notifyId,notification);
        updateNotification();
    }
    private void updateNotification(){
        if (remoteView!=null)
            if (isOnUpdating){
                remoteView.setTextViewText(R.id.update_msg, "更新正在下载 "+p+"%");
            }else {
                remoteView.setTextViewText(R.id.update_msg, "下载暂停,点击继续");
            }
        notification.contentView = remoteView;
        notification.bigContentView=remoteView;
        manager.notify(notifyId,notification);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION)){
                if (isOnUpdating){
                    stopDownload();
                }else {
                    continueDownload();
                }
            }
        }
    };
}
