package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint({"NonConstantResourceId", "StaticFieldLeak"})
public class RequestFriendActivity extends BaseActivity {
    long u_id;

    @BindView(R.id.rq_content)
    MultiAutoCompleteTextView rq_content;
    @BindView(R.id.rmk_name)
    EditText rmk_name;

    @Override
    public int getLayoutId() {
        return R.layout.activity_request_friend;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.back, R.id.send_fri_rq})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.send_fri_rq:
                showLoadingDialog(false, "正在发送");
                String rq_words = rq_content.getText().toString();
                String rq_rmk_name = rmk_name.getText().toString();
                new AsyncTask<Void, Void, Base>() {
                    @Override
                    protected Base doInBackground(Void... voids) {
                        return HttpRequest.addFriendRequest(u_id, rq_words, rq_rmk_name);
                    }

                    @Override
                    protected void onPostExecute(Base base) {
                        try {
                            dismissLoadingDialog();
                            if (base.code == Constant.SUCCESS) {
                                ToastUtils.showToast("发送成功");
                            } else {
                                ToastUtils.showToast("发送失败");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
                break;
        }
    }

    @Override
    public void initDatas() {
        showLoadingDialog(false, "获取用户信息中");
        String uName = getIntent().getStringExtra("uName");
        if (uName == null) {
            ToastUtils.showToast("用户信息错误");
            finish();
            return;
        }
        new AsyncTask<Void, Void, Users>() {
            @Override
            protected Users doInBackground(Void... voids) {
                return HttpRequest.getUserMsg(uName, SharedPreferencesUtil.getInstance().getString("loginToken"));
            }

            @Override
            protected void onPostExecute(Users base) {
                try {
                    dismissLoadingDialog();
                    if (base == null || base.code != Constant.SUCCESS) {
                        ToastUtils.showToast("获取用户信息错误");
                    } else {
                        u_id = base.getUser().getU_id();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    public void configViews() {

    }
}
