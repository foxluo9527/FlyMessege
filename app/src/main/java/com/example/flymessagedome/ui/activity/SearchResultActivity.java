package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.ui.adapter.FriendAdapter;
import com.example.flymessagedome.ui.adapter.SearchUserAdapter;
import com.example.flymessagedome.ui.contract.SearchContract;
import com.example.flymessagedome.ui.presenter.SearchPresenter;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

import static com.example.flymessagedome.service.MessageService.MSG_TYPE;
import static com.example.flymessagedome.service.MessageService.SERVICE_DISCONNECT;
import static com.example.flymessagedome.service.MessageService.SOCKET_SERVICE_ACTION;

public class SearchResultActivity extends BaseActivity implements SearchContract.View,SearchUserAdapter.OnRecyclerViewItemClickListener,BGARefreshLayout.BGARefreshLayoutDelegate {
    private static final String TAG = "FlyMessage";
    @BindView(R.id.search_empty)
    TextView search_empty;
    @BindView(R.id.person_list)
    RecyclerView recyclerView;
    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.cancel_tv)
    TextView cancelTv;
    @BindView(R.id.cancel)
    ImageView cancel;
    ArrayList<SearchUserModel.ResultBean> userBeans=new ArrayList<>();
    @BindView(R.id.blacklist_refresh)
    BGARefreshLayout mRefreshLayout;
    @Inject
    SearchPresenter searchPresenter;
    SearchUserAdapter adapter;
    String searchWords;
    int nowPage=1;
    @Override
    public int getLayoutId() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }
    @OnClick({R.id.cancel,R.id.cancel_tv})
    public void onViewClick(View v){
        if(v.getId()==R.id.cancel_tv){
            if (cancelTv.getText().equals("取消")){
                finish();
            }else{
                if (TextUtils.isEmpty(searchWords)){
                    ToastUtils.showToast("请输入搜索内容");
                }else {
                    mRefreshLayout.beginRefreshing();
                }
            }
        }else {
            v.setVisibility(View.GONE);
            searchEt.setText("");
        }
    }
    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)){
            nowPage=1;
            userBeans.clear();
            searchPresenter.search(searchWords,20,nowPage);
        }else {
            ToastUtils.showToast("请检查网络连接");
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)){
            if (userBeans.size()<20){
                return false;
            }
            else if(userBeans.size()%20!=0){
                ToastUtils.showToast("人家也是有底线的");
                return false;
            }
            nowPage++;
            searchPresenter.search(searchWords,20,nowPage);
            return true;
        }else {
            ToastUtils.showToast("请检查网络连接");
            return false;
        }
    }
    @Override
    public void initDatas() {
        initRefreshLayout();
        searchWords=getIntent().getStringExtra("searchWords");
        adapter=new SearchUserAdapter(userBeans,mContext,this::onItemClick);
        StaggeredGridLayoutManager msgGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(msgGridLayoutManager);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        if (!TextUtils.isEmpty(searchWords)){
            searchEt.setText(searchWords);
            cancelTv.setText("搜索");
            cancel.setVisibility(View.VISIBLE);
            mRefreshLayout.beginRefreshing();
        }
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.equals("\n")){
                    mRefreshLayout.beginRefreshing();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchWords=s.toString();
                if (s.toString().length()>0){
                    cancelTv.setText("搜索");
                    cancel.setVisibility(View.VISIBLE);
                }else {
                    cancelTv.setText("取消");
                    cancel.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void configViews() {
        searchPresenter.attachView(this);
    }

    @Override
    public void initSearchResult(ArrayList<SearchUserModel.ResultBean> users) {
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        dismissLoadingDialog();
        if (users!=null&&users.size()!=0){
            userBeans.addAll(users);
            adapter.setKeyString(searchWords);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            search_empty.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.GONE);
            search_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loginFailed(Login login) {
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        dismissLoadingDialog();
        if (!NetworkUtils.isConnected(mContext)||login==null){
            Intent actionIntent = new Intent(SOCKET_SERVICE_ACTION);
            actionIntent.putExtra(MSG_TYPE,SERVICE_DISCONNECT);
            mContext.sendBroadcast(actionIntent);
        }else if (login!=null&&login.code== Constant.FAILED) {
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
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {

    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public void onItemClick(View view, int position) {
        if (userBeans.get(position).getU_id()!=LoginActivity.loginUser.getU_id()){
            Intent intent=new Intent(mContext,ShowUserActivity.class);
            intent.putExtra("userName",userBeans.get(position).getU_name());
            startActivity(intent);
        }else {
            startActivity(new Intent(mContext,LoginUserMsgActivity.class));
        }
    }
}
