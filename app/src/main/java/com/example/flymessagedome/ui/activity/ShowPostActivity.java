package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Post;
import com.example.flymessagedome.ui.adapter.DiscussAdapter;
import com.example.flymessagedome.ui.contract.ShowPostContract;
import com.example.flymessagedome.ui.presenter.PostPresenter;
import com.example.flymessagedome.ui.widget.MaxRecyclerView;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.flymessagedome.utils.TimeUtil.QQFormatTime;

@SuppressLint("NonConstantResourceId")
public class ShowPostActivity extends BaseActivity implements ShowPostContract.View {

    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.head)
    CircleImageView head;

    @BindView(R.id.zan_state)
    ImageView zanState;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.content)
    TextView content;

    @BindView(R.id.content_pics)
    BGANinePhotoLayout pics;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.delete)
    ImageView delete;

    @BindView(R.id.edit)
    ImageView edit;

    @BindView(R.id.discuss_list)
    MaxRecyclerView discussList;

    @Inject
    PostPresenter postPresenter;
    private int postId;
    private final ArrayList<Post.PostBean.CommentsBean> comments = new ArrayList<>();
    private DiscussAdapter adapter;
    private Post.PostBean postBean;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_post;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initDatas() {
        postId = getIntent().getIntExtra("postId", -1);
        if (postId == -1) {
            finish();
            return;
        }
        refreshLayout.setOnRefreshListener(() -> postPresenter.getPost(postId));
        refreshLayout.setRefreshing(true);
        postPresenter.getPost(postId);
        adapter = new DiscussAdapter(comments, mContext);
        discussList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        discussList.setAdapter(adapter);
        discussList.setNestedScrollingEnabled(false);
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            refreshLayout.setEnabled(scrollY == 0);
        });
    }

    @Override
    public void configViews() {
        postPresenter.attachView(this);
    }

    @Override
    public void initPost(Post.PostBean postBean) {
        refreshLayout.setRefreshing(false);
        this.postBean = postBean;
        try {
            if (postBean.getU_id() == LoginActivity.loginUser.getU_id()) {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            }
            HttpProxyCacheServer proxy = FlyMessageApplication.getProxy(mContext);
            Glide.with(mContext)
                    .load(proxy.getProxyUrl(postBean.getU_head()))
                    .into(head);
            ArrayList<String> photos = new ArrayList<>();
            for (Post.PostBean.PostItemsBean postItem : postBean.getPostItems()) {
                photos.add(proxy.getProxyUrl(postItem.getCommunity_post_item_url()));
            }
            content.setText(postBean.getCommunityPostContent());
            pics.setData(photos);
            name.setText(postBean.getU_nick_name());
            time.setText(QQFormatTime(postBean.getCreateTime()));
            if (postBean.getZan_state() == 0)
                for (Post.PostBean.ZanBean zan : postBean.getZans()) {
                    if (zan.getUser_id() == LoginActivity.loginUser.getU_id()) {
                        postBean.setZan_state(1);
                        break;
                    }
                }
            comments.clear();
            comments.addAll(postBean.getComments());
            adapter.notifyDataSetChanged();
            zanState.setImageDrawable(getResources().getDrawable(postBean.getZan_state() == 0 ? R.drawable.zan : R.drawable.zan_sel));
            pics.setDelegate(new BGANinePhotoLayout.Delegate() {
                @Override
                public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
                    photoPreviewWrapper(ninePhotoLayout.getData(), position);
                }

                @Override
                public void onClickExpand(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void zanPostSuccess(int postId) {
        dismissLoadingDialog();
        setResult(2);
        postBean.setZan_state(1);
        zanState.setImageDrawable(getResources().getDrawable(R.drawable.zan_sel));
    }

    @Override
    public void cancelZanPostSuccess(int postId) {
        dismissLoadingDialog();
        setResult(2);
        postBean.setZan_state(0);
        zanState.setImageDrawable(getResources().getDrawable(R.drawable.zan));
    }

    @OnClick({R.id.back, R.id.delete, R.id.edit, R.id.zan_state, R.id.discuss})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.zan_state:
                if (postBean.getZan_state() == 0) {
                    showLoadingDialog(false, "点赞中");
                    postPresenter.zanPost(postId);
                }else {
                    showLoadingDialog(false, "取消点赞中");
                    postPresenter.cancelZanPost(postId);
                }
                break;
        }
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
    public void showError() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String msg) {
        refreshLayout.setRefreshing(false);
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        refreshLayout.setRefreshing(true);
        postPresenter.getPost(postId);
    }

    @Override
    public void tokenExceed() {

    }
}