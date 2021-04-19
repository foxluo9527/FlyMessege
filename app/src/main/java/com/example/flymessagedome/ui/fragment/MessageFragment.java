package com.example.flymessagedome.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseFragment;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.ui.activity.AddFriendActivity;
import com.example.flymessagedome.ui.activity.GroupChatActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.activity.QrCodeActivity;
import com.example.flymessagedome.ui.activity.SearchActivity;
import com.example.flymessagedome.ui.activity.UserChatActivity;
import com.example.flymessagedome.ui.adapter.ChatAdapter;
import com.example.flymessagedome.ui.contract.ChatContract;
import com.example.flymessagedome.ui.presenter.ChatFragmentPresenter;
import com.example.flymessagedome.ui.widget.MyListView;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkType;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.flymessagedome.service.MessageService.MSG_TYPE;
import static com.example.flymessagedome.service.MessageService.SERVICE_DISCONNECT;
import static com.example.flymessagedome.service.MessageService.SOCKET_SERVICE_ACTION;

@SuppressLint("NonConstantResourceId")
public class MessageFragment extends BaseFragment implements ChatContract.View, MenuItem.OnMenuItemClickListener, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "FlyMessage";
    public static ArrayList<Chat> chats;
    int nowSelectItem = 0;

    @BindView(R.id.disconnect_view)
    View disconnectView;
    @BindView(R.id.home_message_list)
    MyListView chatList;
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.add_menu)
    View menu;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    @Inject
    ChatFragmentPresenter chatFragmentPresenter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_message;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @OnClick({R.id.add_menu, R.id.search_view, R.id.disconnect_view})
    public void onClick(View view) {
        if (view.getId() == R.id.search_view) {
            startActivity(new Intent(mContext, SearchActivity.class));
        } else if (view.getId() == R.id.disconnect_view) {
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        } else {
            //创建弹出式菜单对象（最低版本11）
            PopupMenu popup = new PopupMenu(mContext, view);//第二个参数是绑定的那个view
            //获取菜单填充器
            MenuInflater inflater = popup.getMenuInflater();
            //填充菜单
            inflater.inflate(R.menu.home_add_menu, popup.getMenu());
            //绑定菜单项的点击事件
            popup.setOnMenuItemClickListener(this::onMenuItemClick);
            //显示
            popup.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        chatFragmentPresenter.getChatList(mContext);
    }

    public void refreshChatList() {
        new Handler().postDelayed(() -> {
            if (chatList != null) {
                chatFragmentPresenter.getChatList(mContext);
                refreshChatList();
            }
        }, 60000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initDatas() {
        refreshLayout.setRefreshing(true);
        chatFragmentPresenter.getChatList(mContext);
        refreshChatList();
        refreshLayout.setOnRefreshListener(() -> {
            if (!NetworkUtils.isConnected(mContext) || !MainActivity.serviceBinder.getConnectState()) {
                Intent actionIntent = new Intent(SOCKET_SERVICE_ACTION);
                actionIntent.putExtra(MSG_TYPE, SERVICE_DISCONNECT);
                mContext.sendBroadcast(actionIntent);
            }
            refreshLayout.setRefreshing(true);
            chatFragmentPresenter.getMessage(20, 1);
            new Handler().postDelayed(() -> {
                if (refreshLayout != null)
                    refreshLayout.setRefreshing(false);
            }, 10000);
        });
        chatList.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view, position, id) -> {
            MainActivity.serviceBinder.cancelNotify(Math.toIntExact(chats.get(position).getC_id()));
            Intent intent;
            if (chats.get(position).getChat_type() == 0) {
                intent = new Intent(mContext, UserChatActivity.class);
                intent.putExtra("userId", (long) chats.get(position).getSource_id());
            } else {
                intent = new Intent(mContext, GroupChatActivity.class);
                intent.putExtra("groupId", (long) chats.get(position).getSource_g_id());
            }
            startActivity(intent);
        });
        chatList.setOnItemLongClickListener((parent, view, position, id) -> {
            nowSelectItem = position;
            return false;
        });
        chatList.setOnCreateContextMenuListener((arg0, arg1, arg2) -> {
            // TODO Auto-generated method stub
            if (!chats.get(nowSelectItem).getChat_up())
                arg0.add(0, 0, 0, "顶置聊天");
            else
                arg0.add(0, 0, 0, "取消顶置");
            arg0.add(0, 1, 0, "删除聊天");
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            try{
                refreshLayout.setEnabled(scrollView.getScrollY() == 0);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    //设置菜单内容和事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //获取点击的item的id
        switch (item.getItemId()) {
            case 0:
                if (!chats.get(info.position).getChat_up())
                    chatFragmentPresenter.upChatList(chats.get(info.position));
                else
                    chatFragmentPresenter.cancelUpChat(chats.get(info.position));
                return true;
            case 1:
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("是否删除此聊天的消息记录")
                        .setPositiveButton("删除", (dialog1, which) -> {
                            dialog1.cancel();
                            chatFragmentPresenter.delChatList(chats.get(info.position), true);
                        })
                        .setNegativeButton("不删除", (dialog1, which) -> {
                            dialog1.cancel();
                            chatFragmentPresenter.delChatList(chats.get(info.position), false);
                        })
                        .setNeutralButton("取消", (dialog1, which) -> dialog1.cancel())
                        .create().show();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void configViews() {
        chatFragmentPresenter.attachView(this);
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        super.onNetConnected(networkType);
    }

    @Override
    public void onNetDisconnected() {
        super.onNetDisconnected();
        MainActivity.serviceBinder.closeConnect();
        if (disconnectView != null)
            disconnectView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void serviceDisconnect() {
        super.serviceDisconnect();
        if (disconnectView != null)
            disconnectView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void serviceConnected() {
        super.serviceConnected();
        if (disconnectView != null)
            disconnectView.setVisibility(View.GONE);
    }

    @Override
    protected void receiveUserMessage() {
        super.receiveUserMessage();
        chatFragmentPresenter.getChatList(mContext);
    }

    @Override
    public void initChatList(ChatAdapter chatAdapter) {
        refreshLayout.setRefreshing(false);
        chatList.setAdapter(chatAdapter);
    }

    @Override
    public void loginFailed(Login login) {
        refreshLayout.setRefreshing(false);
        if (!NetworkUtils.isConnected(mContext) || login == null) {
            Intent actionIntent = new Intent(SOCKET_SERVICE_ACTION);
            actionIntent.putExtra(MSG_TYPE, SERVICE_DISCONNECT);
            mContext.sendBroadcast(actionIntent);
        } else if (login != null && login.code == Constant.FAILED) {
            Log.e(TAG, "获取用户登录信息失败，请重新登录");
            ToastUtils.showToast("获取用户登录信息失败，请重新登录");
            SharedPreferencesUtil.getInstance().removeAll();
            ActivityCollector.finishAll();
            SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, false);
            LoginActivity.startActivity(mContext);
        }
    }


    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                startActivity(new Intent(mContext, AddFriendActivity.class));
                break;
            case R.id.sys:
                sys();
                break;
        }
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO) {
            sys();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO) {
            ToastUtils.showToast("你拒绝了摄像头权限");
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    public void sys() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            new IntentIntegrator(((MainActivity) mContext))
                    .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                    //.setPrompt("请对准二维码")// 设置提示语
                    .setCameraId(0)// 选择摄像头,可使用前置或者后置
                    .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                    .setCaptureActivity(QrCodeActivity.class)//自定义扫码界面
                    .initiateScan();// 初始化扫码
        } else {
            EasyPermissions.requestPermissions(this, "扫码需要以下权限:\n\n1.摄像头权限", Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }
}
