package com.example.flymessagedome.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupBeanDao;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.GroupMemberDao;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.GroupCreatorModel;
import com.example.flymessagedome.model.GroupMemberModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtil;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_BG_IMG;

public class GroupChatSettingActivity extends BaseActivity {
    @BindView(R.id.group_creator_head)
    CircleImageView group_creator_head;
    @BindView(R.id.head_img)
    ImageView headImg;
    @BindView(R.id.name_tv)
    TextView name;
    @BindView(R.id.group_name)
    TextView group_name;
    @BindView(R.id.group_num)
    TextView group_num;
    @BindView(R.id.group_introduce)
    TextView group_introduce;
    @BindView(R.id.group_remarks_name_tv)
    TextView group_remarks_name_tv;
    @BindView(R.id.chat_top_switch)
    Switch chat_top_switch;
    @BindView(R.id.message_important_switch)
    Switch message_important_switch;
    @BindView(R.id.edit_group_msg)
    View edit_group_msg;
    UserBean userBean;
    GroupBean groupBean;
    ChatDao chatDao=FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    GroupMemberDao groupMemberDao=FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao();
    MessageDao messageDao=FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    GroupBeanDao groupBeanDao=FlyMessageApplication.getInstances().getDaoSession().getGroupBeanDao();
    Chat userChat;
    GroupMember groupMember;
    GroupMember creator;
    @Override
    public int getLayoutId() {
        return R.layout.activity_group_chat_setting;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }
    @OnClick({R.id.back,R.id.group_msg,R.id.chat_bg_img,R.id.chat_top,R.id.share_group,R.id.message_important,R.id.chat_del,R.id.group_exit
    ,R.id.group_remarks_name,R.id.edit_group_msg,R.id.group_members})
    public void onViewClick(View v){
        Bundle bundle=new Bundle();
        bundle.putParcelable("group",groupBean);
        Intent intent;
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.group_msg:
                intent=new Intent(mContext,GroupMsgActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.group_members:
                intent=new Intent(mContext,ShowGroupMembersActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.chat_bg_img:
                choiceBg();
                break;
            case R.id.share_group:
                intent=new Intent(mContext,ShareGroupActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.group_remarks_name:
                if (groupMember==null){
                    ToastUtils.showToast("获取群成员信息失败");
                    return;
                }
                intent=new Intent(mContext,ChangeGroupNickNameActivity.class);
                bundle=new Bundle();
                bundle.putParcelable("group",groupBean);
                bundle.putString("remarkName",groupMember.getG_nick_name());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.edit_group_msg:
                if (groupBean==null){
                    ToastUtils.showToast("获取群聊信息失败");
                    return;
                }
                intent=new Intent(mContext,EditGroupMsgActivity.class);
                bundle=new Bundle();
                bundle.putParcelable("group",groupBean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.chat_top:
                chat_top_switch.setChecked(!chat_top_switch.isChecked());
                break;
            case R.id.message_important:
                message_important_switch.setChecked(!message_important_switch.isChecked());
                break;
            case R.id.group_exit:
                String exitString="确认退出群聊？";
                if (creator!=null&&creator.getU_id()==LoginActivity.loginUser.getU_id()){
                    exitString="您为群主退出将会解散群聊，确认退出群聊？";
                }
                dialog.setMessage(exitString)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                exitGroup();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
                break;
            case R.id.chat_del:
                dialog.setMessage("是否清除所有聊天记录")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                messageDao.queryBuilder().where(MessageDao.Properties.CId.eq(userChat.getC_id())).buildDelete().executeDeleteWithoutDetachingEntities();
                                userChat.setChat_reshow(false);
                                chatDao.update(userChat);
                                ToastUtils.showToast("清除聊天记录成功");
                                GroupChatActivity.resultRefresh=true;
                            }
                        })
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
    protected void onResume() {
        super.onResume();
        List<GroupMember> members =groupMemberDao.queryBuilder()
                .where(GroupMemberDao.Properties.G_id.eq(groupBean.getG_id()))
                .where(GroupMemberDao.Properties.U_id.eq(LoginActivity.loginUser.getU_id()))
                .list();
        if (members.size()>0){
            groupMember=members.get(0);
        }
        if (groupMember!=null)
        group_remarks_name_tv.setText(groupMember.getG_nick_name());
        groupBean=groupBeanDao.load(groupBean.getG_id());
        if (groupBean != null) {
            String url;
            if (groupBean.getG_head_img().contains("http")){
                url=FlyMessageApplication.getProxy(mContext).getProxyUrl(groupBean.getG_head_img());
            }else {
                url=groupBean.getG_head_img();
            }
            Glide.with(mContext).load(url)
                    .into(headImg);
            name.setText(groupBean.getG_name());
            group_introduce.setText(groupBean.getG_introduce());
        }
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
        getGroupCreator();
        getMyGroupMsg();
        userChat=chatDao.queryBuilder()
                .where(ChatDao.Properties.Source_g_id.eq(groupBean.getG_id()))
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(ChatDao.Properties.Chat_type.eq(1))
                .unique();
        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(groupBean.getG_head_img()))
                .into(headImg);
        name.setText(groupBean.getG_name());
        group_introduce.setText(groupBean.getG_introduce());
        group_name.setText(groupBean.getG_name());
        group_num.setText(groupBean.getG_num());
        message_important_switch.setChecked(!userChat.getChat_show_remind());
        message_important_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userChat.setChat_show_remind(!isChecked);
                chatDao.update(userChat);
            }
        });
        chat_top_switch.setChecked(userChat.getChat_up());
        chat_top_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userChat.setChat_up(isChecked);
                chatDao.update(userChat);
            }
        });
    }

    @Override
    public void configViews() {

    }
    private void  getMyGroupMsg(){
        List<GroupMember> members =groupMemberDao.queryBuilder()
                .where(GroupMemberDao.Properties.G_id.eq(groupBean.getG_id()))
                .where(GroupMemberDao.Properties.U_id.eq(Integer.parseInt(String.valueOf(LoginActivity.loginUser.getU_id()))))
                .list();
        if (members.size()>0){
            groupMember=members.get(0);
        }
        if (groupMember==null){
            new AsyncTask<Void,Void, GroupMemberModel>() {
                @Override
                protected GroupMemberModel doInBackground(Void... voids) {
                    String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
                    return HttpRequest.getGroupMember(Integer.parseInt(String.valueOf(groupBean.getG_id())),Integer.parseInt(String.valueOf(LoginActivity.loginUser.getU_id())), loginToken);
                }

                @Override
                protected void onPostExecute(GroupMemberModel groupMemberModel) {
                    super.onPostExecute(groupMemberModel);
                    if (groupMemberModel!=null&&groupMemberModel.code==Constant.SUCCESS){
                        groupMember=groupMemberModel.getGroup_member();
                        if (groupMember.getPower()==1){
                            edit_group_msg.setVisibility(View.VISIBLE);
                        }
                        groupMember=groupMemberModel.getGroup_member();
                        groupMemberDao.insertOrReplace(groupMember);
                        group_remarks_name_tv.setText(groupMember.getG_nick_name());
                    }else if (groupMemberModel!=null){
                        ToastUtils.showToast(groupMemberModel.msg);
                    }else {
                        ToastUtils.showToast("获取用户群聊资料失败，请稍后重试");
                    }
                }
            }.execute();
        }else {
            group_remarks_name_tv.setText(groupMember.getG_nick_name());
            if (groupMember.getPower()==1){
                edit_group_msg.setVisibility(View.VISIBLE);
            }
        }
    }
    private void getGroupCreator(){
        creator=groupMemberDao.queryBuilder()
                .where(GroupMemberDao.Properties.G_id.eq(groupBean.getG_id()))
                .where(GroupMemberDao.Properties.Power.eq(1))
                .unique();
        if (creator==null){
            showLoadingDialog(true,"加载中...");
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
                    dismissLoadingDialog();
                    if (groupCreatorModel!=null&&groupCreatorModel.code== Constant.SUCCESS){
                        GroupMember creator=groupCreatorModel.getGroup_creator();
                        if (creator!=null){
                            creator.setIsCreator(true);
                            groupMemberDao.insertOrReplace(creator);
                            userBean= FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(creator.getU_id());
                            if (userBean!=null){
                                Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                                        .into(group_creator_head);
                            }else {
                                getCreatorMsg(creator);
                            }
                        }
                    }else if (groupCreatorModel==null){
                        ToastUtils.showToast(groupCreatorModel.msg);
                    }else  ToastUtils.showToast("获取群主信息失败");
                }
            }.execute();
        }else {
            userBean= FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(creator.getU_id());
            if (userBean!=null){
                Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                        .into(group_creator_head);
            }else {
                getCreatorMsg(creator);
            }
        }
    }
    private void getCreatorMsg(GroupMember creator){
        userBean=FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(creator.getU_id());
        if (userBean==null){
            new AsyncTask<Void,Void, Users>() {
                @Override
                protected Users doInBackground(Void... voids) {
                    String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
                    if (loginToken==null){
                        return null;
                    }
                    return HttpRequest.getUserMsg(Integer.parseInt(String.valueOf(creator.getU_id())),loginToken);
                }
                @Override
                protected void onPostExecute(Users users) {
                    if (users!=null&&users.code== Constant.SUCCESS){
                        userBean=users.getUser();
                        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                                .into(group_creator_head);
                        FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().insertOrReplace(userBean);
                    }else if (users!=null){
                        ToastUtils.showToast(users.msg);
                    }else  ToastUtils.showToast("获取群主信息失败");
                }
            }.execute();
        }else {
            Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                    .into(group_creator_head);
        }
    }
    private void exitGroup(){
        new AsyncTask<Void,Void, Base>() {
            @Override
            protected Base doInBackground(Void... voids) {
                if (creator!=null&&creator.getU_id()==LoginActivity.loginUser.getU_id()){
                    return HttpRequest.delGroup(groupBean.getG_id());
                }else {
                    return HttpRequest.exitGroup(groupBean.getG_id(),LoginActivity.loginUser.getU_id());
                }
            }

            @Override
            protected void onPostExecute(Base base) {
                super.onPostExecute(base);
                if (base!=null&&base.code==Constant.SUCCESS){
                    groupBeanDao.delete(groupBean);
                    chatDao.queryBuilder().where(ChatDao.Properties.Source_g_id.eq(groupBean.getG_id())).buildDelete().executeDeleteWithoutDetachingEntities();
                    groupMemberDao.queryBuilder().where(GroupMemberDao.Properties.G_id.eq(groupBean.getG_id())).buildDelete().executeDeleteWithoutDetachingEntities();
                    messageDao.queryBuilder().where(MessageDao.Properties.M_source_g_id.eq(groupBean.getG_id())).where(MessageDao.Properties.Login_u_id.eq(LoginActivity.loginUser.getU_id())).buildDelete().executeDeleteWithoutDetachingEntities();
                    GroupChatActivity.resultRefresh=true;
                    finish();
                }else if (base!=null){
                    ToastUtils.showToast(base.msg);
                }else {
                    ToastUtils.showToast("解散群聊失败，请稍后重试");
                }
            }
        }.execute();
    }
    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    private void choiceBg() {
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
            startActivityForResult(photoPickerIntent, RC_CHOOSE_BG_IMG);
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
                dialog.setMessage("是否更换聊天背景")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                userChat.setBgImg(resultUri.getPath());
                                chatDao.update(userChat);
                                ToastUtils.showToast("更换聊天背景成功");
                                GroupChatActivity.resultRefresh=true;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
            }else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                cropError.printStackTrace();
            }
        }else if(resultCode == RESULT_OK&&requestCode==RC_CHOOSE_BG_IMG){
            List<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
            if (selectedPhotos.size()>0){
                File file=new File(Environment.getExternalStorageDirectory()+"/FlyMessage/temp/");
                if(!file.exists()){
                    file.mkdirs();
                }
                file=new File(Environment.getExternalStorageDirectory()+"/FlyMessage/temp/"+ UUID.randomUUID()+".jpg");
                UCrop.of(Uri.fromFile(new File(selectedPhotos.get(0))), Uri.fromFile(file))
                        .withAspectRatio(9, 16)
                        .start((GroupChatSettingActivity)mContext);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
