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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.adapter.SearchGroupsAdapter;
import com.example.flymessagedome.ui.adapter.SearchUserAdapter;
import com.example.flymessagedome.ui.contract.SearchGroupContract;
import com.example.flymessagedome.ui.presenter.SearchGroupPresenter;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

public class SearchGroupResultActivity extends BaseActivity implements SearchGroupContract.View, BGARefreshLayout.BGARefreshLayoutDelegate,SearchGroupsAdapter.OnRecyclerViewItemClickListener {
    private static final String TAG = "FlyMessage";
    @BindView(R.id.search_empty)
    TextView search_empty;
    @BindView(R.id.group_list)
    RecyclerView recyclerView;
    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.cancel_tv)
    TextView cancelTv;
    @BindView(R.id.cancel)
    ImageView cancel;
    @BindView(R.id.blacklist_refresh)
    BGARefreshLayout mRefreshLayout;
    ArrayList<GroupBean> groupBeans=new ArrayList<>();
    String searchWords;
    int nowPage=1;
    SearchGroupsAdapter adapter;
    @Inject
    SearchGroupPresenter searchGroupPresenter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_search_group_result;
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

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void initDatas() {
        initRefreshLayout();
        searchWords=getIntent().getStringExtra("searchWords");
        adapter=new SearchGroupsAdapter(groupBeans,mContext,this::onItemClick);
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
        searchGroupPresenter.attachView(this);
    }

    @Override
    public void initSearchResult(ArrayList<GroupBean> groupBeans) {
        dismissLoadingDialog();
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        if (groupBeans!=null&&groupBeans.size()!=0){
            this.groupBeans.addAll(groupBeans);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            search_empty.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.GONE);
            search_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.beginRefreshing();
    }

    @Override
    public void showError(String msg) {
        dismissLoadingDialog();
        if (mRefreshLayout!=null){
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();}
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {

    }

    @Override
    public void tokenExceed() {

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
            groupBeans.clear();
            searchGroupPresenter.search(searchWords,20,nowPage);
        }else {
            ToastUtils.showToast("请检查网络连接");
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)){
            if (groupBeans.size()<20){
                return false;
            }
            else if(groupBeans.size()%20!=0){
                ToastUtils.showToast("人家也是有底线的");
                return false;
            }
            nowPage++;
            searchGroupPresenter.search(searchWords,20,nowPage);
            return true;
        }else {
            ToastUtils.showToast("请检查网络连接");
            return false;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle=new Bundle();
        bundle.putParcelable("group",groupBeans.get(position));
        Intent intent=new Intent(mContext,GroupMsgActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
