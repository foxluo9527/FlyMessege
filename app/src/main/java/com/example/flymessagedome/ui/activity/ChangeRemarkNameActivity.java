package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.FriendsBeanDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeRemarkNameActivity extends BaseActivity {
    @BindView(R.id.remark_name)
    EditText rmkName;
    @BindView(R.id.cancel)
    ImageView cancel;
    UserBeanDao userBeanDao= FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();
    FriendsBeanDao friendsBeanDao=FlyMessageApplication.getInstances().getDaoSession().getFriendsBeanDao();
    UserBean userBean;
    long f_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_remark_name;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }
    @OnClick({R.id.done,R.id.back,R.id.cancel})
    public void viewClick(View v){
        if (v.getId()==R.id.done){
            showLoadingDialog(true,"修改备注中");
            String newName=rmkName.getText().toString();
            if (TextUtils.isEmpty(newName)){
                newName=userBean.getU_nick_name();
            }
            String finalNewName = newName;
            new AsyncTask<Void,Void, Base>() {
                @SuppressLint("WrongThread")
                @Override
                protected Base doInBackground(Void... voids) {
                    return HttpRequest.changeRMKName(f_id, finalNewName);
                }
                @Override
                protected void onPostExecute(Base base) {
                    dismissLoadingDialog();
                    if (base!=null&&base.code== Constant.SUCCESS){
                        ToastUtils.showToast("备注修改成功");
                        FriendsBean friendsBean=friendsBeanDao.load(f_id);
                        friendsBean.setF_remarks_name(finalNewName);
                        finish();
                    }else {
                        if (base==null){
                            ToastUtils.showToast("备注修改失败:请检查网络连接");
                        }else {
                            ToastUtils.showToast("备注修改失败:"+base.msg);
                        }
                    }
                }
            }.execute();
        }else if (v.getId()==R.id.back){
            finish();
        }else {
            rmkName.setHint(userBean.getU_nick_name());
            rmkName.setText("");
            cancel.setVisibility(View.GONE);
        }
    }
    @Override
    public void initDatas() {
        cancel.setVisibility(View.GONE);
        long u_id=getIntent().getLongExtra("userId",0);
        f_id=getIntent().getLongExtra("fId",0);
        if (u_id==0||f_id==0){
            ToastUtils.showToast("错误用户ID");
            finish();
            return;
        }else {
            userBean=userBeanDao.load(u_id);
            if (userBean==null){
                showLoadingDialog(true,"用户信息加载中");
                if (NetworkUtils.isConnected(this)){
                    new AsyncTask<Void,Void,UserBean>() {
                        @Override
                        protected UserBean doInBackground(Void... voids) {
                            return HttpRequest.getUserMsg((int)u_id, SharedPreferencesUtil.getInstance().getString("loginToken")).getUser();
                        }
                        @Override
                        protected void onPostExecute(UserBean user) {
                            userBean=user;
                            userBeanDao.insertOrReplace(user);
                            rmkName.setHint(userBean.getU_nick_name());
                            dismissLoadingDialog();
                        }
                    }.execute();
                }else {
                    ToastUtils.showToast("获取用户信息失败,请检查网络连接");
                    finish();
                    return;
                }
            }
            rmkName.setHint(userBean.getU_nick_name());
        }
        rmkName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()==0){
                    rmkName.setHint(userBean.getU_nick_name());
                    cancel.setVisibility(View.GONE);
                }else {
                    cancel.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void configViews() {

    }
}
