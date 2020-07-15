package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.UserSignModel;
import com.example.flymessagedome.ui.contract.UserSignContract;
import com.example.flymessagedome.ui.presenter.UserSignPresenter;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class AddSignActivity extends BaseActivity implements UserSignContract.View {
    @BindView(R.id.sign_edit_view)
    MultiAutoCompleteTextView signEditView;
    @BindView(R.id.sign_send_btn)
    Button signSendBtn;
    String sign;
    @Inject
    UserSignPresenter userSignPresenter;

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_sign;
    }
    @OnClick({R.id.back,R.id.sign_history,R.id.sign_send_btn})
    public void onViewClick(View v){
        if (R.id.back==v.getId()){
            finish();
        }else if (R.id.sign_send_btn==v.getId()){
            sign=signEditView.getText().toString();
            userSignPresenter.addUserSign(sign);
        }else {
            startActivity(new Intent(mContext,ShowHistorySignActivity.class));
        }
    }
    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void initDatas() {
        sign=LoginActivity.loginUser.getU_sign();
        if (!TextUtils.isEmpty(sign)){
            signEditView.setText(sign);
        }
    }

    @Override
    public void configViews() {
        userSignPresenter.attachView(this);
    }

    @Override
    public void initUserSign(ArrayList<UserSignModel.SignBean> signBeans) {

    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {
        dismissLoadingDialog();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
        ToastUtils.showToast("发表签名成功");
        LoginActivity.loginUser.setU_sign(sign);
    }

    @Override
    public void tokenExceed() {

    }
}
