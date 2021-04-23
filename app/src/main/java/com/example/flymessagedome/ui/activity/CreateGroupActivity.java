package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.contract.CreateGroupContract;
import com.example.flymessagedome.ui.presenter.CreateGroupPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.ToastUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_HEAD_IMG;

@SuppressLint("NonConstantResourceId")
public class CreateGroupActivity extends BaseActivity implements CreateGroupContract.View {

    @BindView(R.id.cover_img)
    ImageView coverImg;
    @BindView(R.id.edit_name)
    EditText nameEt;
    @BindView(R.id.introduce_text)
    TextView introduceTv;
    @BindView(R.id.edit_introduce)
    MultiAutoCompleteTextView introduceEt;
    @BindView(R.id.show_edit_view)
    View eidtIntroduceView;
    String headPath;

    @Inject
    CreateGroupPresenter createGroupPresenter;

    @OnClick({R.id.back, R.id.change_cover_view, R.id.edit_introduce_view, R.id.cancel_btn, R.id.sure_btn, R.id.create})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.change_cover_view:
                choiceHeadImg();
                break;
            case R.id.edit_introduce_view:
                if (eidtIntroduceView.getVisibility() != View.VISIBLE) {
                    eidtIntroduceView.setVisibility(View.VISIBLE);
                    introduceEt.setText(introduceTv.getText());
                }
                break;
            case R.id.cancel_btn:
                eidtIntroduceView.setVisibility(View.GONE);
                break;
            case R.id.sure_btn:
                introduceTv.setText(introduceEt.getText().toString());
                eidtIntroduceView.setVisibility(View.GONE);
                break;
            case R.id.create:
                String name = nameEt.getText().toString();
                String introduce = introduceEt.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast("请输入群聊名称");
                    return;
                }
                if (TextUtils.isEmpty(introduce)) {
                    introduce = "暂无简介";
                }
                showLoadingDialog(false, "创建群聊中");
                createGroupPresenter.createGroup(name, introduce);
                break;
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    private void choiceHeadImg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/photograph");
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(mContext)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
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
        try {
            if (requestCode == UCrop.REQUEST_CROP) {
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    headPath = resultUri.getPath();
                    Glide.with(mContext).load(headPath).into(coverImg);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    cropError.printStackTrace();
                }
            } else if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_HEAD_IMG) {
                List<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
                if (selectedPhotos.size() > 0) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/FlyMessage/temp/");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file = new File(Environment.getExternalStorageDirectory() + "/FlyMessage/temp/" + UUID.randomUUID() + ".jpg");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    UCrop.of(Uri.fromFile(new File(selectedPhotos.get(0))), Uri.fromFile(file))
                            .withAspectRatio(1, 1)
                            .start(CreateGroupActivity.this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_group;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        createGroupPresenter.attachView(this);
    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {
        try {
            dismissLoadingDialog();
            ToastUtils.showToast(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {

    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public void createSuccess(int g_id) {
        try {
            dismissLoadingDialog();
            if (headPath != null) {
                showLoadingDialog(true, "设置群头像中");
                createGroupPresenter.changeGroupHead(g_id, headPath);
            } else {
                ToastUtils.showToast("创建群聊成功");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void headSuccess() {
        try {
            dismissLoadingDialog();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
