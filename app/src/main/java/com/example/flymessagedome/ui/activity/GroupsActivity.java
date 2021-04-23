package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.adapter.GroupExpendListAdapter;
import com.example.flymessagedome.ui.contract.GroupContract;
import com.example.flymessagedome.ui.presenter.GroupPresenter;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class GroupsActivity extends BaseActivity implements GroupContract.View {

    @BindView(R.id.group_list)
    ExpandableListView listView;
    ArrayList<ArrayList<GroupBean>> groups = new ArrayList<>();
    @Inject
    GroupPresenter groupPresenter;
    GroupExpendListAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_groups;
    }

    @OnClick({R.id.back, R.id.create_group})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.create_group:
                startActivity(new Intent(mContext, CreateGroupActivity.class));
                break;
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void initDatas() {
        groupPresenter.getGroups();
        showLoadingDialog(true, "加载中");
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupPresenter.getGroupsByPage(20, 1);
    }

    @Override
    public void configViews() {
        groupPresenter.attachView(this);
    }

    @Override
    public void initGroups(ArrayList<ArrayList<GroupBean>> groupBeans) {
        try {
            dismissLoadingDialog();
            groups = groupBeans;
            adapter = new GroupExpendListAdapter(mContext, groups);
            listView.setAdapter(adapter);
            adapter.setOnGroupItemClickListener((groupPosition, childPosition) -> {
                Intent intent = new Intent(mContext, GroupChatActivity.class);
                intent.putExtra("groupId", groups.get(groupPosition).get(childPosition).getG_id());
                startActivity(intent);
            });
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
}
