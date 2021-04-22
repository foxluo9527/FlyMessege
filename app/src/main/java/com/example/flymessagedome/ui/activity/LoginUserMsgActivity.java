package com.example.flymessagedome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.contract.MineContract;
import com.example.flymessagedome.ui.presenter.MineFragmentPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.ImageUtils;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_BG_IMG;
import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_HEAD_IMG;

@SuppressLint("NonConstantResourceId")
public class LoginUserMsgActivity extends BaseActivity implements MineContract.View,EasyPermissions.PermissionCallbacks{

    @BindView(R.id.nick_name)
    TextView nickName;
    @BindView(R.id.u_name)
    TextView u_name;
    @BindView(R.id.position)
    TextView position;
    @BindView(R.id.sex)
    ImageView sex;
    @BindView(R.id.u_head_img)
    CircleImageView headImg;
    @BindView(R.id.main_view)
    ImageView mainView;
    @BindView(R.id.sign)
    TextView sign;

    @Inject
    MineFragmentPresenter mineFragmentPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_user_msg;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.back,R.id.u_head_img,R.id.sign_view,R.id.msg_view,R.id.change_msg,R.id.show_bg_view,R.id.my_qr_code,R.id.more,R.id.community})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.msg_view:
                Intent showMsigntent=new Intent(mContext,ShowUserMsgActivity.class);
                showMsigntent.putExtra("userId",LoginActivity.loginUser.getU_id());
                startActivity(showMsigntent);
                break;
            case R.id.show_bg_view:
                showBGPopupMenu(mContext);
                break;
            case R.id.sign_view:
                startActivity(new Intent(mContext,AddSignActivity.class));
                break;
            case R.id.change_msg:
                startActivity(new Intent(mContext, EditUserMsgActivity.class));
                break;
            case R.id.u_head_img:
                showHeadPopupMenu(mContext);
                break;
            case R.id.my_qr_code:
                startActivity(new Intent(mContext,MyQrCodeActivity.class));
                break;
            case R.id.more:
                showMorePopupMenu(mContext);
                break;
            case R.id.community:
                Intent intent=new Intent(mContext, UserCommunityActivity.class);
                intent.putExtra("userId",LoginActivity.loginUser.getU_id());
                intent.putExtra("uName",LoginActivity.loginUser.getU_nick_name());
                startActivityForResult(intent,2);
                break;
        }
    }
    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void configViews() {
        mineFragmentPresenter.attachView(this);
    }

    @Override
    public void initDatas() {
        u_name.setText(LoginActivity.loginUser.getU_name());
        nickName.setText(LoginActivity.loginUser.getU_nick_name());
        if (TextUtils.isEmpty(LoginActivity.loginUser.getU_sex())){
            sex.setVisibility(View.GONE);
        }else {
            sex.setVisibility(View.VISIBLE);
            if (LoginActivity.loginUser.getU_sex().equals("男")){
                sex.setImageDrawable(AppCompatResources.getDrawable(mContext,R.mipmap.man));
            }else {
                sex.setImageDrawable(AppCompatResources.getDrawable(mContext,R.mipmap.women));
            }
        }
        position.setText(LoginActivity.loginUser.getU_position());
        sign.setText(LoginActivity.loginUser.getU_sign());
        Glide.with(mContext)
                .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(LoginActivity.loginUser.getU_head_img()))
                .into(headImg);
        Glide.with(mContext)
                .asDrawable()
                .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(LoginActivity.loginUser.getU_bg_img()))
                .into(mainView);
        u_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText("url", LoginActivity.loginUser.getU_name()));
                ToastUtils.showToast("用户名已复制到剪切板");
                return false;
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showHeadPopupMenu(Context context) {
        final View popView = View.inflate(context,R.layout.click_head_popup,null);
        View cancel=popView.findViewById(R.id.cancel_item);
        View changeHead=popView.findViewById(R.id.change_head);
        View showHead=popView.findViewById(R.id.show_head);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = ImageUtils.dip2px(mContext,161);
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(() -> {
            WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
            lp.alpha = 1.0f;
            ((Activity)context).getWindow().setAttributes(lp);
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp =  ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);
        View.OnClickListener listener= v -> {
            switch (v.getId()){
                case R.id.cancel_item:
                    popupWindow.dismiss();
                    break;
                case R.id.change_head:
                    popupWindow.dismiss();
                    choiceHeadImg();
                    break;
                case R.id.show_head:
                    popupWindow.dismiss();
                    startActivity(new Intent(mContext,ShowLoginHeadActivity.class));
                    break;
            }
        };
        cancel.setOnClickListener(listener);
        changeHead.setOnClickListener(listener);
        showHead.setOnClickListener(listener);

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showBGPopupMenu(Context context) {
        final View popView = View.inflate(context,R.layout.show_login_bg_popup,null);
        View change_head_item=popView.findViewById(R.id.change_head_item);
        View change_bg_item=popView.findViewById(R.id.change_bg_item);
        View cancel_item=popView.findViewById(R.id.cancel_item);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = ImageUtils.dip2px(mContext,163);
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp =  ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.cancel_item:
                        popupWindow.dismiss();
                        break;
                    case R.id.change_bg_item:
                        choiceBgImg();
                        popupWindow.dismiss();
                        break;
                    case R.id.change_head_item:
                        showBGImg();
                        popupWindow.dismiss();
                        break;
                }
            }
        };
        cancel_item.setOnClickListener(listener);
        change_bg_item.setOnClickListener(listener);
        change_head_item.setOnClickListener(listener);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showMorePopupMenu(Context context) {
        final View popView = View.inflate(context,R.layout.show_login_msg_more_popup,null);
        View show_bg_item=popView.findViewById(R.id.show_bg_item);
        View change_bg_item=popView.findViewById(R.id.change_bg_item);
        View changeHead=popView.findViewById(R.id.change_head);
        View showHead=popView.findViewById(R.id.show_head);
        View show_msg_item=popView.findViewById(R.id.show_msg_item);
        View change_msg_item=popView.findViewById(R.id.change_msg_item);
        View cancel_item=popView.findViewById(R.id.cancel_item);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = ImageUtils.dip2px(mContext,390);
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp =  ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.cancel_item:
                        popupWindow.dismiss();
                        break;
                    case R.id.change_bg_item:
                        choiceBgImg();
                        popupWindow.dismiss();
                        break;
                    case R.id.show_bg_item:
                        showBGImg();
                        popupWindow.dismiss();
                        break;
                    case R.id.change_head:
                        popupWindow.dismiss();
                        choiceHeadImg();
                        break;
                    case R.id.show_head:
                        popupWindow.dismiss();
                        startActivity(new Intent(mContext,ShowLoginHeadActivity.class));
                        break;
                    case R.id.show_msg_item:
                        popupWindow.dismiss();
                        Intent showMsigntent=new Intent(mContext,ShowUserMsgActivity.class);
                        showMsigntent.putExtra("userId",LoginActivity.loginUser.getU_id());
                        startActivity(showMsigntent);
                        break;
                    case R.id.change_msg_item:
                        popupWindow.dismiss();
                        startActivity(new Intent(mContext,EditUserMsgActivity.class));
                        break;
                }
            }
        };
        cancel_item.setOnClickListener(listener);
        change_bg_item.setOnClickListener(listener);
        show_bg_item.setOnClickListener(listener);
        changeHead.setOnClickListener(listener);
        showHead.setOnClickListener(listener);
        show_msg_item.setOnClickListener(listener);
        change_msg_item.setOnClickListener(listener);

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    private void choiceHeadImg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/photograph");
            ArrayList<String> checkPhotos=new ArrayList<>();
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(mContext)
                    .cameraFileDir(true ? takePhotoDir : null) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(1) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_HEAD_IMG);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }
    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    private void choiceBgImg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/photograph/");
            ArrayList<String> checkPhotos=new ArrayList<>();
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(mContext)
                    .cameraFileDir(true ? takePhotoDir : null) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(1) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_BG_IMG);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }
    @AfterPermissionGranted(Constant.REQUEST_CODE_SHOW_PHOTOS)
    public void showBGImg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/download/");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(mContext)
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            photoPreviewIntentBuilder.previewPhoto(LoginActivity.loginUser.getU_bg_img());
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_SHOW_PHOTOS, perms);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode== UCrop.REQUEST_CROP){
            if (resultCode == RESULT_OK){
                final Uri resultUri = UCrop.getOutput(data);
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("是否更换头像")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                                ((BaseActivity)mContext).showLoadingDialog(false,"更换头像中");
                                mineFragmentPresenter.changeHead(resultUri.getPath());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
            }else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                cropError.printStackTrace();
            }
        }else if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_HEAD_IMG) {
            List<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
            if (selectedPhotos.size()>0){
                File file=new File(Environment.getExternalStorageDirectory()+"/FlyMessage/temp/");
                if(!file.exists()){
                    file.mkdirs();
                }
                file=new File(Environment.getExternalStorageDirectory()+"/FlyMessage/temp/"+ UUID.randomUUID()+".jpg");
                UCrop.of(Uri.fromFile(new File(selectedPhotos.get(0))), Uri.fromFile(file))
                        .withAspectRatio(1, 1)
                        .start((LoginUserMsgActivity)mContext);
            }
        }else if(resultCode == RESULT_OK&&requestCode==RC_CHOOSE_BG_IMG){
            List<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
            if (selectedPhotos.size()>0){
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("是否更换名片背景")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                                ((BaseActivity)mContext).showLoadingDialog(false,"更换名片背景中");
                                mineFragmentPresenter.changeBgImg(selectedPhotos.get(0));
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initHeadImg(String headUrl) {
        ((BaseActivity)mContext).dismissLoadingDialog();
        ToastUtils.showToast("修改头像成功");
        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(headUrl)).into(headImg);
        LoginActivity.loginUser.setU_head_img(headUrl);
        FlyMessageApplication.getInstances().getDaoSession().getUserDao().update(LoginActivity.loginUser);
    }

    @Override
    public void initBgImg(String bgUrl) {
        ((BaseActivity)mContext).dismissLoadingDialog();
        ToastUtils.showToast("修改名片背景成功");
        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(bgUrl)).into(mainView);
        LoginActivity.loginUser.setU_bg_img(bgUrl);
        FlyMessageApplication.getInstances().getDaoSession().getUserDao().update(LoginActivity.loginUser);
    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {
        ((BaseActivity)mContext).dismissLoadingDialog();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        ((BaseActivity)mContext).dismissLoadingDialog();
    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode==Constant.REQUEST_CODE_PERMISSION_CHOICE_PHOTO){
            ToastUtils.showToast("你拒绝了读写存储空间权限");
        }else if (requestCode==Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO){
            ToastUtils.showToast("你拒绝了摄像头权限");
        }else if (requestCode==Constant.REQUEST_CODE_PERMISSION_CHOICE_FILE){
            ToastUtils.showToast("你拒绝了读写存储空间权限");
        }else if (requestCode==Constant.REQUEST_CODE_PERMISSION_RECORD_AUDIO){
            ToastUtils.showToast("你拒绝了录音和麦克风权限");
        }else if (requestCode==Constant.REQUEST_CODE_SHOW_PHOTOS){
            ToastUtils.showToast("访问设备上的照片权限");
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName()));
            startActivityForResult(intent, 1010);
        }
    }
}
