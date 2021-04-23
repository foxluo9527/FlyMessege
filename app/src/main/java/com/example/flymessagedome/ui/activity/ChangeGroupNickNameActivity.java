package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.GroupMemberDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ChangeGroupNickNameActivity extends BaseActivity {

    @BindView(R.id.group_remark_name)
    EditText remarkName;
    GroupBean groupBean;

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_group_nick_name;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.back, R.id.done})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.done:
                changeRemarkName();
                break;
        }
    }

    @Override
    public void initDatas() {
        Bundle bundle = getIntent().getExtras();
        groupBean = bundle.getParcelable("group");
        if (groupBean == null) {
            ToastUtils.showToast("获取群聊信息失败");
            finish();
            return;
        }
        if (!NetworkUtils.isConnected(mContext)) {
            ToastUtils.showToast("请检查网络连接");
            finish();
            return;
        }
        remarkName.setHint(bundle.getString("remarkName"));
    }

    @Override
    public void configViews() {

    }

    @SuppressLint("StaticFieldLeak")
    private void changeRemarkName() {
        new AsyncTask<Void, Void, Base>() {

            @Override
            protected Base doInBackground(Void... voids) {
                return HttpRequest.changeGroupMemberRemarkName(groupBean.getG_id(), remarkName.getText().toString());
            }

            @Override
            protected void onPostExecute(Base base) {
                try {
                    if (base != null) {
                        ToastUtils.showToast(base.msg);
                        if (base.code == Constant.SUCCESS) {
                            GroupChatActivity.resultRefresh = true;
                            GroupMember g = FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao().queryBuilder()
                                    .where(GroupMemberDao.Properties.G_id.eq(groupBean.getG_id()))
                                    .where(GroupMemberDao.Properties.U_id.eq(LoginActivity.loginUser.getU_id()))
                                    .unique();
                            if (g != null) {
                                g.setG_nick_name(remarkName.getText().toString());
                                FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao().update(g);
                            }
                        }
                    } else {
                        ToastUtils.showToast("修改群昵称失败，请稍后重试");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        }.execute();
    }
}
