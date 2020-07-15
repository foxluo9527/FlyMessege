package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.FriendsBeanDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.BlackListModel;
import com.example.flymessagedome.model.CheckBlackListModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.utils.AgeUtils;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;

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

    @Override
    public void initDatas() {
        long u_id=getIntent().getLongExtra("userId",-1);
        if (u_id<=0){
            ToastUtils.showToast("用户id错误");
            finish();
            return;
        }
        if (NetworkUtils.isConnected(mContext)){
            showLoadingDialog(true,"数据加载中");
            new AsyncTask<Void,Void, Users>() {
                @Override
                protected Users doInBackground(Void... voids) {
                    String loginToken= SharedPreferencesUtil.getInstance().getString("loginToken");
                    return HttpRequest.getUserMsg((int)u_id,loginToken);
                }
                @Override
                protected void onPostExecute(Users user) {
                    if (user==null||user.code!= Constant.SUCCESS){
                        ToastUtils.showToast(user.msg);
                        finish();
                        return;
                    }
                    userBean=user.getUser();
                    initUserMsg();
                }
            }.execute();
        }else {
            ToastUtils.showToast("获取用户信息失败，请检查网络连接");
            userBean=FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(u_id);
            if (userBean==null){
                finish();
                return;
            }
            initUserMsg();
        }
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userBean.getU_id()==LoginActivity.loginUser.getU_id()){
                    startActivity(new Intent(mContext,EditUserMsgActivity.class));
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        u_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText("url", userBean.getU_name()));
                ToastUtils.showToast("用户名已复制到剪切板");
                return false;
            }
        });
    }
    private void initUserMsg(){
        dismissLoadingDialog();
        if (userBean.getU_sex()==null){
            sex.setText("未填");
        }else {
            sex.setText(userBean.getU_sex());
        }
        name.setText(userBean.getU_nick_name());
        u_name.setText(userBean.getU_name());
        position.setText(userBean.getU_position());
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String birthdayString=format.format(userBean.getU_brithday());
        birthday.setText(birthdayString);
        age.setText(AgeUtils.getAgeFromBirthTime(birthdayString)+"");
        if (userBean.getU_id()==LoginActivity.loginUser.getU_id()){
            editView.setVisibility(View.VISIBLE);
        }else {
            editView.setVisibility(View.GONE);
        }
    }

    @Override
    public void configViews() {

    }
}
