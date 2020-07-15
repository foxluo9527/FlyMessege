package com.example.flymessagedome.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.base.BaseFragment;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.CheckBlackListModel;
import com.example.flymessagedome.ui.activity.AccountActivity;
import com.example.flymessagedome.ui.activity.AddSignActivity;
import com.example.flymessagedome.ui.activity.EditUserMsgActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.LoginUserMsgActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.activity.RequestFriendActivity;
import com.example.flymessagedome.ui.activity.SettingActivity;
import com.example.flymessagedome.ui.contract.MineContract;
import com.example.flymessagedome.ui.presenter.MineFragmentPresenter;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.FileUtil;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.ImageUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_BG_IMG;
import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_HEAD_IMG;
import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_PHOTO;

public class MineFragment extends BaseFragment{
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
    @BindView(R.id.sgin)
    TextView sgin;
    @BindView(R.id.msg_view)
    View msgView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_mine;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.setting_view,R.id.sgin_view,R.id.msg_view,R.id.change_msg,R.id.log_out})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.setting_view:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.msg_view:
                startActivity(new Intent(mContext, LoginUserMsgActivity.class));
                break;
            case R.id.sgin_view:
                startActivity(new Intent(mContext, AddSignActivity.class));
                break;
            case R.id.change_msg:
                startActivity(new Intent(mContext, EditUserMsgActivity.class));
                break;
            case R.id.log_out:
                startActivity(new Intent((MainActivity)mContext, AccountActivity.class));
                break;
        }
    }

    @Override
    public void initDatas() {
        if (LoginActivity.loginUser==null){
            return;
        }
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
        sgin.setText(LoginActivity.loginUser.getU_sign());
        Glide.with(mContext)
                .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(LoginActivity.loginUser.getU_head_img()))
                .into(headImg);
        u_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText("url", LoginActivity.loginUser.getU_name()));
                ToastUtils.showToast("用户名已复制到剪切板");
                return false;
            }
        });
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                initDatas();
            }
        });
    }

    @Override
    public void configViews() {

    }

}
