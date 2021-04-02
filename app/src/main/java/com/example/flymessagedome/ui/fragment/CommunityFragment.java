package com.example.flymessagedome.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseFragment;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Weather;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.contract.CommunityContract;
import com.example.flymessagedome.ui.presenter.CommunityPresenter;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.LocationService;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.example.flymessagedome.view.CircleImageView;
import com.xuexiang.citypicker.CityPicker;
import com.xuexiang.citypicker.adapter.OnLocationListener;
import com.xuexiang.citypicker.adapter.OnPickListener;
import com.xuexiang.citypicker.model.City;
import com.xuexiang.citypicker.model.HotCity;
import com.xuexiang.citypicker.model.LocateState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint("NonConstantResourceId")
public class CommunityFragment extends BaseFragment implements CommunityContract.View {
    @BindView(R.id.head)
    CircleImageView head;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.sign)
    TextView sign;

    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

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

    @Inject
    CommunityPresenter communityPresenter;

    private boolean alive = false;
    private String cityName;
    private Handler timeHandler;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_community;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @SuppressLint("SimpleDateFormat")
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

        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            initDatas();
        });

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

        cityName = SharedPreferencesUtil.getInstance().getString("city_name");
        if (TextUtils.isEmpty(cityName)) {
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (EasyPermissions.hasPermissions(mContext, perms)) {
                pickCity();
            } else {
                EasyPermissions.requestPermissions(this, "天气需要获取定位权限", Constant.REQUEST_CODE_PERMISSION_CHOICE_PHOTO, perms);
            }
        }else {
            city.setText(cityName);
            communityPresenter.getWeather(cityName);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alive = false;
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

                    OnBDLocationListener mListener = new OnBDLocationListener();

                    @Override
                    public void onPick(int position, City data) {
                        cityName = data.getName();
                        if (!TextUtils.isEmpty(cityName)){
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
            String imgName=weather.getWeatherimg().split("\\.")[0];
            weatherImg.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(imgName, "drawable", mContext.getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 百度定位
     */
    public static class OnBDLocationListener extends BDAbstractLocationListener {

        private OnLocationListener mOnLocationListener;

        public OnBDLocationListener setOnLocationListener(OnLocationListener onLocationListener) {
            mOnLocationListener = onLocationListener;
            return this;
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

    }

    @Override
    public void showError(String msg) {
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {

    }

    @Override
    public void tokenExceed() {

    }
}
