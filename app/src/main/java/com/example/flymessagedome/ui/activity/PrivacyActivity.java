package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.PrivacyModel;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class PrivacyActivity extends BaseActivity {
    PrivacyModel.PrivacyBean privacy;
    @BindView(R.id.online_state_switch)
    Switch online_state_switch;
    @BindView(R.id.allow_show_switch)
    Switch allow_show_switch;
    @Override
    public int getLayoutId() {
        return R.layout.activity_privacy;
    }
    @OnClick({R.id.back,R.id.online_state,R.id.allow_show,R.id.black_list})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.online_state:
                online_state_switch.setChecked(!online_state_switch.isChecked());
                break;
            case R.id.allow_show:
                allow_show_switch.setChecked(!allow_show_switch.isChecked());
                break;
            case R.id.black_list:
                startActivity(new Intent(mContext,BlackListActivity.class));
                break;
        }
    }
    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initDatas() {
        showLoadingDialog(true,"获取隐私设置信息中");
        new AsyncTask<Void,Void,PrivacyModel>() {
            @Override
            protected PrivacyModel doInBackground(Void... voids) {
                return HttpRequest.getPrivacy();
            }

            @Override
            protected void onPostExecute(PrivacyModel privacyBean) {
                privacy=privacyBean.getPrivacy();
                dismissLoadingDialog();
                if (privacy==null||privacyBean.code== Constant.FAILED){
                    ToastUtils.showToast("获取用户隐私权限信息失败");
                }else {
                    allow_show_switch.setChecked(privacy.getShow_u_message()==1);
                    online_state_switch.setChecked(privacy.getShow_online_state()==1);
                }
            }
        }.execute();
        allow_show_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    privacy.setShow_u_message(1);
                }else {
                    privacy.setShow_u_message(0);
                }
                showLoadingDialog(false,"修改隐私设置中");
                new AsyncTask<Void,Void, Base>(){
                    @Override
                    protected Base doInBackground(Void... voids) {
                        return HttpRequest.changePrivacy(privacy);
                    }

                    @Override
                    protected void onPostExecute(Base base) {
                        dismissLoadingDialog();
                        if (base==null||base.code==Constant.FAILED){
                            ToastUtils.showToast("修改隐私设置失败");
                            if (allow_show_switch.isChecked()){
                                privacy.setShow_u_message(0);
                            }else {
                                privacy.setShow_u_message(1);
                            }
                            allow_show_switch.setChecked(privacy.getShow_u_message()==1);
                        }
                    }
                }.execute();
            }
        });
        online_state_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    privacy.setShow_online_state(1);
                }else {
                    privacy.setShow_online_state(0);
                }
                showLoadingDialog(false,"修改隐私设置中");
                new AsyncTask<Void,Void, Base>(){
                    @Override
                    protected Base doInBackground(Void... voids) {
                        return HttpRequest.changePrivacy(privacy);
                    }

                    @Override
                    protected void onPostExecute(Base base) {
                        dismissLoadingDialog();
                        if (base==null||base.code==Constant.FAILED){
                            ToastUtils.showToast("修改隐私设置失败");
                            if (online_state_switch.isChecked()){
                                privacy.setShow_online_state(0);
                            }else {
                                privacy.setShow_online_state(1);
                            }
                            online_state_switch.setChecked(privacy.getShow_online_state()==1);
                        }
                    }
                }.execute();
            }
        });
    }
    @Override
    public void configViews() {

    }
}
