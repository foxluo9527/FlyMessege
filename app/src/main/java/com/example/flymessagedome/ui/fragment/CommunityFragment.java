package com.example.flymessagedome.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.base.BaseFragment;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.PostListResult;
import com.example.flymessagedome.model.Weather;
import com.example.flymessagedome.service.UploadService;
import com.example.flymessagedome.ui.activity.AddPostActivity;
import com.example.flymessagedome.ui.activity.AddSignActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.LoginUserMsgActivity;
import com.example.flymessagedome.ui.activity.ShowPostActivity;
import com.example.flymessagedome.ui.activity.ShowUserActivity;
import com.example.flymessagedome.ui.activity.UserCommunityActivity;
import com.example.flymessagedome.ui.adapter.PostListAdapter;
import com.example.flymessagedome.ui.contract.CommunityContract;
import com.example.flymessagedome.ui.presenter.CommunityPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.LocationService;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;
import com.xuexiang.citypicker.CityPicker;
import com.xuexiang.citypicker.adapter.OnLocationListener;
import com.xuexiang.citypicker.adapter.OnPickListener;
import com.xuexiang.citypicker.model.City;
import com.xuexiang.citypicker.model.HotCity;
import com.xuexiang.citypicker.model.LocateState;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NonConstantResourceId")
public class CommunityFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, CommunityContract.View, PostListAdapter.OnPostClickListener {
    @BindView(R.id.head)
    CircleImageView head;

