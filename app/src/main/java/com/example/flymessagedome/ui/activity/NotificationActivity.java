package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Switch;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.utils.NotificationUtil;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class NotificationActivity extends BaseActivity {

    @BindView(R.id.allow_notify_switch)
    Switch allow_notify_switch;
    @BindView(R.id.notify_voice_switch)
    Switch notify_voice_switch;
    @BindView(R.id.notify_vibrator_switch)
    Switch notify_vibrator_switch;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notification;
    }

    @OnClick({R.id.back, R.id.allow_notify, R.id.notify_voice, R.id.notify_vibrator, R.id.setting_notify})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.allow_notify:
                allow_notify_switch.setChecked(!allow_notify_switch.isChecked());
                break;
            case R.id.notify_voice:
                notify_voice_switch.setChecked(!notify_voice_switch.isChecked());
                break;
            case R.id.notify_vibrator:
                notify_vibrator_switch.setChecked(!notify_vibrator_switch.isChecked());
                break;
            case R.id.setting_notify:
                NotificationUtil.gotoSet(mContext);
                break;
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initDatas() {
        allow_notify_switch.setChecked(SharedPreferencesUtil.getInstance().getBoolean("allowNotify", true));
        notify_vibrator_switch.setChecked(SharedPreferencesUtil.getInstance().getBoolean("msgVibrator", true));
        notify_voice_switch.setChecked(SharedPreferencesUtil.getInstance().getBoolean("msgVoice", true));
        allow_notify_switch.setOnCheckedChangeListener((buttonView, isChecked) -> SharedPreferencesUtil.getInstance().putBoolean("allowNotify", isChecked));
        notify_vibrator_switch.setOnCheckedChangeListener((buttonView, isChecked) -> SharedPreferencesUtil.getInstance().putBoolean("msgVibrator", isChecked));
        notify_voice_switch.setOnCheckedChangeListener((buttonView, isChecked) -> SharedPreferencesUtil.getInstance().putBoolean("msgVoice", isChecked));
    }

    @Override
    public void configViews() {

    }
}
