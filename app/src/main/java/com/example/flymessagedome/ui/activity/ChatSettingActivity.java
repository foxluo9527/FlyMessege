package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.FriendsBeanDao;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.CheckBlackListModel;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;
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

@SuppressLint({"NonConstantResourceId", "StaticFieldLeak"})
public class ChatSettingActivity extends BaseActivity {
    String user_name;

    @BindView(R.id.head_img)
    ImageView headImg;
    @BindView(R.id.name_tv)
    TextView name;
    @BindView(R.id.message_record)
    View message_record;
    @BindView(R.id.chat_bg_img)
    View chat_bg_img;
    @BindView(R.id.chat_top)
    View chat_top;
    @BindView(R.id.message_important)
    View message_important;
    @BindView(R.id.message_blacklist)
    View message_blacklist;
    @BindView(R.id.chat_del)
    View chat_del;
    @BindView(R.id.friend_del)
    View friend_del;
    @BindView(R.id.add_fri_btn)
    Button add_fri_btn;
    @BindView(R.id.chat_top_switch)
    Switch chat_top_switch;
    @BindView(R.id.message_important_switch)
    Switch message_important_switch;
    @BindView(R.id.message_blacklist_switch)
    Switch message_blacklist_switch;
    UserBean userBean;
    boolean inBlackList = false;
    FriendsBean friendsBean;
    FriendsBeanDao friendsBeanDao = FlyMessageApplication.getInstances().getDaoSession().getFriendsBeanDao();
    ChatDao chatDao = FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    MessageDao messageDao = FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    Chat userChat;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat_setting;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.back, R.id.user_msg, R.id.message_record, R.id.chat_bg_img, R.id.chat_top, R.id.message_important, R.id.message_blacklist, R.id.chat_del
            , R.id.friend_del, R.id.add_fri_btn})
    public void onViewClick(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.user_msg:
                Intent showIntent = new Intent(mContext, ShowUserActivity.class);
                showIntent.putExtra("userName", user_name);
                startActivity(showIntent);
                finish();
                break;
            case R.id.message_record:
                Intent recordIntent = new Intent(mContext, MessageRecordActivity.class);
                recordIntent.putExtra("userId", userBean.getU_id());
                startActivity(recordIntent);
                break;
            case R.id.chat_bg_img:
                choiceBg();
                break;
            case R.id.chat_top:
                chat_top_switch.setChecked(!chat_top_switch.isChecked());
                break;
            case R.id.message_important:
                message_important_switch.setChecked(!message_important_switch.isChecked());
                break;
            case R.id.message_blacklist:
                message_blacklist_switch.setChecked(!message_blacklist_switch.isChecked());
                break;
            case R.id.chat_del:
                dialog.setMessage("是否清除所有聊天记录")
                        .setPositiveButton("删除", (dialog1, which) -> {
                            dialog1.cancel();
                            messageDao.queryBuilder().where(MessageDao.Properties.CId.eq(userChat.getC_id())).buildDelete().executeDeleteWithoutDetachingEntities();
                            userChat.setChat_reshow(false);
                            chatDao.update(userChat);
                            ToastUtils.showToast("清除聊天记录成功");
                            UserChatActivity.resultRefresh = true;
                        })
                        .setNegativeButton("取消", (dialog12, which) -> dialog12.cancel())
                        .create().show();
                break;
            case R.id.friend_del:
                dialog.setMessage("是否删除该好友")
                        .setPositiveButton("删除", (dialog13, which) -> {
                            dialog13.cancel();
                            showLoadingDialog(false, "正在删除");
                            new AsyncTask<Void, Void, Base>() {
                                @Override
                                protected Base doInBackground(Void... voids) {
                                    return HttpRequest.delFriend(friendsBean.getF_id());
                                }

                                @Override
                                protected void onPostExecute(Base base) {
                                    try {
                                        dismissLoadingDialog();
                                        if (base.code == Constant.SUCCESS) {
                                            ToastUtils.showToast("操作成功");
                                            friendsBean = null;
                                            userBean.setIsFriend(false);
                                            FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().update(userBean);
                                            initDatas();
                                            UserChatActivity.resultRefresh = true;
                                        } else {
                                            ToastUtils.showToast("操作失败");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.execute();
                        })
                        .setNegativeButton("取消", (dialog14, which) -> dialog14.cancel())
                        .create().show();
                break;
            case R.id.add_fri_btn:
                Intent rq_intent = new Intent(mContext, RequestFriendActivity.class);
                rq_intent.putExtra("uName", userBean.getU_name());
                startActivity(rq_intent);
                break;

        }
    }

    @Override
    public void initDatas() {
        user_name = getIntent().getStringExtra("userName");
        if (TextUtils.isEmpty(user_name)) {
            ToastUtils.showToast("用户名错误");
            finish();
            return;
        }
        if (user_name.equals(LoginActivity.loginUser.getU_name())) {
            startActivity(new Intent(mContext, LoginUserMsgActivity.class));
            finish();
            return;
        }
        userBean = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().queryBuilder().where(UserBeanDao.Properties.U_name.eq(user_name)).list().get(0);
        if (userBean == null) {
            finish();
            return;
        }
        List<FriendsBean> friends = friendsBeanDao.queryBuilder()
                .where(FriendsBeanDao.Properties.F_source_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(FriendsBeanDao.Properties.F_object_u_id.eq(userBean.getU_id()))
                .list();
        if (friends.size() > 0) {
            friendsBean = friends.get(0);
        }
        if (NetworkUtils.isConnected(mContext)) {
            showLoadingDialog(false, "获取好友信息中");
            new AsyncTask<Void, Void, CheckBlackListModel>() {
                @Override
                protected CheckBlackListModel doInBackground(Void... voids) {
                    return HttpRequest.checkBlackList(userBean.getU_id());
                }

                @Override
                protected void onPostExecute(CheckBlackListModel blackListModel) {
                    try {
                        dismissLoadingDialog();
                        if (blackListModel != null)
                            inBlackList = blackListModel.inBlacklist;
                        if (inBlackList) {
                            message_blacklist_switch.setChecked(true);
                        }
                        message_blacklist_switch.setChecked(inBlackList);
                        message_blacklist_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            showLoadingDialog(false, "正在操作");
                            new AsyncTask<Void, Void, Base>() {
                                @Override
                                protected Base doInBackground(Void... voids) {
                                    CheckBlackListModel blackListModel1 = HttpRequest.checkBlackList(userBean.getU_id());
                                    inBlackList = blackListModel1.inBlacklist;
                                    if (inBlackList) {
                                        return HttpRequest.delBlackList(userBean.getU_id());
                                    } else {
                                        return HttpRequest.addBlackList(userBean.getU_id());
                                    }
                                }

                                @Override
                                protected void onPostExecute(Base base) {
                                    dismissLoadingDialog();
                                    if (base.code == Constant.SUCCESS) {
                                        inBlackList = !inBlackList;
                                        message_blacklist_switch.setChecked(inBlackList);
                                        ToastUtils.showToast("操作成功");
                                    } else {
                                        ToastUtils.showToast("操作失败");
                                    }
                                }
                            }.execute();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } else {
            ToastUtils.showToast("获取好友信息失败，请检查网络连接");
        }
        userChat = chatDao.queryBuilder()
                .where(ChatDao.Properties.Source_id.eq(userBean.getU_id()))
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(ChatDao.Properties.Chat_type.eq(0))
                .unique();
        Glide.with(mContext)
                .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                .into(headImg);
        if (friendsBean != null && !friendsBean.getF_remarks_name().equals(userBean.getU_nick_name()))
            name.setText(friendsBean.getF_remarks_name());
        else
            name.setText(userBean.getU_nick_name());
        if (!userBean.getIsFriend() || friendsBean == null) {
            message_blacklist.setVisibility(View.GONE);
            message_important.setVisibility(View.GONE);
            message_record.setVisibility(View.GONE);
            chat_bg_img.setVisibility(View.GONE);
            chat_top.setVisibility(View.GONE);
            add_fri_btn.setVisibility(View.VISIBLE);
            friend_del.setVisibility(View.GONE);
        } else {
            message_important_switch.setChecked(!userChat.getChat_show_remind());
            message_important_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                userChat.setChat_show_remind(!isChecked);
                chatDao.update(userChat);
            });
            chat_top_switch.setChecked(userChat.getChat_up());
            chat_top_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                userChat.setChat_up(isChecked);
                chatDao.update(userChat);
            });
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    private void choiceBg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/photograph");
            ArrayList<String> checkPhotos = new ArrayList<>();
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(mContext)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
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
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                final Uri resultUri = UCrop.getOutput(data);
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("是否更换聊天背景")
                        .setPositiveButton("确定", (dialog1, which) -> {
                            dialog1.cancel();
                            userChat.setBgImg(resultUri.getPath());
                            ToastUtils.showToast("更换聊天背景成功");
                            UserChatActivity.resultRefresh = true;
                        })
                        .setNegativeButton("取消", (dialog12, which) -> dialog12.cancel())
                        .create().show();
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                cropError.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_BG_IMG) {
            List<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
            if (selectedPhotos.size() > 0) {
                File file = new File(Environment.getExternalStorageDirectory() + "/FlyMessage/temp/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(Environment.getExternalStorageDirectory() + "/FlyMessage/temp/" + UUID.randomUUID() + ".jpg");
                UCrop.of(Uri.fromFile(new File(selectedPhotos.get(0))), Uri.fromFile(file))
                        .withAspectRatio(9, 16)
                        .start((ChatSettingActivity) mContext);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void configViews() {

    }
}