    @BindView(R.id.community_refresh)
    BGARefreshLayout mRefreshLayout;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.sign)
    TextView sign;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.city)
    TextView city;

    @BindView(R.id.weather_des)
    TextView weatherDes;

    @BindView(R.id.weather_temp)
    TextView weatherTemp;

    @BindView(R.id.weather_img)
    ImageView weatherImg;

    @BindView(R.id.none)
    TextView none;

    @BindView(R.id.post_list)
    RecyclerView list;

    @Inject
    CommunityPresenter communityPresenter;

    private boolean alive = false;
    private String cityName;
    private Handler timeHandler;
    private ArrayList<String> unUploadPhotos;
    private final ArrayList<PostListResult.PostsBean> postsBeans = new ArrayList<>();
    private int nowPage = 1;
    private PostListAdapter adapter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_community;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @OnClick({R.id.add,R.id.name,R.id.sign,R.id.head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                startActivityForResult(new Intent(mContext, AddPostActivity.class), 1);
                break;
            case R.id.name:
                startActivity(new Intent(mContext, LoginUserMsgActivity.class));
                break;
            case R.id.head:
                Intent intent=new Intent(mContext, UserCommunityActivity.class);
                intent.putExtra("uName",LoginActivity.loginUser.getU_nick_name());
                intent.putExtra("userId",LoginActivity.loginUser.getU_id());
                startActivityForResult(intent,2);
                break;
            case R.id.sign:
                startActivity(new Intent(mContext, AddSignActivity.class));
                break;
        }
    }

    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(mContext, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentView = super.onCreateView(inflater, container, savedInstanceState);
        initRefreshLayout();
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

        return parentView;
    }

    @SuppressLint({"SimpleDateFormat", "HandlerLeak"})
    @Override
    public void initDatas() {

        alive = true;
        if (LoginActivity.loginUser == null) {
            return;
        }
        name.setText(LoginActivity.loginUser.getU_nick_name());

        Glide.with(mContext)
                .load(FlyMessageApplication.getProxy(mContext).getProxyUrl(LoginActivity.loginUser.getU_head_img()))
                .into(head);

        sign.setText(LoginActivity.loginUser.getU_sign());

        timeHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (alive) {
                    try {
                        String time = (String) msg.obj;
                        CommunityFragment.this.time.setText(time);
                        timeHandler.postDelayed(() -> {
                            Message message = new Message();
                            message.obj = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(System.currentTimeMillis());
                            timeHandler.sendMessage(message);
                        }, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        alive = false;
                    }
                }
            }
        };

        city.setOnClickListener(v -> pickCity());

        Message message = new Message();
        message.obj = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(System.currentTimeMillis());
        timeHandler.sendMessage(message);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(cityName)) {
            cityName = SharedPreferencesUtil.getInstance().getString("city_name");
            if (TextUtils.isEmpty(cityName)) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                if (EasyPermissions.hasPermissions(mContext, perms)) {
                    pickCity();
                } else {
                    EasyPermissions.requestPermissions(this, "天气需要获取定位权限", Constant.REQUEST_CODE_PERMISSION_CHOICE_PHOTO, perms);
                }
            } else {
                city.setText(cityName);
                communityPresenter.getWeather(cityName);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alive = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String postContent = data.getStringExtra("content");
            ((BaseActivity) mContext).showLoadingDialog(false, "发布帖子中");
            communityPresenter.addPost(postContent);
            ArrayList<String> photos = data.getStringArrayListExtra("photos");
            if (photos.size() > 0) {
                unUploadPhotos = photos;
            }
        } else if (requestCode == 2 && resultCode == 2) {
            mRefreshLayout.beginRefreshing();
        }
    }

    private void pickCity() {
        ArrayList<HotCity> mHotCities = new ArrayList<>();
        mHotCities.add(new HotCity("北京", "北京", "101010100"));
        mHotCities.add(new HotCity("上海", "上海", "101020100"));
        mHotCities.add(new HotCity("广州", "广东", "101280101"));
        mHotCities.add(new HotCity("深圳", "广东", "101280601"));
        mHotCities.add(new HotCity("杭州", "浙江", "101210101"));
        CityPicker.from(this)
                .setLocatedCity(null)
                .setHotCities(mHotCities)
                .setOnPickListener(new OnPickListener() {

                    final OnBDLocationListener mListener = new OnBDLocationListener();

                    @Override
                    public void onPick(int position, City data) {
                        cityName = data.getName();
                        if (!TextUtils.isEmpty(cityName)) {
                            SharedPreferencesUtil.getInstance().putString("city_name", cityName);
                            LocationService.stop(mListener);
                            city.setText(cityName);
                            communityPresenter.getWeather(cityName);
                        }
                    }

                    @Override
                    public void onCancel() {
                        LocationService.stop(mListener);
                    }

                    @Override
                    public void onLocate(final OnLocationListener locationListener) {
                        //开始定位
                        mListener.setOnLocationListener(locationListener);
                        LocationService.start(mListener);
                    }

                })
                .show();
    }

    @Override
    public void initWeather(Weather.Newslist weather) {
        if (weather == null)
            return;
        try {
            weatherDes.setText(weather.getWeather());
            weatherTemp.setText(weather.getReal());
            String imgName = weather.getWeatherimg().split("\\.")[0];
            weatherImg.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(imgName, "drawable", mContext.getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPostSuccess(int postId) {
        ((BaseActivity) mContext).dismissLoadingDialog();
        if (unUploadPhotos != null && unUploadPhotos.size() > 0) {
            ArrayList<UploadService.UploadTaskBean> taskBeans = new ArrayList<>();
            for (String photo : unUploadPhotos) {
                taskBeans.add(new UploadService.UploadTaskBean(postId, photo));
                FlyMessageApplication.getInstances().uploadService.initUploadTask(taskBeans);
            }
        }
    }

    @Override
    public void initPostList(List<PostListResult.PostsBean> result) {
        if (result.size() == 0)
            none.setVisibility(View.VISIBLE);
        else
            none.setVisibility(View.GONE);
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        postsBeans.clear();
        postsBeans.addAll(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addPostList(List<PostListResult.PostsBean> result) {
        none.setVisibility(View.GONE);
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        postsBeans.addAll(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void zanPostSuccess(int postId) {
        ((BaseActivity) mContext).dismissLoadingDialog();
        for (int i = 0; i < postsBeans.size(); i++) {
            if (postsBeans.get(i).getCommunity_post_id() == postId) {
                postsBeans.get(i).setZan_state(1);
                postsBeans.get(i).setZanCount(postsBeans.get(i).getZanCount() + 1);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public void cancelZanPostSuccess(int postId) {
        ((BaseActivity) mContext).dismissLoadingDialog();
        for (int i = 0; i < postsBeans.size(); i++) {
            if (postsBeans.get(i).getCommunity_post_id() == postId) {
                postsBeans.get(i).setZan_state(0);
                postsBeans.get(i).setZanCount(postsBeans.get(i).getZanCount() - 1);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        none.setVisibility(View.GONE);
        if (NetworkUtils.isConnected(mContext)) {
            nowPage = 1;
            communityPresenter.getPosts(nowPage);
            initDatas();
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
            communityPresenter.getPosts(nowPage);
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
            communityPresenter.zanPost(postsBeans.get(position).getCommunity_post_id());
        } else {
            ((BaseActivity) mContext).showLoadingDialog(false, "取消点赞中");
            communityPresenter.cancelZanPost(postsBeans.get(position).getCommunity_post_id());
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

    /**
     * 百度定位
     */
    public static class OnBDLocationListener extends BDAbstractLocationListener {

        private OnLocationListener mOnLocationListener;

        public void setOnLocationListener(OnLocationListener onLocationListener) {
            mOnLocationListener = onLocationListener;
        }

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (mOnLocationListener != null) {
                mOnLocationListener.onLocationChanged(LocationService.onReceiveLocation(bdLocation), LocateState.SUCCESS);
                LocationService.get().unregisterListener(this);
            }
        }
    }

    @Override
    public void configViews() {
        communityPresenter.attachView(this);
    }

    @Override
    public void showError() {
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        ((BaseActivity) mContext).dismissLoadingDialog();
    }

    @Override
    public void showError(String msg) {
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        ((BaseActivity) mContext).dismissLoadingDialog();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        mRefreshLayout.endRefreshing();
        mRefreshLayout.endLoadingMore();
        ((BaseActivity) mContext).dismissLoadingDialog();
    }

    @Override
    public void tokenExceed() {
        ((BaseActivity) mContext).dismissLoadingDialog();
    }
}
