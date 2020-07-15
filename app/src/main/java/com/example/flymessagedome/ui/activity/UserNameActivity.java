package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.utils.CommUtil;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class UserNameActivity extends BaseActivity {
    @BindView(R.id.old_u_name)
    TextView old_u_name;
    @BindView(R.id.new_u_name)
    EditText new_u_name;
    @BindView(R.id.u_name_error)
    TextView u_name_error;
    @BindView(R.id.cancel)
    ImageView cancel;
    @Override
    public int getLayoutId() {
        return R.layout.activity_user_name;
    }
    @OnClick({R.id.back,R.id.change_name,R.id.cancel})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.change_name:
                String newName=new_u_name.getText().toString();
                if (TextUtils.isEmpty(newName)){
                    u_name_error.setVisibility(View.VISIBLE);
                    u_name_error.setText("请输入新用户名");
                    return;
                } else if (newName.length()<6) {
                    u_name_error.setVisibility(View.VISIBLE);
                    u_name_error.setText("请输入6-20位新用户名");
                    return;
                }else if (CommUtil.isMobileNO(newName)){
                    u_name_error.setVisibility(View.VISIBLE);
                    u_name_error.setText("请勿直接使用电话号码作为飞讯名");
                    return;
                }else if (newName.equals(old_u_name.getText().toString())){
                    u_name_error.setVisibility(View.VISIBLE);
                    u_name_error.setText("新旧飞讯名不能重复");
                    return;
                }else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setMessage("确认修改飞讯名?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    changeName(newName);
                                }})
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();//取消弹出框
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                }
                break;
            case R.id.cancel:
                new_u_name.setText("");
                cancel.setVisibility(View.GONE);
                u_name_error.setVisibility(View.GONE);
                break;
        }
    }
    private void changeName(String newName){
        if (NetworkUtils.isConnected(mContext)){
            showLoadingDialog(true,"修改飞讯名中");
            new AsyncTask<Void,Void, Base>() {
                @Override
                protected Base doInBackground(Void... voids) {
                    Base base= HttpRequest.checkUserName(newName);
                    if (base!=null&&base.code== Constant.SUCCESS)
                        return HttpRequest.changeUserName(newName);
                    else if (base!=null)
                        return base;
                    else
                        return null;
                }

                @Override
                protected void onPostExecute(Base base) {
                    dismissLoadingDialog();
                    if (base==null){
                        ToastUtils.showToast("修改飞讯名失败");
                    }else{
                        ToastUtils.showToast(base.msg);
                        if (base.code== Constant.SUCCESS){
                            LoginActivity.loginUser.setU_name(newName);
                            initDatas();
                        }
                    }
                }
            }.execute();
        }
        else {
            ToastUtils.showToast("修改飞讯名失败，请检查网络");
        }
    }
    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    public void initDatas() {
        old_u_name.setText(LoginActivity.loginUser.getU_name());
        new_u_name.setHint(LoginActivity.loginUser.getU_name());
        u_name_error.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        new_u_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                u_name_error.setVisibility(View.GONE);
                if (s.length()>0){
                    cancel.setVisibility(View.VISIBLE);
                }else {
                    cancel.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void configViews() {

    }
}
