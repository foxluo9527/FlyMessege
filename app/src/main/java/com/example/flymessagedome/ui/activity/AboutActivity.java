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
import com.example.flymessagedome.utils.ToastUtils;
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
            case R.id.protocol:
                Intent intent2 = new Intent(mContext, WebActivity.class);
                intent2.putExtra("URLString", Constant.protocolUrl);
                startActivity(intent2);
                break;
            case R.id.back_msg:
                if (checkApkExist(mContext, "com.tencent.mobileqq")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=1061297065&version=1")));
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:17628648573"));
                    startActivity(intent);
                    ToastUtils.showToast("本机未安装QQ应用");
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
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initDatas() {
        versionString = getAppVersionName();
        version.setText("飞讯 V" + versionString);
        /***** 获取升级信息 *****/
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();

        if (upgradeInfo == null) {
            update_msg.setText("已是最新版本");
            return;
        } else if (upgradeInfo.versionName.equals(versionString))
            update_msg.setText("已是最新版本");
        else
            update_msg.setText("新版本:V" + upgradeInfo.versionName);
    }

    @Override
    public void configViews() {

    }

}
