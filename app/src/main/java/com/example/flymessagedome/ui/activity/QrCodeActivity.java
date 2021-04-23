package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flymessagedome.R;
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.ImageUtils;
import com.example.flymessagedome.utils.ToastUtils;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_HEAD_IMG;

public class QrCodeActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private DecoratedBarcodeView decoratedBarcodeView;
    private CaptureManager captureManager;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        mContext = this;
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        findViewById(R.id.back).setOnClickListener(v -> finish());
        findViewById(R.id.my_qr_code).setOnClickListener(v -> startActivity(new Intent(QrCodeActivity.this, MyQrCodeActivity.class)));
        decoratedBarcodeView = this.findViewById(R.id.decoratedBarcodeView);
        //重要代码，初始化捕获
        captureManager = new CaptureManager(this, decoratedBarcodeView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
        decoratedBarcodeView.setTorchListener(this);
        findViewById(R.id.local_qr_code).setOnClickListener(v -> choiceImg());
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    private void choiceImg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            ArrayList<String> checkPhotos = new ArrayList<>();
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(mContext)
                    .cameraFileDir(null)
                    .maxChooseCount(1) // 图片选择张数的最大值
                    .selectedPhotos(checkPhotos) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_HEAD_IMG);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_HEAD_IMG) {
            List<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
            if (selectedPhotos.size() > 0) {
                new AsyncTask<Void, Void, Result>() {

                    @Override
                    protected Result doInBackground(Void... voids) {
                        return ImageUtils.decodeBarcodeRGB(selectedPhotos.get(0));
                    }

                    @Override
                    protected void onPostExecute(Result result) {
                        try {
                            if (result != null)
                                done(result.getText());
                            else
                                ToastUtils.showToast("扫描失败，请重试");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTorchOn() {

    }

    @SuppressLint("StaticFieldLeak")
    private void done(String content) {
        if (content == null) {
            ToastUtils.showToast("扫描失败");
        } else if (content.contains("http")) {
            Intent intent = new Intent(mContext, WebActivity.class);
            intent.putExtra("URLString", content);
            startActivity(intent);
        } else if (content.contains("[") && content.contains("]")) {
            String fileContent = content.substring(content.indexOf("[") + 1, content.indexOf("]"));
            if (fileContent.contains(":")) {
                String[] fileParams = fileContent.split(":");
                fileContent = fileParams[0];
                if (fileContent.equals("FlyMessage-addFriend")) {
                    String u_name = fileParams[1];
                    Intent intent = new Intent(mContext, ShowUserActivity.class);
                    intent.putExtra("userName", u_name);
                    startActivity(intent);
                    finish();
                } else if (fileContent.equals("FlyMessage-addGroup")) {
                    String g_id = fileParams[1];
                    try {
                        int gId = Integer.parseInt(g_id);
                        try {
                            new AsyncTask<Void, Void, GroupModel>() {
                                @Override
                                protected GroupModel doInBackground(Void... voids) {
                                    return HttpRequest.getGroupMsg(gId);
                                }

                                @Override
                                protected void onPostExecute(GroupModel groupModel) {
                                    try {
                                        if (groupModel != null && groupModel.code == Constant.SUCCESS) {
                                            Bundle bundle = new Bundle();
                                            bundle.putParcelable("group", groupModel.getGroup());
                                            Intent intent = new Intent(mContext, GroupMsgActivity.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            finish();
                                        } else if (groupModel != null) {
                                            ToastUtils.showToast(groupModel.msg);
                                        } else {
                                            ToastUtils.showToast("获取群聊信息失败");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            ToastUtils.showToast(content);
        }
    }

    @Override
    public void onTorchOff() {

    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            captureManager.onPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            captureManager.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            captureManager.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        try {
            super.onSaveInstanceState(outState, outPersistentState);
            captureManager.onSaveInstanceState(outState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return decoratedBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
