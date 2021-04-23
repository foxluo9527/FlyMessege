package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.utils.AgeUtils;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import java.text.SimpleDateFormat;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class ShowUserMsgActivity extends BaseActivity {

    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.position)
    TextView position;
    @BindView(R.id.u_name)
    TextView u_name;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.birthday)
    TextView birthday;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.edit_view)
    View editView;
    @BindView(R.id.edit_btn)
    Button editBtn;
    UserBean userBean;
    @BindView(R.id.back)
    ImageView back;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_user_msg;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void initDatas() {
        long u_id = getIntent().getLongExtra("userId", -1);
        if (u_id <= 0) {
            ToastUtils.showToast("用户id错误");
            finish();
            return;
        }
        if (NetworkUtils.isConnected(mContext)) {
            showLoadingDialog(true, "数据加载中");
            new AsyncTask<Void, Void, Users>() {
                @Override
                protected Users doInBackground(Void... voids) {
                    String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
                    return HttpRequest.getUserMsg((int) u_id, loginToken);
                }

                @Override
                protected void onPostExecute(Users user) {
                    try {
                        if (user == null || user.code != Constant.SUCCESS) {
                            ToastUtils.showToast(user.msg);
                            finish();
                            return;
                        }
                        userBean = user.getUser();
                        initUserMsg();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } else {
            ToastUtils.showToast("获取用户信息失败，请检查网络连接");
            userBean = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(u_id);
            if (userBean == null) {
                finish();
                return;
            }
            initUserMsg();
        }
        editBtn.setOnClickListener(v -> {
            if (userBean.getU_id() == LoginActivity.loginUser.getU_id()) {
                startActivity(new Intent(mContext, EditUserMsgActivity.class));
            }
        });
        back.setOnClickListener(v -> finish());
        u_name.setOnLongClickListener(v -> {
            ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setPrimaryClip(ClipData.newPlainText("url", userBean.getU_name()));
            ToastUtils.showToast("用户名已复制到剪切板");
            return false;
        });
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void initUserMsg() {
        dismissLoadingDialog();
        if (userBean.getU_sex() == null) {
            sex.setText("未填");
        } else {
            sex.setText(userBean.getU_sex());
        }
        name.setText(userBean.getU_nick_name());
        u_name.setText(userBean.getU_name());
        position.setText(userBean.getU_position());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String birthdayString = format.format(userBean.getU_brithday());
        birthday.setText(birthdayString);
        age.setText(AgeUtils.getAgeFromBirthTime(birthdayString) + "");
        if (userBean.getU_id() == LoginActivity.loginUser.getU_id()) {
            editView.setVisibility(View.VISIBLE);
        } else {
            editView.setVisibility(View.GONE);
        }
    }

    @Override
    public void configViews() {

    }
}
