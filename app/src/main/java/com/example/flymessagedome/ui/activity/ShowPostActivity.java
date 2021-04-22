package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Post;
import com.example.flymessagedome.service.UploadService;
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
public class ShowPostActivity extends BaseActivity implements ShowPostContract.View, DiscussAdapter.OnCommentClickListener {

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

    @BindView(R.id.comment)
    EditText comment;

    @Inject
    PostPresenter postPresenter;
    private int postId;
    private final ArrayList<Post.PostBean.CommentsBean> comments = new ArrayList<>();
    private DiscussAdapter adapter;
    private Post.PostBean postBean;
    private int commentCommentId = -1;
    private int commentReplyId = -1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_post;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.bar_bg));
        }
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
        adapter.setListener(this);
        discussList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        discussList.setAdapter(adapter);
        discussList.setNestedScrollingEnabled(false);
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            refreshLayout.setEnabled(scrollY == 0);
        });
        comment.setHint("评论");
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

    @Override
    public void zanCommentSuccess(int commentId) {
        dismissLoadingDialog();
        setResult(2);
        for (Post.PostBean.CommentsBean comment : postBean.getComments()) {
            if (comment.getCommunity_post_comment_id() == commentId) {
                comment.setZan_state(1);
                comment.setZan_num(comment.getZan_num() + 1);
                adapter.notifyItemChanged(postBean.getComments().indexOf(comment));
                break;
            }
        }
    }

    @Override
    public void cancelZanCommentSuccess(int commentId) {
        dismissLoadingDialog();
        setResult(2);
        for (Post.PostBean.CommentsBean comment : postBean.getComments()) {
            if (comment.getCommunity_post_comment_id() == commentId) {
                comment.setZan_state(0);
                comment.setZan_num(comment.getZan_num() - 1);
                adapter.notifyItemChanged(postBean.getComments().indexOf(comment));
                break;
            }
        }
    }

    @Override
    public void commentSuccess() {
        refreshLayout.setRefreshing(true);
        postPresenter.getPost(postId);
        dismissLoadingDialog();
        setResult(2);
    }

    @Override
    public void deleteSuccess() {
        dismissLoadingDialog();
        setResult(2);
        finish();
    }

    @Override
    public void deleteCommentSuccess() {
        dismissLoadingDialog();
        refreshLayout.setRefreshing(true);
        postPresenter.getPost(postId);
        setResult(2);
    }

    @OnClick({R.id.back, R.id.delete, R.id.edit, R.id.zan_state, R.id.discuss, R.id.send, R.id.content})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.zan_state:
                if (postBean.getZan_state() == 0) {
                    showLoadingDialog(false, "点赞中");
                    postPresenter.zanPost(postId);
                } else {
                    showLoadingDialog(false, "取消点赞中");
                    postPresenter.cancelZanPost(postId);
                }
                break;
            case R.id.content:
            case R.id.discuss:
                focusComment("评论", -1, -1);
                break;
            case R.id.edit:
                Intent editIntent = new Intent(mContext, EditPostActivity.class);
                editIntent.putExtra("post", JSON.toJSONString(postBean));
                startActivityForResult(editIntent, 1);
                break;
            case R.id.send:
                String contentString = comment.getText().toString();
                if (TextUtils.isEmpty(contentString)) {
                    ToastUtils.showToast("请输入评论内容");
                } else {
                    if (commentCommentId > 0) {
                        showLoadingDialog(false, "回复评论中");
                        postPresenter.replyComment(contentString, commentCommentId, commentReplyId);
                    } else {
                        showLoadingDialog(false, "评论中");
                        postPresenter.comment(contentString, postId);
                    }
                    comment.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                }
                break;
            case R.id.delete:
                new AlertDialog.Builder(mContext)
                        .setMessage("确认删除此贴?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog, which) -> {
                            showLoadingDialog(true, "删除中");
                            postPresenter.deletePost(postId);
                        }).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2 && data != null) {
            ArrayList<String> addPhotos = data.getStringArrayListExtra("photos");
            ArrayList<UploadService.UploadTaskBean> taskBeans = new ArrayList<>();
            for (String photo : addPhotos) {
                taskBeans.add(new UploadService.UploadTaskBean(postId, photo));
            }
            FlyMessageApplication.getInstances().uploadService.initUploadTask(taskBeans);
            refreshLayout.setRefreshing(true);
            postPresenter.getPost(postId);
            setResult(2);
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
        dismissLoadingDialog();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String msg) {
        refreshLayout.setRefreshing(false);
        dismissLoadingDialog();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        refreshLayout.setRefreshing(true);
        postPresenter.getPost(postId);
        dismissLoadingDialog();
    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public void clickZan(int position) {
        Post.PostBean.CommentsBean comment = postBean.getComments().get(position);
        if (comment.getZan_state() == 0) {
            showLoadingDialog(false, "点赞评论中");
            postPresenter.zanComment(comment.getCommunity_post_comment_id());
        } else {
            showLoadingDialog(false, "取消点赞评论中");
            postPresenter.cancelCommentZan(comment.getCommunity_post_comment_id());
        }
    }

    @Override
    public void clickHead(int position) {
        Intent showIntent = new Intent(mContext, ShowUserActivity.class);
        showIntent.putExtra("userName", postBean.getComments().get(position).getSend_u_name());
        startActivity(showIntent);
    }

    @Override
    public void clickReplyHead(int replyIndex, int position) {
        Intent showIntent = new Intent(mContext, ShowUserActivity.class);
        showIntent.putExtra("userName", postBean.getComments().get(position).getReplies().get(replyIndex).getSend_u_name());
        startActivity(showIntent);
    }

    @Override
    public void clickComment(int position) {
        Post.PostBean.CommentsBean comment = postBean.getComments().get(position);
        focusComment("回复:" + comment.getSend_u_nick_name(), comment.getCommunity_post_comment_id(), -1);
    }

    @Override
    public void clickReply(int replyIndex, int position) {
        Post.PostBean.CommentsBean.RepliesBean reply = postBean.getComments().get(position).getReplies().get(replyIndex);
        focusComment("回复:" + reply.getSend_u_nick_name(), reply.getCommunity_post_comment_id(), reply.getCommunity_post_comment_reply_id());
    }

    @Override
    public void longPressComment(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.post_comment_menu, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            new AlertDialog.Builder(mContext)
                    .setMessage("确认删除此评论?")
                    .setPositiveButton("确定", (dialog, which) -> {
                        showLoadingDialog(false, "删除评论中...");
                        postPresenter.deleteComment(postBean.getComments().get(position).getCommunity_post_comment_id());
                    })
                    .setNegativeButton("取消", null).show();
            return true;
        });
        popupMenu.show();
    }

    @Override
    public void longPressReply(View view, int replyIndex, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.post_comment_menu, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            new AlertDialog.Builder(mContext)
                    .setMessage("确认删除此回复?")
                    .setPositiveButton("确定", (dialog, which) -> {
                        showLoadingDialog(false, "删除回复中...");
                        postPresenter.deleteReply(postBean.getComments().get(position).getReplies().get(replyIndex).getCommunity_post_comment_reply_id());
                    })
                    .setNegativeButton("取消", null).show();
            return false;
        });
        popupMenu.show();
    }

    /**
     * 分三种
     * 1:直接评论帖子 commentId=-1,replyId=-1
     * 2:回复评论 commentId->评论id,replyId=-1
     * 3:回复回复 commentId->评论id,replyId->回复id
     */
    private void focusComment(String hint, int commentId, int replyId) {
        commentCommentId = commentId;
        commentReplyId = replyId;
        comment.setHint(hint);
        comment.setFocusableInTouchMode(true);
        comment.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}