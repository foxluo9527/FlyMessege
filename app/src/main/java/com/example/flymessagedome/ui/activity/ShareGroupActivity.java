package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.utils.ImageUtils;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ShareGroupActivity extends BaseActivity {

    @BindView(R.id.group_qr_code)
    ImageView myQrCodeView;
    @BindView(R.id.group_name)
    TextView group_name;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        GroupBean groupBean = bundle.getParcelable("group");
        group_name.setText("" + groupBean.getG_name());
        myQrCodeView.setImageBitmap(ImageUtils.generateBitmap("[FlyMessage-addGroup:" + groupBean.getG_id() + "]", 400, 400, null, 0.2f));
    }

    @OnClick({R.id.back})
    public void onViewClick(View v) {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_share_group;
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
