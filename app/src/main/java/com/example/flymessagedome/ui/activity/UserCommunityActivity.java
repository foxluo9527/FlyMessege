package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.PostListResult;
import com.example.flymessagedome.ui.adapter.PostListAdapter;
import com.example.flymessagedome.ui.contract.UserCommunityContract;
import com.example.flymessagedome.ui.presenter.UserCommunityPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint("NonConstantResourceId")
public class UserCommunityActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, UserCommunityContract.View, PostListAdapter.OnPostClickListener {
    @Inject
    UserCommunityPresenter presenter;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.none)
    TextView none;

    @BindView(R.id.community_refresh)
    BGARefreshLayout mRefreshLayout;

    @BindView(R.id.post_list)
    RecyclerView list;
    private int nowPage = 1;
    private final ArrayList<PostListResult.PostsBean> postsBeans = new ArrayList<>();
    int userId;
    private PostListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.bar_bg));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_community;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initDatas() {
        userId = (int) getIntent().getLongExtra("userId", 0);
        if (userId <= 0) {
            userId = getIntent().getIntExtra("userId", 0);
            if (userId <= 0) {
                finish();
                return;
            }
        }
        title.setText(getIntent().getStringExtra("uName") + "的社区主页");
        initRefreshLayout();
        findViewById(R.id.back).setOnClickListener(v -> finish());
        list.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new PostListAdapter(postsBeans, mContext);
        adapter.setListener(this);
        list.setAdapter(adapter);
        mRefreshLayout.beginRefreshing();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        //解决滑动冲突
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mRefreshLayout.setEnabled(linearLayoutManager.findFirstVisibleItemPosition() == 0);
            }
        });
    }

    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(mContext, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    @Override
    public void configViews() {
        presenter.attachView(this);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        none.setVisibility(View.GONE);
        if (NetworkUtils.isConnected(mContext)) {
            nowPage = 1;
            presenter.getPosts(userId, nowPage);
        } else {
            ToastUtils.showToast("请检查网络连接");
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)) {
            if (postsBeans.size() < 10 || postsBeans.size() % 10 != 0) {
                ToastUtils.showToast("人家也是有底线的");
                return false;
            }
            nowPage++;
            presenter.getPosts(userId, nowPage);
            return true;
        } else {
            ToastUtils.showToast("请检查网络连接");
            return false;
        }
    }

    @Override
    public void clickZan(int position) {
        if (postsBeans.get(position).getZan_state() == 0) {
            ((BaseActivity) mContext).showLoadingDialog(false, "点赞中");
            presenter.zanPost(postsBeans.get(position).getCommunity_post_id());
        } else {
            ((BaseActivity) mContext).showLoadingDialog(false, "取消点赞中");
            presenter.cancelZanPost(postsBeans.get(position).getCommunity_post_id());
        }
    }

    @Override
    public void clickPost(int position) {
        Intent intent = new Intent(mContext, ShowPostActivity.class);
        intent.putExtra("postId", postsBeans.get(position).getCommunity_post_id());
        startActivityForResult(intent, 2);
    }

    @Override
    public void clickHead(int position) {
        Intent showIntent = new Intent(mContext, ShowUserActivity.class);
        showIntent.putExtra("userName", postsBeans.get(position).getU_name());
        startActivity(showIntent);
    }

    @Override
    public void clickItemPosition(ArrayList<String> datas, int itemPosition, int position) {
        photoPreviewWrapper(datas, itemPosition);
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_SHOW_PHOTOS)
    public void photoPreviewWrapper(ArrayList<String> datas, int itemPosition) {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/download");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(mContext)
                    .previewPhotos(datas) // 当前预览的图片路径集合
                    .currentPosition(itemPosition) // 当前预览图片的索引
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_SHOW_PHOTOS, perms);
        }
    }

    @Override
    public void initPostList(List<PostListResult.PostsBean> result) {
        try {
            if (result.size() == 0)
                none.setVisibility(View.VISIBLE);
            else
                none.setVisibility(View.GONE);
            mRefreshLayout.endRefreshing();
            mRefreshLayout.endLoadingMore();
            postsBeans.clear();
            postsBeans.addAll(result);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPostList(List<PostListResult.PostsBean> result) {
        try {
            none.setVisibility(View.GONE);
            mRefreshLayout.endRefreshing();
            mRefreshLayout.endLoadingMore();
            postsBeans.addAll(result);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void zanPostSuccess(int postId) {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
            for (int i = 0; i < postsBeans.size(); i++) {
                if (postsBeans.get(i).getCommunity_post_id() == postId) {
                    postsBeans.get(i).setZan_state(1);
                    postsBeans.get(i).setZanCount(postsBeans.get(i).getZanCount() + 1);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setResult(2);
    }

    @Override
    public void cancelZanPostSuccess(int postId) {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
            for (int i = 0; i < postsBeans.size(); i++) {
                if (postsBeans.get(i).getCommunity_post_id() == postId) {
                    postsBeans.get(i).setZan_state(0);
                    postsBeans.get(i).setZanCount(postsBeans.get(i).getZanCount() - 1);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setResult(2);
    }

    @Override
    public void showError() {
        try {
            mRefreshLayout.endRefreshing();
            mRefreshLayout.endLoadingMore();
            ((BaseActivity) mContext).dismissLoadingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError(String msg) {
        try {
            mRefreshLayout.endRefreshing();
            mRefreshLayout.endLoadingMore();
            ((BaseActivity) mContext).dismissLoadingDialog();
            ToastUtils.showToast(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {
        try {
            mRefreshLayout.endRefreshing();
            mRefreshLayout.endLoadingMore();
            ((BaseActivity) mContext).dismissLoadingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tokenExceed() {
        try {
            ((BaseActivity) mContext).dismissLoadingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}