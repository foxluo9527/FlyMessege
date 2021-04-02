package com.example.flymessagedome.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.receiver.NetStateChangeObserver;
import com.example.flymessagedome.receiver.NetStateChangeReceiver;
import com.example.flymessagedome.service.MessageService;
import com.example.flymessagedome.utils.NetworkType;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.flymessagedome.service.MessageService.LOG_OUT;
import static com.example.flymessagedome.service.MessageService.RECEIVE_FRIEND_REQUEST;
import static com.example.flymessagedome.service.MessageService.RECEIVE_USER_MESSAGE;
import static com.example.flymessagedome.service.MessageService.SERVICE_CONNECTED;
import static com.example.flymessagedome.service.MessageService.SERVICE_DISCONNECT;

public abstract class BaseFragment extends Fragment implements NetStateChangeObserver {

    public static final String TAG = "FlyMessage";
    private Unbinder unbinder;
    protected View parentView;
    protected FragmentActivity activity;
    protected LayoutInflater inflater;
    protected Context mContext;
    private ServiceMessageReceiver serviceMessageReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        mContext = activity;
        parentView = inflater.inflate(getLayoutResId(),container,false);
        this.inflater = inflater;
        unbinder = ButterKnife.bind(this,parentView);
        setupActivityComponent(FlyMessageApplication.getInstances().getAppComponent());
        configViews();
        initDatas();
        if (serviceMessageReceiver==null){
            serviceMessageReceiver = new ServiceMessageReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MessageService.SOCKET_SERVICE_ACTION);
            mContext.registerReceiver(serviceMessageReceiver, itFilter);
        }
        NetStateChangeReceiver.registerReceiver(mContext);
        NetStateChangeReceiver.registerObserver(this);
        return parentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
        this.mContext = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unbinder.unbind();
            if (serviceMessageReceiver!=null){
                mContext.unregisterReceiver(serviceMessageReceiver);
            }
            NetStateChangeReceiver.unRegisterReceiver(mContext);
            NetStateChangeReceiver.unRegisterObserver(this);
        }catch (Exception ignored){

        }
    }

    public FragmentActivity getSupportActivity(){
        return super.getActivity();
    }

    public abstract @LayoutRes int getLayoutResId();
    protected abstract void setupActivityComponent(AppComponent appComponent);

    public abstract void initDatas();

    @Override
    public void onNetDisconnected() {
        // 监听到网络断开连接
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        // 监听到网络连接
    }

    protected void serviceDisconnect(){
        Log.e(TAG,"连接已断开");
    }

    protected void serviceConnected(){
        Log.e(TAG,"连接服务成功");
    }

    protected  void receiveUserMessage(){
        Log.e(TAG,"收到消息");
    }

    protected void receiveFriendRequest(){
        Log.e(TAG,"收到好友申请");
    }
    protected void loginRemote(){
        Log.e(TAG,"异地登陆");
    }
    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public abstract void configViews();

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
                    int m_type=intent.getIntExtra(MessageService.MSG_TYPE,-1);
                    switch (m_type){
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
