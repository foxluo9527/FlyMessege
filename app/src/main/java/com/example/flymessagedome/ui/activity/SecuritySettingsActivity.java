package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SecuritySettingsActivity extends BaseActivity {

    @BindView(R.id.u_name)
    TextView u_name;
    @BindView(R.id.phone)
    TextView phone;

    @Override
    public int getLayoutId() {
        return R.layout.activity_security_settings;
    }

    @OnClick({R.id.back, R.id.change_name, R.id.change_pass, R.id.change_phone})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.change_phone:
                startActivity(new Intent(mContext, ChangePhoneActivity.class));
                break;
            case R.id.change_pass:
                startActivity(new Intent(mContext, ChangePassActivity.class));
                break;
            case R.id.change_name:
                startActivity(new Intent(mContext, UserNameActivity.class));
                break;
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    public void initDatas() {
        u_name.setText(LoginActivity.loginUser.getU_name());
        String hintPhone;
        char[] characters = new char[LoginActivity.loginUser.getU_phone().length()];
        for (int i = 0; i < LoginActivity.loginUser.getU_phone().length(); i++) {
            char c = LoginActivity.loginUser.getU_phone().charAt(i);
            if (i > 2 && i < 8) {
                c = '*';
            }
            characters[i] = c;
        }
        hintPhone = String.valueOf(characters);
        phone.setText(hintPhone);
    }

    @Override
    public void configViews() {

    }
}
