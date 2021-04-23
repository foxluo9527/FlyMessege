package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Base64Util;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ChangePassActivity extends BaseActivity {
    @BindView(R.id.old_pass)
    EditText old_pass;
    @BindView(R.id.new_pass)
    EditText new_pass;
    @BindView(R.id.old_pass_error)
    TextView old_pass_error;
    @BindView(R.id.new_pass_error)
    TextView new_pass_error;
    @BindView(R.id.old_pass_cancel)
    ImageView old_pass_cancel;
    @BindView(R.id.new_pass_cancel)
    ImageView new_pass_cancel;
    @BindView(R.id.show_pass_check)
    CheckBox show_pass_check;

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_pass;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick({R.id.back, R.id.old_pass_cancel, R.id.new_pass_cancel, R.id.change_pass_btn})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.old_pass_cancel:
                old_pass.setText("");
                break;
            case R.id.new_pass_cancel:
                new_pass.setText("");
                break;
            case R.id.change_pass_btn:
                String oldPass = old_pass.getText().toString();
                if (TextUtils.isEmpty(oldPass)) {
                    old_pass_error.setVisibility(View.VISIBLE);
                    old_pass_error.setText("您还未输入旧密码");
                    return;
                } else if (!oldPass.equals(Base64Util.decode(LoginActivity.loginUser.getU_pass()))) {
                    old_pass_error.setVisibility(View.VISIBLE);
                    old_pass_error.setText("旧密码输入有误");
                    return;
                } else {
                    old_pass_error.setVisibility(View.GONE);
                }
                String newPass = new_pass.getText().toString();
                if (newPass.length() < 6 || newPass.length() > 20) {
                    new_pass_error.setText("请输入6-20位新密码");
                    new_pass_error.setVisibility(View.VISIBLE);
                    return;
                } else if (newPass.equals(oldPass)) {
                    new_pass_error.setText("新旧密码不能相同");
                    new_pass_error.setVisibility(View.VISIBLE);
                    return;
                } else {
                    new_pass_error.setVisibility(View.GONE);
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("确认修改密码?")
                        .setPositiveButton("确定", (dialog1, which) -> changePass(newPass))
                        .setNegativeButton("取消", (dialog12, which) -> dialog12.cancel())
                        .setCancelable(false)
                        .create().show();
                break;
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @SuppressLint("StaticFieldLeak")
    private void changePass(String newPass) {
        if (NetworkUtils.isConnected(mContext)) {
            showLoadingDialog(true, "修改密码中");
            new AsyncTask<Void, Void, Base>() {
                @Override
                protected Base doInBackground(Void... voids) {
                    return HttpRequest.changePass(newPass);
                }

                @Override
                protected void onPostExecute(Base base) {
                    try {
                        dismissLoadingDialog();
                        if (base == null) {
                            ToastUtils.showToast("修改密码失败，请重新登录后重试");
                        } else {
                            ToastUtils.showToast(base.msg);
                            if (base.code == Constant.SUCCESS) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                dialog.setMessage("密码已修改请重新登录")
                                        .setPositiveButton("确定", (dialog1, which) -> {
                                            MainActivity.serviceBinder.closeConnect();
                                            ActivityCollector.finishAll();
                                            SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, false);
                                            LoginActivity.startActivity(mContext);
                                            dialog1.cancel();
                                        })
                                        .setCancelable(false)
                                        .create().show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } else {
            ToastUtils.showToast("修改密码失败，请检查网络");
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void initDatas() {
        old_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                old_pass_error.setVisibility(View.GONE);
                if (s.length() > 0) {
                    old_pass_cancel.setVisibility(View.VISIBLE);
                } else {
                    old_pass_cancel.setVisibility(View.GONE);
                }
            }
        });
        old_pass.setOnFocusChangeListener((v, hasFocus) -> {
            //旧密码离开焦点
            if (!hasFocus) {
                String oldPass = old_pass.getText().toString();
                if (TextUtils.isEmpty(oldPass)) {
                    old_pass_error.setVisibility(View.VISIBLE);
                    old_pass_error.setText("您还未输入旧密码");
                } else if (!oldPass.equals(Base64Util.decode(LoginActivity.loginUser.getU_pass()))) {
                    old_pass_error.setVisibility(View.VISIBLE);
                    old_pass_error.setText("旧密码输入有误");
                } else {
                    old_pass_error.setVisibility(View.GONE);
                }
            }
        });
        new_pass.setOnFocusChangeListener((v, hasFocus) -> {
            //旧密码离开焦点
            if (!hasFocus) {
                String newPass = new_pass.getText().toString();
                if (newPass.length() < 6 || newPass.length() > 20) {
                    new_pass_error.setText("请输入6-20位新密码");
                    new_pass_error.setVisibility(View.VISIBLE);
                } else {
                    new_pass_error.setVisibility(View.GONE);
                }
            }
        });
        new_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    new_pass_cancel.setVisibility(View.VISIBLE);

                } else {
                    new_pass_cancel.setVisibility(View.GONE);
                }
            }
        });
        show_pass_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                old_pass.setInputType(128);
                new_pass.setInputType(128);
            } else {
                old_pass.setInputType(129);
                new_pass.setInputType(129);
            }
        });
    }

    @Override
    public void configViews() {

    }
}
