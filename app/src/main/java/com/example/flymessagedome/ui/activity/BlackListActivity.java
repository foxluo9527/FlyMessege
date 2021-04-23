package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.BlackListModel;
import com.example.flymessagedome.ui.adapter.BlackListAdapter;
import com.example.flymessagedome.ui.contract.BlackListContract;
import com.example.flymessagedome.ui.presenter.BlackListPresenter;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

@SuppressLint("NonConstantResourceId")
public class BlackListActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, BlackListContract.View {
    ArrayList<BlackListModel.BlackListsBean> blackListsBeans = new ArrayList<>();

    @BindView(R.id.blacklist_refresh)
    BGARefreshLayout mRefreshLayout;
    @BindView(R.id.blacklist_view)
    RecyclerView recyclerView;
    @BindView(R.id.none)
    TextView none;
    BlackListAdapter adapter;
    int nowPage = 1;
    @Inject
    BlackListPresenter blackListPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_black_list;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @OnClick({R.id.back})
    public void onViewClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }
    }

    @Override
    public void initDatas() {
        initRefreshLayout();
        adapter = new BlackListAdapter(blackListsBeans, mContext);
        StaggeredGridLayoutManager msgGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(msgGridLayoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            showLoadingDialog(true, "移除黑名单中");
            blackListPresenter.removeBlackList(blackListsBeans.get(position));
        });
        mRefreshLayout.beginRefreshing();
    }

    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时不显示加载更多控件
        // mRefreshLayout.setIsShowLoadingMoreView(false);
        // 设置正在加载更多时的文本
//        refreshViewHolder.setLoadingMoreText(loadingMoreText);
        // 设置整个加载更多控件的背景颜色资源 id
//        refreshViewHolder.setLoadMoreBackgroundColorRes(loadMoreBackgroundColorRes);
        // 设置整个加载更多控件的背景 drawable 资源 id
//        refreshViewHolder.setLoadMoreBackgroundDrawableRes(loadMoreBackgroundDrawableRes);
        // 设置下拉刷新控件的背景颜色资源 id
//        refreshViewHolder.setRefreshViewBackgroundColorRes(refreshViewBackgroundColorRes);
        // 设置下拉刷新控件的背景 drawable 资源 id
//        refreshViewHolder.setRefreshViewBackgroundDrawableRes(refreshViewBackgroundDrawableRes);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
//        mRefreshLayout.setCustomHeaderView(mBanner, false);
        // 可选配置  -------------END
    }

    @Override
    public void configViews() {
        blackListPresenter.attachView(this);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        none.setVisibility(View.GONE);
        if (NetworkUtils.isConnected(mContext)) {
            nowPage = 1;
            blackListPresenter.getBlackList(20, nowPage);
            blackListsBeans.clear();
        } else {
            ToastUtils.showToast("请检查网络连接");
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)) {
            if (blackListsBeans.size() < 20 || blackListsBeans.size() % 20 != 0) {
                ToastUtils.showToast("人家也是有底线的");
                return false;
            }
            nowPage++;
            blackListPresenter.getBlackList(20, nowPage);
            return true;
        } else {
            ToastUtils.showToast("请检查网络连接");
            return false;
        }
    }

    @Override
    public void initBlackList(ArrayList<BlackListModel.BlackListsBean> blackListBeans) {
        try {
            blackListsBeans.addAll(blackListBeans);
            none.setVisibility(blackListBeans.size() == 0 ? View.VISIBLE : View.GONE);
            adapter.notifyDataSetChanged();
            mRefreshLayout.endRefreshing();
            mRefreshLayout.endLoadingMore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {
        try {
            mRefreshLayout.endRefreshing();
            mRefreshLayout.endLoadingMore();
            ToastUtils.showToast(msg);
            none.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {
        try {
            dismissLoadingDialog();
            mRefreshLayout.beginRefreshing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tokenExceed() {

    }
}
