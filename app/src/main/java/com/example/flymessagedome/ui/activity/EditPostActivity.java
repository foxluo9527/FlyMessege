package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Post;
import com.example.flymessagedome.ui.contract.EditPostContract;
import com.example.flymessagedome.ui.presenter.EditPostPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.flymessagedome.ui.activity.AddPostActivity.PRC_PHOTO_PICKER;
import static com.example.flymessagedome.ui.activity.AddPostActivity.RC_PHOTO_PREVIEW;
import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_PHOTO;

@SuppressLint("NonConstantResourceId")
public class EditPostActivity extends BaseActivity implements EditPostContract.View, EasyPermissions.PermissionCallbacks, BGASortableNinePhotoLayout.Delegate {

    @BindView(R.id.snpl_moment_add_photos)
    BGASortableNinePhotoLayout mPhotosSnpl;

    @BindView(R.id.save)
    Button save;

    @BindView(R.id.content)
    EditText content;

    private Post.PostBean postBean;
    private final ArrayList<String> addPhotos = new ArrayList<>();
    private HttpProxyCacheServer proxy;
    @Inject
    EditPostPresenter postPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.bar_bg));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_post;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initDatas() {
        String json = getIntent().getStringExtra("post");
        if (TextUtils.isEmpty(json)) {
            finish();
            return;
        }
        postBean = JSON.parseObject(json, Post.PostBean.class);
        content.setText(postBean.getCommunityPostContent() + "");
        ArrayList<String> data = new ArrayList<>();
        proxy = FlyMessageApplication.getProxy(mContext);
        for (Post.PostBean.PostItemsBean postItem : postBean.getPostItems()) {
            data.add(proxy.getProxyUrl(postItem.getCommunity_post_item_url()));
        }
        mPhotosSnpl.setData(data);
        mPhotosSnpl.setDelegate(this);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                save.setEnabled(!TextUtils.isEmpty(content.getText().toString()) || mPhotosSnpl.getData().size() > 0);
            }
        });
        save.setEnabled(!TextUtils.isEmpty(content.getText().toString()) || mPhotosSnpl.getData().size() > 0);
    }

    @OnClick({R.id.back, R.id.save})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.save:
                new AlertDialog.Builder(mContext)
                        .setMessage("确认保存?")
                        .setPositiveButton("确定", (dialog, which) -> {
                            showLoadingDialog(false, "保存中...");
                            postPresenter.editContent(postBean.getCommunity_post_id(), content.getText().toString());
                        })
                        .setNegativeButton("取消", null).show();
                break;
        }
    }

    @Override
    public void configViews() {
        postPresenter.attachView(this);
    }

    @Override
    public void editSuccess() {
        ArrayList<Integer> removeItemIds = new ArrayList<>();
        for (String photo : mPhotosSnpl.getData()) {
            if (photo.contains("com.example.flymessagedome/cache/video-cache")) {
                for (int i = 0; i < postBean.getPostItems().size(); i++) {
                    Post.PostBean.PostItemsBean postItem = postBean.getPostItems().get(i);
                    if (proxy.getProxyUrl(postItem.getCommunity_post_item_url()).equals(photo)) {
                        postBean.getPostItems().remove(i);
                        break;
                    }
                }
            } else {
                addPhotos.add(photo);
            }
        }
        for (Post.PostBean.PostItemsBean postItem : postBean.getPostItems()) {
            removeItemIds.add(postItem.getCommunity_post_item_id());
        }
        Intent result = new Intent();
        result.putStringArrayListExtra("photos", addPhotos);
        setResult(2, result);
        if (removeItemIds.size() > 0) {
            postPresenter.removeItems(removeItemIds);
        } else {
            dismissLoadingDialog();
            finish();
        }
    }

    @Override
    public void removeFailed() {
        dismissLoadingDialog();
        finish();
    }

    @Override
    public void removeSuccess() {
        dismissLoadingDialog();
        finish();
    }

    @Override
    public void showError() {
        dismissLoadingDialog();
    }

    @Override
    public void showError(String msg) {
        dismissLoadingDialog();
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
    }

    @Override
    public void tokenExceed() {

    }

    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "take_photos");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(mPhotosSnpl.getMaxItemCount() - mPhotosSnpl.getItemCount()) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, perms);
        }
    }


    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosSnpl.removeItem(position);
        save.setEnabled(!TextUtils.isEmpty(content.getText().toString()) || mPhotosSnpl.getData().size() > 0);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos(models) // 当前预览的图片路径集合
                .selectedPhotos(models) // 当前已选中的图片路径集合
                .maxChooseCount(mPhotosSnpl.getMaxItemCount()) // 图片选择张数的最大值
                .currentPosition(position) // 当前预览图片的索引
                .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> photos;
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            photos = BGAPhotoPickerActivity.getSelectedPhotos(data);
            mPhotosSnpl.addMoreData(photos);
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            photos = BGAPhotoPickerPreviewActivity.getSelectedPhotos(data);
            mPhotosSnpl.setData(photos);
        }
        save.setEnabled(!TextUtils.isEmpty(content.getText().toString()) || mPhotosSnpl.getData().size() > 0);
    }
}