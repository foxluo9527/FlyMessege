package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.FriendsBeanDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.CheckBlackListModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.ImageUtils;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint("NonConstantResourceId")
public class ShowUserActivity extends BaseActivity implements MenuItem.OnMenuItemClickListener {
    String user_name;
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
    @BindView(R.id.add_fri_btn)
    Button add_fri;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_user;
    }

    UserBean userBean;
    boolean inBlackList = false;
    FriendsBean friendsBean;
    FriendsBeanDao friendsBeanDao = FlyMessageApplication.getInstances().getDaoSession().getFriendsBeanDao();

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    @SuppressLint({"StaticFieldLeak", "SetTextI18n"})
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
        if (NetworkUtils.isConnected(mContext)) {
            showLoadingDialog(true, "数据加载中");
            new AsyncTask<Void, Void, Users>() {
                @Override
                protected Users doInBackground(Void... voids) {
                    String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
                    return HttpRequest.getUserMsg(user_name, loginToken);
                }


                @Override
                protected void onPostExecute(Users user) {
                    if (user == null || user.code != Constant.SUCCESS) {
                        if (user != null)
                            ToastUtils.showToast(user.msg);
                        finish();
                        return;
                    }
                    userBean = user.getUser();
                    List<FriendsBean> friends = friendsBeanDao.queryBuilder()
                            .where(FriendsBeanDao.Properties.F_source_u_id.eq(LoginActivity.loginUser.getU_id()))
                            .where(FriendsBeanDao.Properties.F_object_u_id.eq(userBean.getU_id()))
                            .list();
                    if (friends.size() > 0) {
                        friendsBean = friends.get(0);
                    }
                    new AsyncTask<Void, Void, CheckBlackListModel>() {
                        @Override
                        protected CheckBlackListModel doInBackground(Void... voids) {
                            return HttpRequest.checkBlackList(userBean.getU_id());
                        }

                        @Override
                        protected void onPostExecute(CheckBlackListModel blackListModel) {
                            try {
                                dismissLoadingDialog();
                                inBlackList = blackListModel.inBlacklist;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute();
                    Glide.with(mContext)
                            .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                            .into(headImg);
                    Glide.with(mContext)
                            .asDrawable()
                            .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_bg_img()))
                            .into(mainView);
                    sign.setText(userBean.getU_sign());
                    if (userBean.getU_sex() == null) {
                        sex.setVisibility(View.GONE);
                    } else if (userBean.getU_sex().equals("男")) {
                        sex.setImageDrawable(AppCompatResources.getDrawable(mContext, R.mipmap.man));
                    } else if (userBean.getU_sex().equals("女")) {
                        sex.setImageDrawable(AppCompatResources.getDrawable(mContext, R.mipmap.women));
                    }
                    if (friendsBean != null && !friendsBean.getF_remarks_name().equals(userBean.getU_nick_name()))
                        nickName.setText(friendsBean.getF_remarks_name() + "(" + userBean.getU_nick_name() + ")");
                    else
                        nickName.setText(userBean.getU_nick_name());
                    u_name.setText(userBean.getU_name());
                    position.setText(userBean.getU_position());
                    if (userBean.getIsFriend()) {
                        add_fri.setVisibility(View.GONE);
                    } else {
                        add_fri.setVisibility(View.VISIBLE);
                    }
                }
            }.execute();
        } else {
            ToastUtils.showToast("获取用户信息失败，请检查网络连接");
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
            Glide.with(mContext)
                    .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_head_img()))
                    .into(headImg);
            Glide.with(mContext)
                    .asDrawable()
                    .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(userBean.getU_bg_img()))
                    .into(mainView);
            sign.setText(userBean.getU_sign());
            if (userBean.getU_sex() == null) {
                sex.setVisibility(View.GONE);
            } else if (userBean.getU_sex().equals("男")) {
                sex.setImageDrawable(AppCompatResources.getDrawable(mContext, R.mipmap.man));
            } else if (userBean.getU_sex().equals("女")) {
                sex.setImageDrawable(AppCompatResources.getDrawable(mContext, R.mipmap.women));
            }
            if (friendsBean != null && !friendsBean.getF_remarks_name().equals(userBean.getU_nick_name()))
                nickName.setText(friendsBean.getF_remarks_name() + "(" + userBean.getU_nick_name() + ")");
            else
                nickName.setText(userBean.getU_nick_name());
            u_name.setText(userBean.getU_name());
            position.setText(userBean.getU_position());
            if (userBean.getIsFriend()) {
                add_fri.setVisibility(View.GONE);
            } else {
                add_fri.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.back, R.id.user_set, R.id.send_btn, R.id.add_fri_btn, R.id.msg_view, R.id.u_head_img, R.id.show_bg_view})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.user_set:
                showListPopupMenu(mContext);
                break;
            case R.id.send_btn:
                Intent intent = new Intent(mContext, UserChatActivity.class);
                intent.putExtra("userId", userBean.getU_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
            case R.id.add_fri_btn:
                Intent rq_intent = new Intent(mContext, RequestFriendActivity.class);
                rq_intent.putExtra("uName", userBean.getU_name());
                startActivity(rq_intent);
                break;
            case R.id.msg_view:
                Intent showMsigntent = new Intent(mContext, ShowUserMsgActivity.class);
                showMsigntent.putExtra("userId", userBean.getU_id());
                startActivity(showMsigntent);
                break;
            case R.id.u_head_img:
                showHeadImg();
                break;
            case R.id.show_bg_view:
                showBGImg();
                break;
        }
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("StaticFieldLeak")
    private void showListPopupMenu(Context context) {
        final View popView = View.inflate(context, R.layout.friend_more_popup, null);
        TextView black_list_state = popView.findViewById(R.id.blacklist_state);
        TextView fri_state = popView.findViewById(R.id.fri_state);
        View changeRmk = popView.findViewById(R.id.change_rmk);
        View add_fri = popView.findViewById(R.id.add_fri);
        if (userBean.getIsFriend()) {
            fri_state.setText("删除好友");
        } else {
            changeRmk.setVisibility(View.GONE);
            fri_state.setText("添加好友");
        }
        if (inBlackList) {
            black_list_state.setText(R.string.move_out_blacklist);
        } else {
            black_list_state.setText(R.string.move_into_blacklist);
        }
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height;
        if (userBean.getIsFriend())
            height = ImageUtils.dip2px(mContext, 200);
        else {
            height = ImageUtils.dip2px(mContext, 165);
        }
        PopupWindow popupWindow = new PopupWindow(popView, weight, height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(() -> {
            WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
            lp.alpha = 1.0f;
            ((Activity) context).getWindow().setAttributes(lp);
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity) context).getWindow().setAttributes(lp);
        @SuppressLint("NonConstantResourceId") View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.change_rmk:
                    if (friendsBean != null) {
                        Intent rmkIntent = new Intent(mContext, ChangeRemarkNameActivity.class);
                        rmkIntent.putExtra("userId", userBean.getU_id());
                        rmkIntent.putExtra("fId", friendsBean.getF_id());
                        startActivity(rmkIntent);
                    }
                    popupWindow.dismiss();
                    break;
                case R.id.black_list:
                    showLoadingDialog(false, "正在操作");
                    new AsyncTask<Void, Void, Base>() {
                        @Override
                        protected Base doInBackground(Void... voids) {
                            CheckBlackListModel blackListModel = HttpRequest.checkBlackList(userBean.getU_id());
                            inBlackList = blackListModel.inBlacklist;
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
                                ToastUtils.showToast("操作成功");
                            } else {
                                ToastUtils.showToast("操作失败");
                            }
                        }
                    }.execute();
                    popupWindow.dismiss();
                    break;
                case R.id.add_fri:
                    if (userBean.getIsFriend()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setMessage("确认删除此联系人")
                                .setPositiveButton("确定", (dialog1, which) -> {
                                    dialog1.cancel();//取消弹出框
                                    showLoadingDialog(false, "正在删除");
                                    new AsyncTask<Void, Void, Base>() {
                                        @Override
                                        protected Base doInBackground(Void... voids) {
                                            return HttpRequest.delFriend(friendsBean.getF_id());
                                        }

                                        @Override
                                        protected void onPostExecute(Base base) {
                                            dismissLoadingDialog();
                                            if (base.code == Constant.SUCCESS) {
                                                ToastUtils.showToast("操作成功");
                                                initDatas();
                                                UserChatActivity.resultRefresh = true;
                                            } else {
                                                ToastUtils.showToast("操作失败");
                                            }
                                        }
                                    }.execute();
                                })
                                .setNegativeButton("取消", (dialog12, which) -> {
                                    dialog12.cancel();//取消弹出框
                                })
                                .create().show();
                    } else {
                        popupWindow.dismiss();
                        Intent rq_intent = new Intent(mContext, RequestFriendActivity.class);
                        rq_intent.putExtra("uName", userBean.getU_name());
                        startActivity(rq_intent);
                    }
                    break;
                case R.id.cancel:
                    popupWindow.dismiss();
                    break;
            }
        };
        changeRmk.setOnClickListener(listener);
        View black_list = popView.findViewById(R.id.black_list);
        black_list.setOnClickListener(listener);
        add_fri.setOnClickListener(listener);
        View cancel = popView.findViewById(R.id.cancel);
        cancel.setOnClickListener(listener);

        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_SHOW_PHOTOS)
    public void showHeadImg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/download/");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(mContext)
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            photoPreviewIntentBuilder.previewPhoto(userBean.getU_head_img());
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_SHOW_PHOTOS, perms);
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_SHOW_PHOTOS)
    public void showBGImg() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/download/");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(mContext)
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            photoPreviewIntentBuilder.previewPhoto(userBean.getU_bg_img());
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_SHOW_PHOTOS, perms);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        item.getItemId();
        return false;
    }
}
