package com.example.flymessagedome.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.base.BaseFragment;
import com.example.flymessagedome.bean.FriendRequest;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.ui.activity.AddFriendActivity;
import com.example.flymessagedome.ui.activity.BlackListActivity;
import com.example.flymessagedome.ui.activity.ChangeRemarkNameActivity;
import com.example.flymessagedome.ui.activity.FriendRequestActivity;
import com.example.flymessagedome.ui.activity.GroupsActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.activity.SearchActivity;
import com.example.flymessagedome.ui.activity.ShowUserActivity;
import com.example.flymessagedome.ui.activity.UserChatActivity;
import com.example.flymessagedome.ui.adapter.FriendAdapter;
import com.example.flymessagedome.ui.contract.FriendContract;
import com.example.flymessagedome.ui.presenter.FriendFragmentPresenter;
import com.example.flymessagedome.ui.widget.MaxRecyclerView;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.flymessagedome.service.MessageService.MSG_TYPE;
import static com.example.flymessagedome.service.MessageService.SERVICE_DISCONNECT;
import static com.example.flymessagedome.service.MessageService.SOCKET_SERVICE_ACTION;

@SuppressLint("NonConstantResourceId")
public class FriendFragment extends BaseFragment implements FriendContract.View, MenuItem.OnMenuItemClickListener {
    @BindView(R.id.friend_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.friend_list)
    MaxRecyclerView recyclerView;
    @BindView(R.id.tv_frq_count)
    TextView frq_count;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @Inject
    FriendFragmentPresenter friendFragmentPresenter;

    int menuPosition;
    FriendAdapter adapter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_friend;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void initDatas() {
        friendFragmentPresenter.getFriends(20, 1);
        friendFragmentPresenter.refreshFriendRequests(20, 1);
        refreshLayout.setOnRefreshListener(this::refresh);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> refreshLayout.setEnabled(scrollView.getScrollY() == 0));
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.fri_req, R.id.add_fri, R.id.black_list, R.id.search_view, R.id.groups})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.add_fri:
                startActivity(new Intent(mContext, AddFriendActivity.class));
                break;
            case R.id.fri_req:
                startActivity(new Intent(mContext, FriendRequestActivity.class));
                break;
            case R.id.black_list:
                startActivity(new Intent(mContext, BlackListActivity.class));
                break;
            case R.id.search_view:
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
            case R.id.groups:
                startActivity(new Intent(mContext, GroupsActivity.class));
                break;
        }
    }

    void refresh() {
        if (!NetworkUtils.isConnected(mContext) || !MainActivity.serviceBinder.getConnectState()) {
            Intent actionIntent = new Intent(SOCKET_SERVICE_ACTION);
            actionIntent.putExtra(MSG_TYPE, SERVICE_DISCONNECT);
            mContext.sendBroadcast(actionIntent);
        }
        refreshLayout.setRefreshing(true);
        friendFragmentPresenter.getFriends(20, 1);
        friendFragmentPresenter.refreshFriendRequests(20, 1);
        new Handler().postDelayed(() -> {
            try {
                if (refreshLayout != null)
                    refreshLayout.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 10000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void configViews() {
        friendFragmentPresenter.attachView(this);
    }

    @Override
    protected void receiveFriendRequest() {
        try {
            super.receiveFriendRequest();
            friendFragmentPresenter.getFriendList();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initFriendList(ArrayList<FriendsBean> friendsBeans) {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
            refreshLayout.setRefreshing(false);
            adapter = new FriendAdapter(friendsBeans, mContext);
            StaggeredGridLayoutManager msgGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(msgGridLayoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            adapter.setOnRecyclerViewItemClickListener(new FriendAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(mContext, ShowUserActivity.class);
                    intent.putExtra("userName", adapter.friendGroupMaps.get(position).getFriendsBean().getFriendUser().getU_name());
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    menuPosition = position;
                    PopupMenu popup = new PopupMenu(mContext, view);//第二个参数是绑定的那个view
                    //获取菜单填充器
                    MenuInflater inflater = popup.getMenuInflater();
                    //填充菜单
                    inflater.inflate(R.menu.home_friend_list_menu, popup.getMenu());
                    //绑定菜单项的点击事件
                    popup.setOnMenuItemClickListener(FriendFragment.this::onMenuItemClick);
                    //显示(这一行代码不要忘记了)
                    popup.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError() {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showError(String msg) {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
            ToastUtils.showToast(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void tokenExceed() {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void loginFailed(Login login) {
        try {
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initFriendRequest(ArrayList<FriendRequest> frq_list) {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
            if (frq_list != null) {
                if (frq_list.size() > 0)
                    frq_count.setVisibility(View.VISIBLE);
                else
                    frq_count.setVisibility(View.GONE);
                frq_count.setText(frq_list.size() + "");
            } else {
                ToastUtils.showToast("获取好友申请失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        FriendsBean f = adapter.friendGroupMaps.get(menuPosition).getFriendsBean();
        if (f == null || f.getFriendUser() == null) {
            ToastUtils.showToast("获取用户信息失败,请刷新后重试");
            return false;
        }
        switch (item.getItemId()) {
            case R.id.add_rmk:
                Intent rmkIntent = new Intent(mContext, ChangeRemarkNameActivity.class);
                rmkIntent.putExtra("userId", f.getF_object_u_id());
                rmkIntent.putExtra("fId", (long) f.getF_id());
                startActivity(rmkIntent);
                break;
            case R.id.add_chat:
                Intent intent = new Intent(mContext, UserChatActivity.class);
                intent.putExtra("userId", (long) f.getF_object_u_id());
                startActivity(intent);
                break;
            case R.id.del_fri:
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("确认删除此联系人")
                        .setPositiveButton("确定", (dialog1, which) -> {
                            ((BaseActivity) mContext).showLoadingDialog(false, "删除好友中");
                            friendFragmentPresenter.delFriend(f);
                        })
                        .setNegativeButton("取消", (dialog12, which) -> dialog12.cancel())
                        .create().show();
                break;
        }
        return false;
    }
}
