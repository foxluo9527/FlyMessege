package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.view.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.head_img)
    CircleImageView head_img;
    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }
    @OnClick({R.id.back,R.id.account_setting,R.id.message_setting,R.id.settings,R.id.privacy_setting,R.id.self_setting,R.id.about})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.account_setting:
                startActivity(new Intent(mContext,AccountActivity.class));
                break;
            case R.id.self_setting:
                startActivity(new Intent(mContext,SecuritySettingsActivity.class));
                break;
            case R.id.message_setting:
                startActivity(new Intent(mContext,NotificationActivity.class));
                break;
            case R.id.privacy_setting:
                startActivity(new Intent(mContext,PrivacyActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(mContext,CommonSettingActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(mContext,AboutActivity.class));
                break;
        }
    }
    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initDatas() {
        if (LoginActivity.loginUser!=null)
        Glide.with(mContext).load(LoginActivity.loginUser.getU_head_img()).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).into(head_img);
    }

    @Override
    public void configViews() {

    }
}
