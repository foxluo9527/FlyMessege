package com.example.flymessagedome.ui.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseFragment;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.ui.activity.AccountActivity;
import com.example.flymessagedome.ui.activity.AddSignActivity;
import com.example.flymessagedome.ui.activity.EditUserMsgActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.LoginUserMsgActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.activity.SettingActivity;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class MineFragment extends BaseFragment {
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
    @BindView(R.id.sign)
    TextView sign;
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

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.setting_view, R.id.sign_view, R.id.msg_view, R.id.change_msg, R.id.log_out})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.setting_view:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.msg_view:
                startActivity(new Intent(mContext, LoginUserMsgActivity.class));
                break;
            case R.id.sign_view:
                startActivity(new Intent(mContext, AddSignActivity.class));
                break;
            case R.id.change_msg:
                startActivity(new Intent(mContext, EditUserMsgActivity.class));
                break;
            case R.id.log_out:
                startActivity(new Intent((MainActivity) mContext, AccountActivity.class));
                break;
        }
    }

    @Override
    public void initDatas() {
        if (LoginActivity.loginUser == null) {
            return;
        }
        u_name.setText(LoginActivity.loginUser.getU_name());
        nickName.setText(LoginActivity.loginUser.getU_nick_name());
        if (TextUtils.isEmpty(LoginActivity.loginUser.getU_sex())) {
            sex.setVisibility(View.GONE);
        } else {
            sex.setVisibility(View.VISIBLE);
            if (LoginActivity.loginUser.getU_sex().equals("男")) {
                sex.setImageDrawable(AppCompatResources.getDrawable(mContext, R.mipmap.man));
            } else {
                sex.setImageDrawable(AppCompatResources.getDrawable(mContext, R.mipmap.women));
            }
        }
        position.setText(LoginActivity.loginUser.getU_position());
        sign.setText(LoginActivity.loginUser.getU_sign());
        Glide.with(mContext)
                .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(LoginActivity.loginUser.getU_head_img()))
                .into(headImg);
        u_name.setOnLongClickListener(v -> {
            ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setPrimaryClip(ClipData.newPlainText("url", LoginActivity.loginUser.getU_name()));
            ToastUtils.showToast("用户名已复制到剪切板");
            return false;
        });
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            initDatas();
        });
    }

    @Override
    public void configViews() {

    }

}
