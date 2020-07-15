package com.example.flymessagedome.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupBeanDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.contract.CreateGroupContract;
import com.example.flymessagedome.ui.contract.EditGroupContract;
import com.example.flymessagedome.ui.presenter.CreateGroupPresenter;
import com.example.flymessagedome.ui.presenter.EditGroupPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkUtils;
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

public class EditGroupMsgActivity extends BaseActivity implements EditGroupContract.View {
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
    GroupBean groupBean;
    private GroupBeanDao groupBeanDao= FlyMessageApplication.getInstances().getDaoSession().getGroupBeanDao();
    @Inject
    EditGroupPresenter editGroupPresenter;
    @OnClick({R.id.back,R.id.change_cover_view,R.id.edit_introduce_view,R.id.cancel_btn,R.id.sure_btn,R.id.create})
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.change_cover_view:
                choiceHeadImg();
                break;
            case R.id.edit_introduce_view:
                if (eidtIntroduceView.getVisibility()!=View.VISIBLE){
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
                String name=nameEt.getText().toString();
                String introduce=introduceTv.getText().toString();
                if (TextUtils.isEmpty(name)){
                    ToastUtils.showToast("请输入群聊名称");
                    return;
                }
                if (TextUtils.isEmpty(introduce)){
                    introduce="暂无简介";
                }
                groupBean.setG_name(name);
                groupBean.setG_introduce(introduce);
                showLoadingDialog(true,"修改群聊信息中");
                editGroupPresenter.editGroup(groupBean);
                if (headPath!=null){
                    showLoadingDialog(true,"修改群聊头像中");
                    editGroupPresenter.changeGroupHead(Integer.parseInt(String.valueOf(groupBean.getG_id())),headPath);
                }
                break;
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
                    .selectedPhotos(checkPhotos) // 当前已选中的图片路径集合
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
                headPath=resultUri.getPath();
                Glide.with(mContext).load(headPath).into(coverImg);
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
                        .start( EditGroupMsgActivity.this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_group_msg;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void initDatas() {
        Bundle bundle=getIntent().getExtras();
        groupBean=bundle.getParcelable("group");
        if (groupBean==null){
            ToastUtils.showToast("获取群聊信息失败");
            finish();
            return;
        }
        if (!NetworkUtils.isConnected(mContext)){
            ToastUtils.showToast("请检查网络连接");
        }
        nameEt.setHint(groupBean.getG_name());
        nameEt.setText(groupBean.getG_name());
        introduceTv.setHint(groupBean.getG_introduce());
        introduceTv.setText(groupBean.getG_introduce());
        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(groupBean.getG_head_img()))
                .error(R.drawable.icon)
                .placeholder(R.drawable.icon)
                .into(coverImg);
    }

    @Override
    public void configViews() {
        editGroupPresenter.attachView(this);
    }

    @Override
    public void editSuccess() {
        dismissLoadingDialog();
        ToastUtils.showToast("修改群聊信息成功");
        groupBeanDao.update(groupBean);
    }

    @Override
    public void headSuccess(String head) {
        dismissLoadingDialog();
        ToastUtils.showToast("修改群聊头像成功");
        groupBean.setG_head_img(head);
        groupBeanDao.update(groupBean);
        headPath=null;
    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {
        dismissLoadingDialog();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
    }

    @Override
    public void tokenExceed() {

    }
}
