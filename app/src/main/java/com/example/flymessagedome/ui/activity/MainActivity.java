package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.service.MessageService;
import com.example.flymessagedome.ui.adapter.TabPageAdapter;
import com.example.flymessagedome.ui.contract.MainContract;
import com.example.flymessagedome.ui.fragment.CommunityFragment;
import com.example.flymessagedome.ui.fragment.FriendFragment;
import com.example.flymessagedome.ui.fragment.MessageFragment;
import com.example.flymessagedome.ui.fragment.MineFragment;
import com.example.flymessagedome.ui.presenter.MainPresenter;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkType;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tencent.bugly.beta.Beta;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements MainContract.View {
    public static final String TAG = "FlyMessage";
    public static MessageService.MessageServiceBinder serviceBinder;
    List<Fragment> fragmentList;

    @BindView(R.id.home_tab_view)
    TabLayout tabLayout;
    @BindView(R.id.main_view)
    ViewPager viewPager;

    @Inject
    MainPresenter mainPresenter;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    protected void serviceDisconnect() {
        super.serviceDisconnect();
        //连接断开重新登录，并尝试重连，若登录失败(密码错误)则可能密码已修改，跳转登录界面
        mainPresenter.reConnectService(serviceBinder);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!SharedPreferencesUtil.getInstance().getBoolean(Constant.IS_LOGIN, false)) {
            LoginActivity.startActivity(this);
            finish();
            overridePendingTransition(0, 0);
        }
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.bar_bg));
        }
        Beta.checkAppUpgrade(false, false);
    }

    @Override
    public void initDatas() {
        bindService();
        initFragment();
        int count = tabLayout.getTabCount();
        LinearLayout childView = (LinearLayout) tabLayout.getChildAt(0);
        ArrayList<ImageView> tabs = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            LinearLayout child = (LinearLayout) childView.getChildAt(index);
            if (child != null)
                for (int i = 0; i < child.getChildCount(); i++) {
                    View view = child.getChildAt(i);
                    if (view instanceof ImageView) {
                        tabs.add((ImageView) view);
                    }
                }
        }
        for (ImageView view : tabs) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.bottomMargin = 0;//将默认的距离底部20dp，改为0，这样底部区域全被listview填满。
            view.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void configViews() {
        mainPresenter.attachView(this);
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new MessageFragment());
        fragmentList.add(new FriendFragment());
        fragmentList.add(new CommunityFragment());
        fragmentList.add(new MineFragment());
        String[] mTabNames = new String[]{"消息", "好友", "社区", "我的"};
        TabPageAdapter pageAdapter = new TabPageAdapter(getSupportFragmentManager(), fragmentList, mTabNames);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.home_tab_message_selecter);
        tabLayout.getTabAt(1).setIcon(R.drawable.home_tab_friend_selecter);
        tabLayout.getTabAt(2).setIcon(R.drawable.home_tab_community_selecter);
        tabLayout.getTabAt(3).setIcon(R.drawable.home_tab_mine_selecter);
    }

    private void bindService() {
        if (serviceBinder == null) {
            Intent intent = new Intent(this, MessageService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyConnection conn = new MyConnection();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                getApplicationContext().startForegroundService(intent);
            else
                getApplicationContext().startService(intent);
            getApplicationContext().bindService(intent, conn, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void showError() {
        toastServerEx(NetworkUtils.isConnected(this), null);
    }

    @Override
    public void showError(String msg) {
        toastServerEx(NetworkUtils.isConnected(this), msg);
    }

    @Override
    public void complete() {
    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        super.onNetConnected(networkType);
        if (!serviceBinder.getConnectState()) {
            serviceBinder.closeConnect();
            mainPresenter.reConnectService(serviceBinder);
        }
    }

    @Override
    public void reConnectFailed(Login login) {
        //重连失败
        if (NetworkUtils.isConnected(this) && login != null && login.code == Constant.FAILED) {
            Log.e(TAG, "获取用户登录信息失败，请重新登录");
            ToastUtils.showToast("获取用户登录信息失败，请重新登录");
            SharedPreferencesUtil.getInstance().removeAll();
            ActivityCollector.finishAll();
            SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, true);
            LoginActivity.startActivity(this);
        }
    }

    @Override
    public void initGroupMsg(GroupBean group) {
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable("group", group);
            Intent intent = new Intent(mContext, GroupMsgActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MyConnection implements ServiceConnection {
        //This method will be entered after the service is started.
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::MessageService::onServiceConnected");
            //Get MyBinder in service
            serviceBinder = (MessageService.MessageServiceBinder) service;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    serviceBinder.connect();
                }
            }.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "::MessageService::onServiceDisconnected");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() != null) {
                    String content = intentResult.getContents();//返回值
                    Log.e("TAG", content);
                    if (content == null) {
                        ToastUtils.showToast("扫描失败");
                    } else if (content.contains("http")) {
                        Intent intent = new Intent(mContext, WebActivity.class);
                        intent.putExtra("URLString", content);
                        startActivity(intent);
                    } else if (content.contains("[") && content.contains("]")) {
                        String fileContent = content.substring(content.indexOf("[") + 1, content.indexOf("]"));
                        if (fileContent.contains(":")) {
                            String[] fileParams = fileContent.split(":");
                            fileContent = fileParams[0];
                            if (fileContent.equals("FlyMessage-addFriend")) {
                                String u_name = fileParams[1];
                                Intent intent = new Intent(mContext, ShowUserActivity.class);
                                intent.putExtra("userName", u_name);
                                startActivity(intent);
                            } else if (fileContent.equals("FlyMessage-addGroup")) {
                                String g_id = fileParams[1];
                                try {
                                    int gId = Integer.parseInt(g_id);
                                    mainPresenter.getGroupMsg(gId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
