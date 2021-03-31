package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.utils.Constant;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.flymessagedome.utils.AppUtils.getAppVersionName;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.had_update_img)
    ImageView had_update_img;
    @BindView(R.id.update_msg)
    TextView update_msg;
    String versionString = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.back, R.id.protocol, R.id.privacy, R.id.check_update, R.id.guide, R.id.back_msg})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.check_update:
                Beta.checkAppUpgrade(true, false);
                break;
            case R.id.privacy:
                Intent privacyIntent = new Intent(mContext, WebActivity.class);
                privacyIntent.putExtra("URLString", Uri.parse(getString(R.string.privacy_file_path)).toString());
                startActivity(privacyIntent);
                break;
            case R.id.protocol:
                Intent protocolIntent = new Intent(mContext, WebActivity.class);
                protocolIntent.putExtra("URLString", Constant.protocolUrl);
                startActivity(protocolIntent);
                break;
            case R.id.back_msg:
                Intent feedback = new Intent(Intent.ACTION_SENDTO);
                feedback.setData(Uri.parse(getString(R.string.email)));
                feedback.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback));
                feedback.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_uname) + LoginActivity.loginUser.getU_name() + getString(R.string.feedback_content));
                if (feedback.resolveActivity(getPackageManager()) != null) {
                    startActivity(feedback);
                } else if (checkApkExist(mContext, getString(R.string.qq_package))) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.qq_contract))));
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(getString(R.string.tel_contract)));
                    startActivity(intent);
                }
                break;
        }
    }

    private boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initDatas() {
        versionString = getAppVersionName();
        version.setText(getString(R.string.app_version) + versionString);
        /***** 获取升级信息 *****/
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();

        if (upgradeInfo == null) {
            update_msg.setText(R.string.already_lastest);
            return;
        } else if (upgradeInfo.versionName.equals(versionString))
            update_msg.setText(R.string.already_lastest);
        else
            update_msg.setText(getString(R.string.new_version) + upgradeInfo.versionName);
    }

    @Override
    public void configViews() {

    }

}
