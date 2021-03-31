package com.example.flymessagedome.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.User;
import com.example.flymessagedome.bean.UserDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.ui.adapter.AccountAdapter;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Base64Util;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class AccountActivity extends BaseActivity {
    @BindView(R.id.account_list)
    RecyclerView recyclerView;
    @BindView(R.id.remember)
    Switch remember;
    @BindView(R.id.auto_login)
    Switch autoLogin;
    @BindView(R.id.edit_account)
    TextView edit;
    ArrayList<User> users;
    AccountAdapter adapter;
    public static boolean onEditAccount = false;
    UserDao userDao = FlyMessageApplication.getInstances().getDaoSession().getUserDao();

    @Override
    protected void onResume() {
        super.onResume();
        onEditAccount = false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.back, R.id.log_out, R.id.exit, R.id.edit_account, R.id.auto_login_view, R.id.remember_view})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit_account:
                onEditAccount = !onEditAccount;
                if (onEditAccount) {
                    edit.setText(R.string.cancel);
                } else {
                    edit.setText(R.string.edit);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.log_out:
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage(getString(R.string.sure_logout))
                        .setPositiveButton(R.string.sure, (dialog1, which) -> {
                            MainActivity.serviceBinder.cancelAll();
                            MainActivity.serviceBinder.closeConnect();
                            ActivityCollector.finishAll();
                            SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, false);
                            LoginActivity.startActivity(mContext);
                            dialog1.cancel();//取消弹出框
                        })
                        .setNegativeButton(R.string.cancel, (dialog12, which) -> {
                            dialog12.cancel();//取消弹出框
                        })
                        .create().show();
                break;
            case R.id.exit:
                AlertDialog.Builder exitDialog = new AlertDialog.Builder(mContext);
                exitDialog.setMessage("确认退出应用?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.serviceBinder.closeConnect();
                                ActivityCollector.finishAll();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
                break;
            case R.id.remember_view:
                remember.setChecked(!remember.isChecked());
                break;
            case R.id.auto_login_view:
                autoLogin.setChecked(!autoLogin.isChecked());
                break;
        }
    }

    @Override
    public void initDatas() {
        users = (ArrayList<User>) userDao.loadAll();
        adapter = new AccountAdapter(users, mContext);
        StaggeredGridLayoutManager msgGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(msgGridLayoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnRecyclerViewItemClickListener(new AccountAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.del_account) {
                    userDao.delete(users.get(position));
                    if (users.get(position).getU_id() == LoginActivity.loginUser.getU_id()) {
                        SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, false);
                        SharedPreferencesUtil.getInstance().putString(Constant.U_NAME, "");
                        SharedPreferencesUtil.getInstance().putString(Constant.U_PASS, "");
                        MainActivity.serviceBinder.closeConnect();
                        ActivityCollector.finishAll();
                        LoginActivity.startActivity(mContext);
                    } else {
                        initDatas();
                    }
                } else if (users.get(position).getU_id() != LoginActivity.loginUser.getU_id()) {
                    showLoadingDialog(false, "切换账号中");
                    new AsyncTask<Void, Void, Login>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        protected Login doInBackground(Void... voids) {
                            return HttpRequest.Login(users.get(position).getU_name(), Base64Util.decode(users.get(position).getU_pass()));
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        protected void onPostExecute(Login login) {
                            dismissLoadingDialog();
                            if (login != null && login.code == Constant.SUCCESS) {
                                SharedPreferencesUtil.getInstance().putString(Constant.U_NAME, login.loginUser.getU_name());
                                SharedPreferencesUtil.getInstance().putString(Constant.U_PASS, Base64Util.decode(login.loginUser.getU_pass()));
                                SharedPreferencesUtil.getInstance().putString(Constant.LOGIN_TOKEN, login.getToken());
                                SharedPreferencesUtil.getInstance().putBoolean(Constant.IS_LOGIN, true);
                                MainActivity.serviceBinder.closeConnect();
                                ActivityCollector.finishAll();
                                if (login.loginUser != null) {
                                    LoginActivity.loginUser = login.loginUser;
                                    UserDao userDao = FlyMessageApplication.getInstances().getDaoSession().getUserDao();
                                    userDao.insertOrReplace(login.loginUser);
                                }
                                MainActivity.startActivity(mContext);
                                finish();
                            } else if (login != null) {
                                ToastUtils.showToast("切换账号失败,账号或密码错误");
                            } else {
                                ToastUtils.showToast("切换账号失败");
                            }
                        }
                    }.execute();
                }
            }
        });
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putBoolean(Constant.REMEMBER_ACCOUNT, isChecked);
                if (!isChecked) {
                    autoLogin.setChecked(false);
                }
            }
        });
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, isChecked);
                if (isChecked) {
                    remember.setChecked(true);
                }
            }
        });
        remember.setChecked(SharedPreferencesUtil.getInstance().getBoolean(Constant.REMEMBER_ACCOUNT, true));
        autoLogin.setChecked(SharedPreferencesUtil.getInstance().getBoolean(Constant.AUTO_LOGIN, true));
    }

    @Override
    public void configViews() {

    }
}
