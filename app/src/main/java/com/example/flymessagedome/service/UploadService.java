package com.example.flymessagedome.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.arialyy.annotations.Upload;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.common.HttpOption;
import com.arialyy.aria.core.common.RequestEnum;
import com.arialyy.aria.core.task.UploadTask;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

public class UploadService extends Service {
    public static final ArrayList<UploadTaskBean> addItemStack = new ArrayList<>();

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void initUploadTask(ArrayList<UploadTaskBean> uploadTaskBeans) {
        try {
            addItemStack.addAll(uploadTaskBeans);
            for (UploadTaskBean uploadTaskBean : uploadTaskBeans) {
                HttpOption option = new HttpOption();
                option.setRequestType(RequestEnum.POST);
                option.setParam("postId", String.valueOf(uploadTaskBean.postId));
                option.setAttachment("file");
                Aria.upload(FlyMessageApplication.getInstances().uploadService).load(uploadTaskBean.path)
                        .setUploadUrl(Constant.API_BASE_URL + "community/addPostItem?loginToken=" + SharedPreferencesUtil.getInstance().getString("loginToken"))
                        .ignoreFilePathOccupy()
                        .option(option)
                        .create();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Aria.download(this).register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aria.upload(this).unRegister();
    }

    @Upload.onTaskRunning
    protected void running(UploadTask task) {

    }

    @Upload.onTaskComplete
    public void taskComplete(UploadTask task) {
        String path = task.getEntity().getFilePath();
        for (int i = 0; i < addItemStack.size(); i++) {
            String photo = addItemStack.get(i).path;
            if (TextUtils.equals(photo, path)) {
                addItemStack.remove(i);
                break;
            }
        }
        boolean onUpload = (addItemStack.size() != 0);
        if (!onUpload) {
            ToastUtils.showToast("所有图片上传完成");
        } else {
            ToastUtils.showToast("上传1张图片成功:剩余" + addItemStack.size() + "张");
        }
    }

    public static class UploadTaskBean {
        int postId;
        String path;

        public UploadTaskBean(int postId, String path) {
            this.postId = postId;
            this.path = path;
        }
    }

    public class MyBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }
}
