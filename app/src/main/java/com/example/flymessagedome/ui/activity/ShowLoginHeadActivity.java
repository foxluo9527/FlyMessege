package com.example.flymessagedome.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.BlackListModel;
import com.example.flymessagedome.model.HeadModel;
import com.example.flymessagedome.model.UserSignModel;
import com.example.flymessagedome.ui.adapter.BlackListAdapter;
import com.example.flymessagedome.ui.adapter.HeadsAdapter;
import com.example.flymessagedome.ui.contract.BlackListContract;
import com.example.flymessagedome.ui.contract.HeadsContract;
import com.example.flymessagedome.ui.presenter.HeadsPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.ImageUtils;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ShowLoginHeadActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, HeadsContract.View{
    @Inject
    HeadsPresenter headsPresenter;
    @BindView(R.id.heads_refresh)
    BGARefreshLayout mRefreshLayout;
    @BindView(R.id.head_list)
    RecyclerView recyclerView;
    @BindView(R.id.now_head)
    ImageView now_head;
    HeadsAdapter adapter;
    ArrayList<HeadModel.HeadsBean> headsBeans=new ArrayList<>();
    int nowPage=1;
    @Override
    public int getLayoutId() {
        return R.layout.activity_show_login_head;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }
    @OnClick({R.id.back,R.id.now_head})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.now_head:
                showHeadImg(LoginActivity.loginUser.getU_head_img());
                break;
        }
    }
    @Override
    public void initDatas() {
        initRefreshLayout();
        adapter=new HeadsAdapter(headsBeans,mContext);
        StaggeredGridLayoutManager msgGridLayoutManager=new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(msgGridLayoutManager);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnRecyclerViewItemClickListener(new HeadsAdapter.OnRecyclerViewItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(View view, int position) {
                showHeadPopupMenu(mContext,position);
            }
        });
        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(LoginActivity.loginUser.getU_head_img())).error(R.mipmap.ic_launcher).into(now_head);
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
        headsPresenter.attachView(this);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)){
            nowPage=1;
            headsPresenter.getHeads(20,nowPage);
            headsBeans.clear();
        }else {
            ToastUtils.showToast("请检查网络连接");
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isConnected(mContext)){
            if (headsBeans.size()<20){
                return false;
            }
            else if(headsBeans.size()%20!=0){
                ToastUtils.showToast("人家也是有底线的");
                return false;
            }
            nowPage++;
            headsPresenter.getHeads(20,nowPage);
            return true;
        }else {
            ToastUtils.showToast("请检查网络连接");
            return false;
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {
        dismissLoadingDialog();
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        mRefreshLayout.beginRefreshing();
    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public void initHeads(ArrayList<HeadModel.HeadsBean> headBeans) {
        dismissLoadingDialog();
        Comparator<HeadModel.HeadsBean> orderHeadComparator = (o1, o2) -> {
            if (o1.getTime()<o2.getTime()){
                return 1;
            }else {
                return -1;
            }
        };
        try {
            Collections.sort(headBeans, orderHeadComparator);
        }catch (Exception e){
            e.printStackTrace();
        }
        headsBeans.addAll(headBeans);
        adapter.notifyDataSetChanged();
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
    }

    @Override
    public void initLoginHead(String headUrl) {
        dismissLoadingDialog();
        LoginActivity.loginUser.setU_head_img(headUrl);
        FlyMessageApplication.getInstances().getDaoSession().getUserDao().update(LoginActivity.loginUser);
        Glide.with(mContext).load(FlyMessageApplication.getProxy(mContext).getProxyUrl(headUrl)).error(R.mipmap.ic_launcher).into(now_head);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showHeadPopupMenu(Context context,int position) {
        final View popView = View.inflate(context,R.layout.old_head_popup,null);
        View cancel=popView.findViewById(R.id.cancel_item);
        View show_img_item=popView.findViewById(R.id.show_img_item);
        View change_head_item=popView.findViewById(R.id.change_head_item);
        View del_img_item=popView.findViewById(R.id.del_img_item);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = ImageUtils.dip2px(mContext,213);
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp =  ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.cancel_item:
                        popupWindow.dismiss();
                        break;
                    case R.id.show_img_item:
                        popupWindow.dismiss();
                        showHeadImg(headsBeans.get(position).getHead_img_link());
                        break;
                    case R.id.change_head_item:
                        popupWindow.dismiss();
                        headsPresenter.changeOldHead(headsBeans.get(position));
                        break;
                    case R.id.del_img_item:
                        popupWindow.dismiss();
                        headsPresenter.delHead(headsBeans.get(position).getH_id());
                        break;
                }
            }
        };
        cancel.setOnClickListener(listener);
        show_img_item.setOnClickListener(listener);
        change_head_item.setOnClickListener(listener);
        del_img_item.setOnClickListener(listener);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    @AfterPermissionGranted(Constant.REQUEST_CODE_SHOW_PHOTOS)
    public void showHeadImg(String url) {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/download");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(mContext)
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            photoPreviewIntentBuilder.previewPhoto(url);
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_SHOW_PHOTOS, perms);
        }
    }
}
