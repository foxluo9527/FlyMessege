package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.utils.ImageUtils;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class MyQrCodeActivity extends BaseActivity {

    @BindView(R.id.my_qr_code)
    ImageView myQrCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap resource = ImageUtils.generateBitmap("[FlyMessage-addFriend:" + LoginActivity.loginUser.getU_name() + "]", 400, 400, null, 0.05f);
        myQrCodeView.setImageBitmap(resource);
    }

    @OnClick({R.id.back})
    public void onViewClick(View v) {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_qr_code;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }
}
