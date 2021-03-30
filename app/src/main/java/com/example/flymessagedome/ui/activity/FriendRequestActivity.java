package com.example.flymessagedome.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.FriendRequestModel;
import com.example.flymessagedome.ui.adapter.FriendRequestAdapter;
import com.example.flymessagedome.ui.contract.FriendRequestContract;
import com.example.flymessagedome.ui.presenter.FriendRequestPresenter;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class FriendRequestActivity extends BaseActivity implements FriendRequestContract.View {
    @BindView(R.id.fri_rq_list)
    RecyclerView recyclerView;
    @BindView(R.id.none)
    TextView none;
    @Inject
    FriendRequestPresenter friendRequestPresenter;
    FriendRequestAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_friend_request;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @OnClick({R.id.back})
    public void onViewClick(View v) {
        finish();
    }

    @Override
    public void initDatas() {
        showLoadingDialog(false, "数据加载中");
        friendRequestPresenter.getFriendRequests(20, 1);
    }

    @Override
    public void configViews() {
        friendRequestPresenter.attachView(this);
    }

    @Override
    public void initFriendRequest(ArrayList<FriendRequestModel.FriendRequestsBean> frq_list) {
        dismissLoadingDialog();
        if (frq_list == null) {
            ToastUtils.showToast("获取数据失败，请稍后重试");
            none.setVisibility(View.VISIBLE);
        } else if (frq_list.size() > 0) {
            none.setVisibility(View.GONE);
            adapter = new FriendRequestAdapter(frq_list, mContext);
            StaggeredGridLayoutManager msgGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(msgGridLayoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            adapter.setOnRecyclerViewItemClickListener((view, position) -> {
                if (view.getId() == R.id.agree_btn) {
                    friendRequestPresenter.agreeFriendRequest(adapter.friendRequests.get(position));
                } else if (view.getId() == R.id.dine_btn) {
                    friendRequestPresenter.delFriendRequest(adapter.friendRequests.get(position));
                } else {
                    Intent intent = new Intent(mContext, ShowUserActivity.class);
                    intent.putExtra("userName", frq_list.get(position).getRqUser().getU_name());
                    startActivity(intent);
                }
            });
        }else {
            none.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError() {
        dismissLoadingDialog();
    }

    @Override
    public void showError(String msg) {
        dismissLoadingDialog();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
        initDatas();
    }

    @Override
    public void tokenExceed() {

    }
}
