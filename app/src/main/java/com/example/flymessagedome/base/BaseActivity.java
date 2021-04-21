package com.example.flymessagedome.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.receiver.NetStateChangeObserver;
import com.example.flymessagedome.receiver.NetStateChangeReceiver;
import com.example.flymessagedome.service.MessageService;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.AppUtils;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkType;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.flymessagedome.service.MessageService.LOG_OUT;
import static com.example.flymessagedome.service.MessageService.RECEIVE_FRIEND_REQUEST;
import static com.example.flymessagedome.service.MessageService.RECEIVE_USER_MESSAGE;
import static com.example.flymessagedome.service.MessageService.SERVICE_CONNECTED;
import static com.example.flymessagedome.service.MessageService.SERVICE_DISCONNECT;

public abstract class BaseActivity extends AppCompatActivity implements NetStateChangeObserver {

    private static final String TAG = "FlyMessage";
    private Unbinder unbinder;
    protected Context mContext;
    protected Activity activity;
    private MaterialDialog mLoadingDialog;
    private ServiceMessageReceiver serviceMessageReceiver;
    private HomeEventReceiver homeEventReceiver;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetStateChangeReceiver.registerReceiver(this);
        String className = this.getLocalClassName();
        Log.e("className", className);
        if (SharedPreferencesUtil.getInstance().getString("loginToken") == null) {
            if (!className.equals("ui.activity.LoginActivity") &&
                    !className.equals("ui.activity.WebActivity") &&
                    !className.equals("ui.activity.WelcomeActivity")) {
                finish();
                ActivityCollector.finishAll();
                LoginActivity.startActivity(this);
                return;
            }
        }
        setContentView(getLayoutId());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        activity = this;
        mContext = this;
        unbinder = ButterKnife.bind(this);
        ActivityCollector.addActivity(this);
        setupActivityComponent(FlyMessageApplication.getInstances().getAppComponent());
        configViews();
        initDatas();
        if (serviceMessageReceiver == null) {
            serviceMessageReceiver = new ServiceMessageReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MessageService.SOCKET_SERVICE_ACTION);
            registerReceiver(serviceMessageReceiver, itFilter);
        }
        if (homeEventReceiver == null) {
            homeEventReceiver = new HomeEventReceiver();
            IntentFilter itFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(homeEventReceiver, itFilter);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ActivityCollector.removeActivity(this);
        dismissLoadingDialog();
        if (serviceMessageReceiver != null) {
            unregisterReceiver(serviceMessageReceiver);
        }
        if (homeEventReceiver != null) {
            unregisterReceiver(homeEventReceiver);
        }
        try {
            if (unbinder != null)
                unbinder.unbind();
            NetStateChangeReceiver.unRegisterReceiver(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetStateChangeReceiver.registerObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NetStateChangeReceiver.unRegisterObserver(this);
    }

    @Override
    public void onNetDisconnected() {
        // 监听到网络断开连接
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        // 监听到网络连接
    }

    //按下home
    public void onPressHome() {
        Log.e(TAG, "按下home键");
    }

    public abstract int getLayoutId();

    protected abstract void setupActivityComponent(AppComponent appComponent);

    public abstract void initDatas();

    protected void serviceDisconnect() {
        Log.e(TAG, "连接已断开");
    }

    protected void serviceConnected() {
        Log.e(TAG, "连接服务成功");
    }

    protected void receiveUserMessage() {
        Log.e(TAG, "收到消息");
    }

    protected void receiveFriendRequest() {
        Log.e(TAG, "收到好友申请");
    }

    protected void loginRemote() {
        Log.e(TAG, "异地登陆，请重新登录");
        ToastUtils.showToast("异地登陆，请重新登录");
        MainActivity.serviceBinder.closeConnect();
        ActivityCollector.finishAll();
        SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, false);
        LoginActivity.startActivity(this);
    }

    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public abstract void configViews();


    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public void showLoadingDialog(Boolean cancelable, String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new MaterialDialog.Builder(this)
                    .widgetColorRes(R.color.black)
                    .progress(true, 0)
                    .cancelable(true)
                    .build();
        }
        mLoadingDialog.setContent(msg);
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void toastServerEx(boolean isInternet, String msg) {
        if (isInternet) {
            if (TextUtils.isEmpty(msg))
                ToastUtils.showToast("请求数据失败,请稍后再试");//服务器异常,请稍后再试   请求数据失败,请稍后再试
            else
                ToastUtils.showToast(msg);
        } else {
            ToastUtils.showToast("网络异常,请检查网络是否可用");//网络异常,请检查网络是否可用
        }
    }

    //按下home监听
    private class HomeEventReceiver extends BroadcastReceiver {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM__HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //按下home进入后台
                    onPressHome();
                } else if (TextUtils.equals(reason, SYSTEM__HOME_KEY_LONG)) {
                    //长按home进入后台应用
                    onPressHome();
                }
            }
        }
    }

    ;

    //收到消息
    private class ServiceMessageReceiver extends BroadcastReceiver {
        private final Handler handler;

        // Handler used to execute code on the UI thread
        public ServiceMessageReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    int m_type = intent.getIntExtra(MessageService.MSG_TYPE, -1);
                    switch (m_type) {
                        case SERVICE_CONNECTED:
                            serviceConnected();
                            break;
                        case SERVICE_DISCONNECT:
                            serviceDisconnect();
                            break;
                        case RECEIVE_USER_MESSAGE:
                            receiveUserMessage();
                            break;
                        case RECEIVE_FRIEND_REQUEST:
                            receiveFriendRequest();
                            break;
                        case LOG_OUT:
                            loginRemote();
                            break;
                    }
                }
            });
        }
    }
}
