package com.example.flymessagedome.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Base64Util;
import com.example.flymessagedome.utils.CommUtil;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ChangePhoneActivity extends BaseActivity {
    @BindView(R.id.old_phone)
    EditText old_phone;
    @BindView(R.id.new_phone)
    EditText new_phone;
    @BindView(R.id.old_phone_error)
    TextView old_phone_error;
    @BindView(R.id.new_phone_error)
    TextView new_phone_error;
    @BindView(R.id.old_phone_cancel)
    ImageView old_phone_cancel;
    @BindView(R.id.new_phone_cancel)
    ImageView new_phone_cancel;
    @Override
    public int getLayoutId() {
        return R.layout.activity_change_phone;
    }

    @OnClick({R.id.back,R.id.old_phone_cancel,R.id.new_phone_cancel,R.id.change_phone_btn})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.old_phone_cancel:
                old_phone.setText("");
                break;
            case R.id.new_phone_cancel:
                new_phone.setText("");
                break;
            case R.id.change_phone_btn:
                String oldPhone = old_phone.getText().toString();
                if (TextUtils.isEmpty(oldPhone)) {
                    old_phone_error.setVisibility(View.VISIBLE);
                    old_phone_error.setText("您还未输入旧手机号");
                    return;
                } else if (!oldPhone.equals(LoginActivity.loginUser.getU_phone())) {
                    old_phone_error.setVisibility(View.VISIBLE);
                    old_phone_error.setText("旧手机号输入有误");
                    return;
                } else {
                    old_phone_error.setVisibility(View.GONE);
                }
                String newPhone = new_phone.getText().toString();
                if (!CommUtil.isMobileNO(newPhone)) {
                    new_phone_error.setText("请输入正确的新手机号");
                    new_phone_error.setVisibility(View.VISIBLE);
                    return;
                } else if (newPhone.equals(oldPhone)) {
                    new_phone_error.setText("新旧手机号不能相同");
                    new_phone_error.setVisibility(View.VISIBLE);
                    return;
                } else {
                    new_phone_error.setVisibility(View.GONE);
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("确认修改手机号?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changePhone(newPhone);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .setCancelable(false)
                        .create().show();
                break;
        }
    }
    private void changePhone(String newPhone){
        if (NetworkUtils.isConnected(mContext)){
            showLoadingDialog(true,"修改手机号中");
            new AsyncTask<Void,Void, Base>() {
                @Override
                protected Base doInBackground(Void... voids) {
                    Base base=HttpRequest.checkUserPhone(newPhone);
                    if (base!=null&&base.code==Constant.SUCCESS)
                        return HttpRequest.changePhone(newPhone);
                    else if (base!=null)
                        return base;
                    else 
                        return null;
                }

                @Override
                protected void onPostExecute(Base base) {
                    dismissLoadingDialog();
                    if (base==null){
                        ToastUtils.showToast("修改手机号失败");
                    }else{
                        ToastUtils.showToast(base.msg);
                        if (base.code== Constant.SUCCESS){
                            LoginActivity.loginUser.setU_phone(newPhone);
                        }
                    }
                }
            }.execute();
        }
        else {
            ToastUtils.showToast("修改手机号失败，请检查网络");
        }
    }
    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initDatas() {
        old_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                old_phone_error.setVisibility(View.GONE);
                if (s.length() > 0) {
                    old_phone_cancel.setVisibility(View.VISIBLE);
                }else {
                    old_phone_cancel.setVisibility(View.GONE);
                }
            }
        });
        old_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //旧手机号离开焦点
                if (!hasFocus){
                    String oldPhone=old_phone.getText().toString();
                    if (TextUtils.isEmpty(oldPhone)){
                        old_phone_error.setVisibility(View.VISIBLE);
                        old_phone_error.setText("您还未输入旧手机号");
                    }else if (!oldPhone.equals(LoginActivity.loginUser.getU_phone())){
                        old_phone_error.setVisibility(View.VISIBLE);
                        old_phone_error.setText("旧手机号输入有误");
                    }else {
                        old_phone_error.setVisibility(View.GONE);
                    }
                }
            }
        });
        new_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //旧手机号离开焦点
                if (!hasFocus){
                    String newPhone=new_phone.getText().toString();
                    if (!CommUtil.isPhone(newPhone)) {
                        new_phone_error.setText("请输入正确的新手机号");
                        new_phone_error.setVisibility(View.VISIBLE);
                        return;
                    }else {
                        new_phone_error.setVisibility(View.GONE);
                    }
                }
            }
        });
        new_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new_phone_error.setVisibility(View.GONE);
                if (s.length() > 0) {
                    new_phone_cancel.setVisibility(View.GONE);
                }else {
                    new_phone_cancel.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void configViews() {

    }
}
