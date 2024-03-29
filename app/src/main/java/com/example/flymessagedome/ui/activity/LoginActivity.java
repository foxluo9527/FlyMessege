package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.User;
import com.example.flymessagedome.bean.UserDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.ui.contract.LoginContract;
import com.example.flymessagedome.ui.presenter.LoginPresenter;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.CommUtil;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.tencent.bugly.beta.Beta;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint({"NonConstantResourceId", "HandlerLeak", "SetTextI18n"})
public class LoginActivity extends BaseActivity implements LoginContract.View {
    public static User loginUser = null;
    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_send_code)
    Button btnSendCode;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.pass_login)
    View passLoginView;
    @BindView(R.id.code_login)
    View codeLoginView;
    @BindView(R.id.tv_login_type)
    TextView tvLoginType;

    @Inject
    LoginPresenter loginPresenter;

    Handler setCountDown = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int s = msg.what;
            if (btnSendCode != null) {
                if (s == 0) {
                    btnSendCode.setEnabled(true);
                    btnSendCode.setText(R.string.get);
                } else {
                    btnSendCode.setText(s + "s");
                }
            }
        }
    };

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void initDatas() {
        SharedPreferencesUtil.getInstance().putBoolean("isFirstUse", false);
        boolean isLogin = SharedPreferencesUtil.getInstance().getBoolean(Constant.IS_LOGIN, false);
        boolean autoLogin = SharedPreferencesUtil.getInstance().getBoolean(Constant.AUTO_LOGIN, true);
        boolean rememberAccount = SharedPreferencesUtil.getInstance().getBoolean(Constant.REMEMBER_ACCOUNT, true);
        if (isLogin && rememberAccount) {
            String u_name = SharedPreferencesUtil.getInstance().getString(Constant.U_NAME);
            String u_pass = SharedPreferencesUtil.getInstance().getString(Constant.U_PASS);
            if (u_name != null && u_pass != null) {
                etUserName.setText(u_name);
                etPassword.setText(u_pass);
                if (autoLogin) {
                    showLoadingDialog(false, Constant.ON_LOGIN);
                    loginPresenter.login(u_name, u_pass);
                }
            }
        }
        Beta.checkAppUpgrade(false, false);
    }

    @Override
    public void configViews() {
        loginPresenter.attachView(this);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.tv_forget_password, R.id.tv_login, R.id.tv_login_type, R.id.btn_send_code, R.id.tv_privacy, R.id.tv_protocol, R.id.tv_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_password:
                Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                intent.putExtra("URLString", Constant.FORGET_PASS_URL);
                startActivity(intent);
                break;
            case R.id.tv_register:
                Intent intent1 = new Intent(LoginActivity.this, WebActivity.class);
                intent1.putExtra("URLString", Constant.REGISTER_URL);
                startActivity(intent1);
                break;
            case R.id.tv_login_type:
                int passViewVisibility = passLoginView.getVisibility();
                int codeViewVisibility = codeLoginView.getVisibility();
                passLoginView.setVisibility(codeViewVisibility);
                codeLoginView.setVisibility(passViewVisibility);
                SpannableString ss;
                if (passViewVisibility == View.VISIBLE) {
                    tvLoginType.setText(R.string.pass_login);
                    ss = new SpannableString(getString(R.string.input_phone));
                } else {
                    tvLoginType.setText(R.string.code_login);
                    ss = new SpannableString(getString(R.string.input_user_name));
                }
                AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
                ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                etUserName.setHint(new SpannedString(ss));
                break;
            case R.id.tv_login:
                String userName = etUserName.getText().toString();
                if (isVisible(passLoginView)) {
                    String password = etPassword.getText().toString();
                    if (TextUtils.isEmpty(userName)) {
                        ToastUtils.showToast(getString(R.string.please_input_uname));
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        ToastUtils.showToast(getString(R.string.please_input_pass));
                        return;
                    }
                    showLoadingDialog(false, Constant.ON_LOGIN);
                    loginPresenter.login(userName, password);
                } else {
                    String code = etCode.getText().toString();
                    if (TextUtils.isEmpty(userName)) {
                        ToastUtils.showToast(getString(R.string.please_input_phone));
                        return;
                    } else if (!CommUtil.isMobileNO(userName)) {
                        ToastUtils.showToast(getString(R.string.please_input_right_phone));
                        return;
                    }
                    if (TextUtils.isEmpty(code)) {
                        ToastUtils.showToast(getString(R.string.please_input_code));
                        return;
                    } else if (code.length() != 6) {
                        ToastUtils.showToast(getString(R.string.please_input_right_code));
                        return;
                    }
                    showLoadingDialog(false, Constant.ON_LOGIN);
                    loginPresenter.codeLogin(userName, code);
                }
                break;
            case R.id.btn_send_code:
                String phone = etUserName.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(getString(R.string.please_input_phone));
                    return;
                } else if (!CommUtil.isMobileNO(phone)) {
                    ToastUtils.showToast(getString(R.string.please_input_right_phone));
                    return;
                }
                btnSendCode.setEnabled(false);
                showLoadingDialog(false, Constant.ON_SENDING_CODE);
                loginPresenter.sendCode(phone);
                break;
            case R.id.tv_privacy:
                Intent privacyIntent = new Intent(mContext, WebActivity.class);
                privacyIntent.putExtra("URLString", Uri.parse(getString(R.string.privacy_file_path)).toString());
                startActivity(privacyIntent);
                break;
            case R.id.tv_protocol:
                Intent protocolIntent = new Intent(mContext, WebActivity.class);
                protocolIntent.putExtra("URLString", Constant.protocolUrl);
                startActivity(protocolIntent);
                break;

        }
    }

    @Override
    public void showError() {
        try {
            dismissLoadingDialog();
            toastServerEx(NetworkUtils.isConnected(this), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError(String msg) {
        try {
            dismissLoadingDialog();
            toastServerEx(NetworkUtils.isConnected(this), msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {
        try {
            dismissLoadingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tokenExceed() {
        try {
            dismissLoadingDialog();
            ToastUtils.showToast(getString(R.string.login_time_out));
            SharedPreferencesUtil.getInstance().removeAll();
            ActivityCollector.finishAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loginSuccess(Login login) {
        try {
            dismissLoadingDialog();
            SharedPreferencesUtil.getInstance().putBoolean(Constant.IS_LOGIN, true);
            if (login.loginUser != null) {
                loginUser = login.loginUser;
                UserDao userDao = FlyMessageApplication.getInstances().getDaoSession().getUserDao();
                userDao.insertOrReplace(loginUser);
            }
            MainActivity.startActivity(mContext);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLoginCodeSuccess() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    for (int s = 60; s >= 0; s--) {
                        Message msg = new Message();
                        msg.what = s;
                        setCountDown.sendMessage(msg);
                        if (s == 60)
                            dismissLoadingDialog();
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void sendLoginCodeFailed() {
        try {
            dismissLoadingDialog();
            btnSendCode.setEnabled(true);
            btnSendCode.setText(R.string.get);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
