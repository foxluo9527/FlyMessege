package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.adapter.GroupMembersAdapter;
import com.example.flymessagedome.ui.contract.GroupMembersContract;
import com.example.flymessagedome.ui.presenter.GroupMemberPresenter;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ShowGroupMembersActivity extends BaseActivity implements GroupMembersContract.View, GroupMembersAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.edit)
    TextView edit;
    @BindView(R.id.del_members)
    Button del;
    @BindView(R.id.group_members)
    RecyclerView group_members;
    ArrayList<GroupMember> groupMembers = new ArrayList<>();
    GroupMembersAdapter adapter;
    GroupBean groupBean;
    public static boolean[] choices = new boolean[0];
    GroupMember creator;
    @Inject
    GroupMemberPresenter groupMemberPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_group_members;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @OnClick({R.id.edit, R.id.back, R.id.del_members})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.del_members:
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("是否删除所选中群成员")
                        .setPositiveButton("确认", (dialog1, which) -> {
                            dialog1.cancel();
                            ArrayList<GroupMember> delMembers = new ArrayList<>();
                            for (int i = 0; i < groupMembers.size(); i++) {
                                if (choices[i] && groupMembers.get(i).getU_id() != LoginActivity.loginUser.getU_id())
                                    delMembers.add(groupMembers.get(i));
                            }
                            showLoadingDialog(true, "正在删除(" + 0 + "/" + delMembers.size() + ")");
                            groupMemberPresenter.delGroupMember(delMembers);
                            edit.setText("编辑");
                        })
                        .setNegativeButton("取消", (dialog12, which) -> dialog12.cancel())
                        .create().show();
                break;
            case R.id.edit:
                if (adapter == null) {
                    return;
                }
                adapter.changeOnEdit();
                if (adapter.isOnEdit()) {
                    edit.setText("取消");
                    del.setVisibility(View.VISIBLE);
                } else {
                    edit.setText("编辑");
                    del.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void initDatas() {
        try {
            Bundle bundle = getIntent().getExtras();
            groupBean = bundle.getParcelable("group");
            if (groupBean == null) {
                ToastUtils.showToast("获取群聊信息失败");
                finish();
                return;
            }
            if (!NetworkUtils.isConnected(mContext)) {
                ToastUtils.showToast("请检查网络连接");
            }
            del.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            showLoadingDialog(true, "获取群聊成员中");
            groupMemberPresenter.getGroupMembers(groupBean.getG_id(), mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configViews() {
        groupMemberPresenter.attachView(this);
    }

    @Override
    public void initGroupMember(ArrayList<GroupMember> groupMembers, GroupMember creator) {
        try {
            dismissLoadingDialog();
            if (creator.getU_id() == LoginActivity.loginUser.getU_id()) {
                edit.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.GONE);
            }
            this.groupMembers = groupMembers;
            this.creator = creator;
            choices = new boolean[groupMembers.size()];
            adapter = new GroupMembersAdapter(groupMembers, mContext);
            StaggeredGridLayoutManager recordGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            group_members.setLayoutManager(recordGridLayoutManager);
            ((SimpleItemAnimator) group_members.getItemAnimator()).setSupportsChangeAnimations(false);
            group_members.setAdapter(adapter);
            group_members.setHasFixedSize(true);
            group_members.setNestedScrollingEnabled(false);
            adapter.setOnRecyclerViewItemClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initDelGroupMember(int now, int sum) {
        try {
            showLoadingDialog(true, "正在删除(" + now + "/" + sum + ")");
            if (now == sum) {
                dismissLoadingDialog();
                groupMembers = new ArrayList<>();
                initDatas();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {
        try {
            dismissLoadingDialog();
            ToastUtils.showToast(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {

    }

    @Override
    public void tokenExceed() {

    }

    @Override
    public void onItemClick(View view, int position) {
        if (adapter.isOnEdit()) {
            if (creator.getId() != groupMembers.get(position).getId()) {
                choices[position] = !choices[position];
                adapter.notifyDataSetChanged();
            }
        } else {
            UserBean userBean = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(groupMembers.get(position).getU_id());
            if (userBean != null) {
                Intent intent = new Intent(mContext, ShowUserActivity.class);
                intent.putExtra("userName", userBean.getU_name());
                startActivity(intent);
            } else {
                ToastUtils.showToast("获取用户信息失败，请稍后重试");
            }
        }
    }
}
