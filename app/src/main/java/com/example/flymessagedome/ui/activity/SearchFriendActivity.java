package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.adapter.SearchFriendAdapter;
import com.example.flymessagedome.ui.contract.SearchFriendContract;
import com.example.flymessagedome.ui.presenter.SearchFriendPresenter;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SearchFriendActivity extends BaseActivity implements SearchFriendContract.View {

    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.cancel)
    ImageView cancel;
    @BindView(R.id.contract_list)
    RecyclerView contract_list;
    ArrayList<FriendsBean> friendBeans = new ArrayList<>();
    SearchFriendAdapter friendAdapter;
    @BindView(R.id.contract_view)
    View contract_view;
    @Inject
    SearchFriendPresenter searchFriendPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_friend;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @OnClick({R.id.cancel, R.id.back})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                cancel.setVisibility(View.GONE);
                searchEt.setText("");
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void initDatas() {
        cancel.setVisibility(View.GONE);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    cancel.setVisibility(View.VISIBLE);
                    showLoadingDialog(true, "搜索中");
                    searchFriendPresenter.getFriendList(s.toString());
                    friendAdapter.setkeyString(s.toString());
                } else {
                    cancel.setVisibility(View.GONE);
                    contract_view.setVisibility(View.GONE);
                }
            }
        });
        friendAdapter = new SearchFriendAdapter(friendBeans, mContext);
        StaggeredGridLayoutManager contractGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        contract_list.setLayoutManager(contractGridLayoutManager);
        ((SimpleItemAnimator) contract_list.getItemAnimator()).setSupportsChangeAnimations(false);
        contract_list.setAdapter(friendAdapter);
        contract_list.setHasFixedSize(true);
        contract_list.setNestedScrollingEnabled(false);
        friendAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            Intent intent = new Intent(mContext, ShowUserActivity.class);
            intent.putExtra("userName", friendBeans.get(position).getFriendUser().getU_name());
            startActivity(intent);
        });
        String searchString = getIntent().getStringExtra("searchString");
        searchEt.setText(searchString);
    }

    @Override
    public void configViews() {
        searchFriendPresenter.attachView(this);
    }

    @Override
    public void initFriendList(ArrayList<FriendsBean> friendsBeans) {
        try {
            dismissLoadingDialog();
            if (friendsBeans.size() == 0) {
                contract_view.setVisibility(View.GONE);
            } else {
                contract_view.setVisibility(View.VISIBLE);
                friendBeans.clear();
                friendBeans.addAll(friendsBeans);
                friendAdapter.notifyDataSetChanged();
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
}
