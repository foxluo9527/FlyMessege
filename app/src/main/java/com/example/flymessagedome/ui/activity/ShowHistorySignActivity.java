package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.UserSignModel;
import com.example.flymessagedome.ui.adapter.SignAdapter;
import com.example.flymessagedome.ui.contract.UserSignContract;
import com.example.flymessagedome.ui.presenter.UserSignPresenter;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

@SuppressLint("NonConstantResourceId")
public class ShowHistorySignActivity extends BaseActivity implements UserSignContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @BindView(R.id.sign_refresh)
    BGARefreshLayout mRefreshLayout;
    @BindView(R.id.sign_view)
    RecyclerView recyclerView;
    @BindView(R.id.del_sign_view)
    View delSignView;
    public static boolean onEditSigns = false;
    public static boolean[] choices;
    SignAdapter adapter;
    int nowPage = 1;
    @Inject
    UserSignPresenter userSignPresenter;
    ArrayList<UserSignModel.SignBean> signBeans = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_history_sign;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onEditSigns = false;
    }

    @OnClick({R.id.back, R.id.sign_manager, R.id.del_sign_btn})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.sign_manager:
                onEditSigns = !onEditSigns;
                if (onEditSigns) {
                    delSignView.setVisibility(View.VISIBLE);
                } else {
                    delSignView.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.del_sign_btn:
                int selectCount = 0;
                for (boolean c :
                        choices) {
                    if (c) {
                        selectCount++;
                    }
                }
                if (selectCount > 0) {
                    for (int i = 0; i < choices.length; i++) {
                        if (choices[i]) {
                            showLoadingDialog(true, "删除所选签名中:" + (i + 1) + "/" + selectCount);
                            userSignPresenter.delUserSign(signBeans.get(i));
                        }
                    }
                } else {
                    ToastUtils.showToast("未选中任何签名");
                }
                break;
        }
    }

    @Override
    public void initDatas() {
        initRefreshLayout();
        adapter = new SignAdapter(signBeans, mContext);
        choices = new boolean[signBeans.size()];
        StaggeredGridLayoutManager msgGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(msgGridLayoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            switch (view.getId()) {
                case R.id.sign_choice:
                    choices[position] = !choices[position];
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.send_btn:
                    showLoadingDialog(true, "发表签名中");
                    UserSignModel.SignBean signBean = signBeans.get(position);
                    userSignPresenter.addUserSign(signBean.getS_content());
                    userSignPresenter.delUserSign(signBean);
                    break;
                case R.id.del_btn:
                    showLoadingDialog(true, "删除签名中");
                    userSignPresenter.delUserSign(signBeans.get(position));
                    break;
            }
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
    }

    @Override
    public void configViews() {
        userSignPresenter.attachView(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initUserSign(ArrayList<UserSignModel.SignBean> signsBeans) {
        try {
            signBeans.addAll(signsBeans);
            Comparator<UserSignModel.SignBean> orderSignComparator = (o1, o2) -> Long.compare(o2.getTime(), o1.getTime());
            signBeans.sort(orderSignComparator);
            choices = new boolean[signBeans.size()];
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
            dismissLoadingDialog();
            ToastUtils.showToast(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {
        try {
            mRefreshLayout.beginRefreshing();
            dismissLoadingDialog();
            ToastUtils.showToast("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)) {
            nowPage = 1;
            signBeans.clear();
            userSignPresenter.getUserSign(20, nowPage);
        } else {
            ToastUtils.showToast("请检查网络连接");
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)) {
            if (signBeans.size() < 20) {
                return false;
            } else if (signBeans.size() % 20 != 0) {
                ToastUtils.showToast("人家也是有底线的");
                return false;
            }
            nowPage++;
            userSignPresenter.getUserSign(20, nowPage);
            return true;
        } else {
            ToastUtils.showToast("请检查网络连接");
            return false;
        }
    }
}
