package com.example.flymessagedome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.contract.EditUserContract;
import com.example.flymessagedome.ui.contract.MineContract;
import com.example.flymessagedome.ui.presenter.EditUserPresenter;
import com.example.flymessagedome.ui.presenter.MineFragmentPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.DivisionModel;
import com.example.flymessagedome.utils.Divisions;
import com.example.flymessagedome.utils.ImageUtils;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import top.defaults.view.Division;
import top.defaults.view.DivisionPickerView;

import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_HEAD_IMG;

public class EditUserMsgActivity extends BaseActivity implements EditUserContract.View, MineContract.View,EasyPermissions.PermissionCallbacks {
    @BindView(R.id.head_img)
    CircleImageView head_img;
    @BindView(R.id.sign)
    TextView sign;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.position)
    TextView position;
    @BindView(R.id.u_name)
    EditText u_name;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.birthday)
    TextView birthday;
    @Inject
    EditUserPresenter editUserPresenter;
    @Inject
    MineFragmentPresenter mineFragmentPresenter;
    UserBean userBean;
    private DatePickerDialog dialog;
    private Calendar calendar;
    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_user_msg;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    public void initDatas() {
        userBean=new UserBean();
        name.setText(LoginActivity.loginUser.getU_nick_name());
        name.setHint(LoginActivity.loginUser.getU_nick_name());
        userBean.setU_nick_name(LoginActivity.loginUser.getU_nick_name());
        if (LoginActivity.loginUser.getU_sex()!=null){
            sex.setText(LoginActivity.loginUser.getU_sex());
            sex.setHint(LoginActivity.loginUser.getU_sex());
            userBean.setU_sex(LoginActivity.loginUser.getU_sex());
        }else {
            sex.setHint("你的性别");
        }
        if (!TextUtils.isEmpty(LoginActivity.loginUser.getU_sign())){
            sign.setHint(LoginActivity.loginUser.getU_sign());
            sign.setText(LoginActivity.loginUser.getU_sign());
            userBean.setU_sign(LoginActivity.loginUser.getU_sign());
        }else {
            sign.setHint("编辑签名");
        }
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        birthday.setText(format.format(LoginActivity.loginUser.getU_brithday()));
        u_name.setText(LoginActivity.loginUser.getU_name());
        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(LoginActivity.loginUser.getU_head_img())).into(head_img);
        if (!TextUtils.isEmpty(LoginActivity.loginUser.getU_position())){
            position.setText(LoginActivity.loginUser.getU_position());
            userBean.setU_position(LoginActivity.loginUser.getU_position());
        }
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userBean.setU_nick_name(s.toString());
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.back,R.id.complete,R.id.date_picker,R.id.sex_view,R.id.head_view,R.id.sign_view,R.id.birthday_view})
    public void onViewClick(View v){
        if (v.getId()==R.id.back){
            finish();
        }else if (v.getId()==R.id.complete){
            showLoadingDialog(false,"修改信息中");
            userBean.setU_sign(sign.getText().toString());
            editUserPresenter.updateUserMsg(userBean);
        }else if (v.getId()==R.id.date_picker){
            showPopueWindow();
        }else if (v.getId()==R.id.head_view){
            showHeadPopupMenu(mContext);
        }else if(v.getId()==R.id.sign_view){
            startActivity(new Intent(mContext,AddSignActivity.class));
        }else if(v.getId()==R.id.birthday_view){
            calendar = Calendar.getInstance();
            dialog=new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month++;
                    try {
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                        Date date=format.parse(year+"-"+month+"-"+dayOfMonth);
                        LoginActivity.loginUser.setU_brithday(date.getTime());
                        userBean.setU_brithday(date.getTime());
                        birthday.setText(format.format(LoginActivity.loginUser.getU_brithday()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }else {
            choiceSex(EditUserMsgActivity.this);
        }
    }
    @Override
    public void configViews() {
        editUserPresenter.attachView(this);
        mineFragmentPresenter.attachView(this);
    }

    @Override
    public void showError() {
        dismissLoadingDialog();
    }

    @Override
    public void showError(String msg) {
        dismissLoadingDialog();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
        ToastUtils.showToast("修改信息成功");
    }

    @Override
    public void tokenExceed() {

    }
    private void showPopueWindow(){
        final PopupWindow popupWindow;
        View popView = View.inflate(this,R.layout.choice_position_popup,null);
        final DivisionPickerView divisionPicker=popView.findViewById(R.id.divisionPicker);
        final TextView textView=popView.findViewById(R.id.textview);
        final List<DivisionModel> divisions = Divisions.get(this);
        Button btn=popView.findViewById(R.id.sure_btn);
        divisionPicker.setDivisions(divisions);
        divisionPicker.setOnSelectedDateChangedListener(new DivisionPickerView.OnSelectedDivisionChangedListener() {
            @Override
            public void onSelectedDivisionChanged(Division division) {
                String nowProvince=divisionPicker.getProvincePicker().getSelectedItem(divisionPicker.getSelectedDivision().getClass()).getText();
                String nowCity=divisionPicker.getCityPicker().getSelectedItem(divisionPicker.getSelectedDivision().getClass()).getText();
                String nowDivision=divisionPicker.getDivisionPicker().getSelectedItem(divisionPicker.getSelectedDivision().getClass()).getText();
                System.out.println(nowProvince+" "+nowCity+" "+nowDivision);
                textView.setText(nowProvince+" "+nowCity+" "+nowDivision);
            }
        });
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/2;
        popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = EditUserMsgActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                EditUserMsgActivity.this.getWindow().setAttributes(lp);
                if (!textView.getText().equals("请选择地区")){
                    String nowProvince=divisionPicker.getProvincePicker().getSelectedItem(divisionPicker.getSelectedDivision().getClass()).getText();
                    String nowCity=divisionPicker.getCityPicker().getSelectedItem(divisionPicker.getSelectedDivision().getClass()).getText();
                    String nowDivision=divisionPicker.getDivisionPicker().getSelectedItem(divisionPicker.getSelectedDivision().getClass()).getText();
                    System.out.println(nowProvince+" "+nowCity+" "+nowDivision);
                    position.setText(nowProvince+" "+nowCity+" "+nowDivision);
                    userBean.setU_position(position.getText().toString());
                }
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        this.getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void choiceSex(Context context) {
        final View popView = View.inflate(context,R.layout.choice_sex_popup,null);
        View man=popView.findViewById(R.id.man);
        View women=popView.findViewById(R.id.women);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = ImageUtils.dip2px(mContext,80);
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(false);
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
                    case R.id.man:
                        popupWindow.dismiss();
                        sex.setText("男");
                        userBean.setU_sex("男");
                        break;
                    case R.id.women:
                        popupWindow.dismiss();
                        sex.setText("女");
                        userBean.setU_sex("女");
                        break;
                }
            }
        };
        women.setOnClickListener(listener);
        man.setOnClickListener(listener);

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }

    @Override
    public void initHeadImg(String headUrl) {
        ((BaseActivity)mContext).dismissLoadingDialog();
        userBean.setU_head_img(headUrl);
        ToastUtils.showToast("修改头像成功");
        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(headUrl)).into(head_img);
        LoginActivity.loginUser.setU_head_img(headUrl);
        FlyMessageApplication.getInstances().getDaoSession().getUserDao().update(LoginActivity.loginUser);
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
                    case R.id.change_head:
                        popupWindow.dismiss();
                        choiceHeadImg();
                        break;
                    case R.id.show_head:
                        popupWindow.dismiss();
                        photoPreviewWrapper();
                        break;
                }
            }
        };
        cancel.setOnClickListener(listener);
        changeHead.setOnClickListener(listener);
        showHead.setOnClickListener(listener);

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    @AfterPermissionGranted(Constant.REQUEST_CODE_SHOW_PHOTOS)
    public void photoPreviewWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/download");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(mContext)
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            photoPreviewIntentBuilder.previewPhoto(LoginActivity.loginUser.getU_head_img());
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_SHOW_PHOTOS, perms);
        }
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
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UCrop.of(Uri.fromFile(new File(selectedPhotos.get(0))), Uri.fromFile(file))
                        .withAspectRatio(1, 1)
                        .start( EditUserMsgActivity.this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    @Override
    public void initBgImg(String bgUrl) {

    }
}
