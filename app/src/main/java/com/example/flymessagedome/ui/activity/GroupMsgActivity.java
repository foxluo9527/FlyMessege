package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.GroupMemberDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.GroupCreatorModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GroupMsgActivity extends BaseActivity {
    @BindView(R.id.group_name)
    TextView group_name;
    @BindView(R.id.group_num)
    TextView group_num;
    @BindView(R.id.group_create_time)
    TextView group_create_time;
    @BindView(R.id.group_introduce)
    TextView group_introduce;
    @BindView(R.id.head_img)
    ImageView head_img;
    @BindView(R.id.group_creator_head)
    CircleImageView group_creator_head;
    @BindView(R.id.add_group_view)
    View add_group_view;
    @BindView(R.id.group_chat_view)
    View group_chat_view;
    UserBean userBean;
    GroupBean groupBean;
    GroupMemberDao groupMemberDao=FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao();
    @Override
    public int getLayoutId() {
        return R.layout.activity_group_msg;
    }
    @OnClick({R.id.back,R.id.head_img,R.id.group_creator,R.id.group_qr_code,R.id.share_group,R.id.add_group,R.id.group_chat})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.head_img:
                showHeadImg();
                break;
            case R.id.group_creator:
                getGroupCreator();
                if (userBean==null){
                    ToastUtils.showToast("获取群主信息失败，请稍后重试");
                }else {
                    Intent intent=new Intent(mContext,ShowUserActivity.class);
                    intent.putExtra("userName",userBean.getU_name());
                    startActivity(intent);
                }
                break;
            case R.id.group_chat:
                Intent chatIntent=new Intent(mContext, GroupChatActivity.class);
                chatIntent.putExtra("groupId",groupBean.getG_id());
                startActivity(chatIntent);
                break;
            case R.id.group_qr_code:
            case R.id.share_group:
                Bundle bundle=new Bundle();
                bundle.putParcelable("group",groupBean);
                Intent intent=new Intent(mContext,ShareGroupActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.add_group:
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("确认加入此群聊")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                showLoadingDialog(false,"正在加入群聊");
                                new AsyncTask<Void,Void, Base>() {
                                    @Override
                                    protected Base doInBackground(Void... voids) {
                                        return HttpRequest.addGroupMember(groupBean.getG_id());
                                    }
                                    @Override
                                    protected void onPostExecute(Base base) {
                                        dismissLoadingDialog();
                                        if (base!=null&&base.code== Constant.SUCCESS){
                                            groupBean.setIsMember(true);
                                            add_group_view.setVisibility(View.GONE);
                                            group_chat_view.setVisibility(View.VISIBLE);
                                            ToastUtils.showToast("加入群聊成功");
                                        }else if (base!=null){
                                            ToastUtils.showToast(base.msg);
                                        }else  ToastUtils.showToast("加入群聊失败");
                                    }
                                }.execute();
                            }})
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
                break;
        }
    }
    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initDatas() {
        Bundle bundle=getIntent().getExtras();
        groupBean=bundle.getParcelable("group");
        if (groupBean==null){
            ToastUtils.showToast("获取群聊信息错误");
            return;
        }
        if (groupBean.getIsMember()){
            add_group_view.setVisibility(View.GONE);
            group_chat_view.setVisibility(View.VISIBLE);
        }else {
            add_group_view.setVisibility(View.VISIBLE);
            group_chat_view.setVisibility(View.GONE);
        }
        group_name.setText(""+groupBean.getG_name());
        group_introduce.setText(""+groupBean.getG_introduce());
        String url;
        if (groupBean.getG_head_img().contains("http")){
            url=FlyMessageApplication.getProxy(mContext).getProxyUrl(groupBean.getG_head_img());
        }else {
            url=groupBean.getG_head_img();
        }
        Glide.with(mContext).load(url)
                .error(R.drawable.icon)
                .placeholder(R.drawable.icon)
                .into(head_img);
        group_num.setText("群号:"+groupBean.getG_num());
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日");
        String dateTime=format.format(groupBean.getG_create_time());
        group_create_time.setText("本群聊创建于"+dateTime);
        getGroupCreator();
        group_num.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText("url", groupBean.getG_num()));
                ToastUtils.showToast("群号已复制在剪切板");
                return false;
            }
        });
    }
    private void getGroupCreator(){
        new AsyncTask<Void,Void, GroupCreatorModel>() {
            @Override
            protected GroupCreatorModel doInBackground(Void... voids) {
                String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
                if (loginToken==null){
                    return null;
                }
                return HttpRequest.getGroupCreator(groupBean.getG_id(),loginToken);
            }
            @Override
            protected void onPostExecute(GroupCreatorModel groupCreatorModel) {
                if (groupCreatorModel!=null&&groupCreatorModel.code== Constant.SUCCESS){
                    GroupMember creator=groupCreatorModel.getGroup_creator();
                    if (creator!=null){
                        creator.setIsCreator(true);
                        groupMemberDao.insertOrReplace(creator);
                        userBean=FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(groupCreatorModel.getGroup_creator().getU_id());
                        if (userBean!=null){
                            Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                                    .into(group_creator_head);
                        }else {
                            new AsyncTask<Void,Void, Users>() {
                                @Override
                                protected Users doInBackground(Void... voids) {
                                    String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
                                    if (loginToken==null){
                                        return null;
                                    }
                                    return HttpRequest.getUserMsg(Integer.parseInt(String.valueOf(groupCreatorModel.getGroup_creator().getU_id())),loginToken);
                                }
                                @Override
                                protected void onPostExecute(Users users) {
                                    if (users!=null&&users.code== Constant.SUCCESS){
                                        userBean=users.getUser();
                                        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                                                .into(group_creator_head);
                                    }else if (users!=null){
                                        ToastUtils.showToast(users.msg);
                                    }else  ToastUtils.showToast("获取群主信息失败");
                                }
                            }.execute();
                        }
                    }
                }else if (groupCreatorModel!=null){
                    ToastUtils.showToast(groupCreatorModel.msg);
                }else  ToastUtils.showToast("获取群主信息失败");
            }
        }.execute();
    }
    @AfterPermissionGranted(Constant.REQUEST_CODE_SHOW_PHOTOS)
    public void showHeadImg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/download/");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(mContext)
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            photoPreviewIntentBuilder.previewPhoto(groupBean.getG_head_img());
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_SHOW_PHOTOS, perms);
        }
    }
    @Override
    public void configViews() {

    }
}
