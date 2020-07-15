package com.example.flymessagedome.ui.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.AppVersionBean;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.service.MessageService;
import com.example.flymessagedome.service.UpdateService;
import com.example.flymessagedome.ui.adapter.TabPageAdapter;
import com.example.flymessagedome.ui.contract.MainContract;
import com.example.flymessagedome.ui.fragment.FriendFragment;
import com.example.flymessagedome.ui.fragment.MessageFragment;
import com.example.flymessagedome.ui.fragment.MineFragment;
import com.example.flymessagedome.ui.presenter.MainPresenter;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.CommUtil;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkType;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class MainActivity extends BaseActivity implements MainContract.View {
    public static final String TAG = "FlyMessage";
    private MyConnection conn;
    private MyUpdateConnection updateConn;
    public static UpdateService.MyUpdateBinder updateBinder;
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
        try {
            if (conn!=null){
                serviceBinder.closeConnect();
                unbindService(conn);
                stopService(new Intent(this,MessageService.class));
            }
            if (updateConn!=null){
                unbindService(updateConn);
                stopService(new Intent(this,UpdateService.class));
            }
        }catch (Exception e){

        }
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
    public void initDatas() {
        bindService();
        initFragment();
        try {
            checkUpdate(this, CommUtil.getAppVersion(this.getPackageName(),this),false);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int count = tabLayout.getTabCount();
        LinearLayout childView=(LinearLayout)tabLayout.getChildAt(0);
        List<ImageView> tabs = new ArrayList();
        for(int index=0;index<count;index++){
            LinearLayout child=(LinearLayout) childView.getChildAt(index);
            if (child!=null)
                for (int i=0;i<child.getChildCount();i++){
                    View view=child.getChildAt(i);
                    if (view instanceof  ImageView){
                        tabs.add((ImageView) view);
                    }
                }
        }
        for (ImageView view:tabs) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.bottomMargin=0;//将默认的距离底部20dp，改为0，这样底部区域全被listview填满。
            view.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void configViews() {
        mainPresenter.attachView(this);
    }

    private void initFragment(){
        fragmentList=new ArrayList<Fragment>();
        fragmentList.add(new MessageFragment());
        fragmentList.add(new FriendFragment());
        fragmentList.add(new MineFragment());
        String[] mTabNames=new String[]{"消息","好友","我的"};
        TabPageAdapter pageAdapter = new TabPageAdapter(getSupportFragmentManager(), fragmentList,mTabNames);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.home_tab_message_selecter);
        tabLayout.getTabAt(1).setIcon(R.drawable.home_tab_friend_selecter);
        tabLayout.getTabAt(2).setIcon(R.drawable.home_tab_mine_selecter);
    }
    private void bindService(){
        if (serviceBinder==null){
            Intent intent = new Intent(this, MessageService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            conn = new MyConnection();
            startService(intent);
            bindService(intent, conn, BIND_AUTO_CREATE);
        }
        if (updateBinder==null){
            Intent updateIntent=new Intent(this.getApplicationContext(),UpdateService.class);
            updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            updateConn=new MyUpdateConnection();
            startService(updateIntent);
            bindService(updateIntent,updateConn,BIND_AUTO_CREATE);
        }
    }

    @Override
    public void showError() {
        toastServerEx(NetworkUtils.isConnected(this),null);
    }

    @Override
    public void showError(String msg) {
        toastServerEx(NetworkUtils.isConnected(this),msg);
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
        if (!serviceBinder.getConnectState()){
            serviceBinder.closeConnect();
            mainPresenter.reConnectService(serviceBinder);
        }
    }

    @Override
    public void reConnectFailed(Login login) {
        //重连失败
        if (NetworkUtils.isConnected(this)&&login!=null&&login.code== Constant.FAILED){
            Log.e(TAG,"获取用户登录信息失败，请重新登录");
            ToastUtils.showToast("获取用户登录信息失败，请重新登录");
            SharedPreferencesUtil.getInstance().removeAll();
            ActivityCollector.finishAll();
            LoginActivity.startActivity(this);
        }
    }

    @Override
    public void initGroupMsg(GroupBean group) {
        Bundle bundle=new Bundle();
        bundle.putParcelable("group",group);
        Intent intent=new Intent(mContext,GroupMsgActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class MyUpdateConnection implements ServiceConnection {
        //This method will be entered after the service is started.
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::updateConnection::onServiceConnected");
            //Get MyBinder in service
            updateBinder = (UpdateService.MyUpdateBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "::updateConnection::onServiceDisconnected");
        }
    }
    private class MyConnection implements ServiceConnection {
        //This method will be entered after the service is started.
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::MessageService::onServiceConnected");
            //Get MyBinder in service
            serviceBinder = (MessageService.MessageServiceBinder) service;
            new Thread(){
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
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                //扫码失败
            } else {
                String content = intentResult.getContents();//返回值
                Log.e("TAG",content);
                if (content==null){
                    ToastUtils.showToast("扫描失败");
                }else if (content.contains("http")){
                    Intent intent= new Intent(mContext, WebActivity.class);
                    intent.putExtra("URLString",content);
                    startActivity(intent);
                }else if (content.contains("[")&&content.contains("]")){
                    String fileContent=content.substring(content.indexOf("[")+1,content.indexOf("]"));
                    if (fileContent.contains(":")) {
                        String[] fileParams=fileContent.split(":");
                        fileContent=fileParams[0];
                        if (fileContent.equals("FlyMessage-addFriend")){
                            String u_name=fileParams[1];
                            Intent intent=new Intent(mContext,ShowUserActivity.class);
                            intent.putExtra("userName",u_name);
                            startActivity(intent);
                        }else if (fileContent.equals("FlyMessage-addGroup")){
                            String g_id=fileParams[1];
                            try {
                                int gId=Integer.parseInt(g_id);
                                mainPresenter.getGroupMsg(gId);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
    public static void checkUpdate(Context context,String version,boolean showToast) {
        new AsyncTask<Void, Void, AppVersionBean>() {
            @Override
            protected AppVersionBean doInBackground(Void... voids) {
                return HttpRequest.checkUpdate(version);
            }
            //SettingActivity.getAppVersion(context.getPackageName(),context)
            @Override
            protected void onPostExecute(AppVersionBean versionBean) {
                if (versionBean != null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("检测到新版本")
                            .setIcon(R.drawable.icon)
                            .setMessage("\n最新版本:v" + versionBean.getVersion()+
                                    "\n新版本信息:" +versionBean.getInfo()+
                                    "\n是否更新?")
                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();//取消弹出框
                                    if (!updateBinder.checkOnDownloading()) {
                                        updateBinder.startUpdate(versionBean.getUrl());
                                        Toast.makeText(context,"更新下载开始",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(context,"更新正在下载中!",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();//取消弹出框
                                    if (versionBean.getForce() == 1) {
                                        System.exit(0);
                                    }
                                }
                            })
                            .create().show();
                }else if (showToast) {
                    Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
