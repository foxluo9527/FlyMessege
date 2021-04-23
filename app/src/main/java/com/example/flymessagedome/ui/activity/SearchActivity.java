package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.adapter.SearchFriendAdapter;
import com.example.flymessagedome.ui.adapter.SearchRecordAdapter;
import com.example.flymessagedome.ui.contract.SearchFriendContract;
import com.example.flymessagedome.ui.contract.SearchRecordContract;
import com.example.flymessagedome.ui.presenter.SearchFriendPresenter;
import com.example.flymessagedome.ui.presenter.SearchRecordPresenter;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SearchActivity extends BaseActivity implements SearchFriendContract.View, SearchRecordContract.View {

    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.cancel)
    ImageView cancel;
    @BindView(R.id.contract_view)
    View contract_view;
    @BindView(R.id.record_view)
    View record_view;
    @BindView(R.id.contract_list)
    RecyclerView contract_list;
    @BindView(R.id.record_list)
    RecyclerView record_list;
    ArrayList<FriendsBean> friendBeans = new ArrayList<>();
    ArrayList<ArrayList<Message>> groupMessages = new ArrayList<>();
    ArrayList<Chat> chats = new ArrayList<>();
    SearchFriendAdapter friendAdapter;
    SearchRecordAdapter recordAdapter;
    @Inject
    SearchFriendPresenter searchFriendPresenter;
    @Inject
    SearchRecordPresenter searchRecordPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @OnClick({R.id.cancel_btn, R.id.cancel, R.id.contract_more, R.id.record_more})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                cancel.setVisibility(View.GONE);
                searchEt.setText("");
                break;
            case R.id.cancel_btn:
                finish();
                break;
            case R.id.contract_more:
                Intent contractIntent = new Intent(mContext, SearchFriendActivity.class);
                contractIntent.putExtra("searchString", searchEt.getText().toString());
                startActivity(contractIntent);
                break;
            case R.id.record_more:
                Intent recordIntent = new Intent(mContext, SearchRecordActivity.class);
                recordIntent.putExtra("searchString", searchEt.getText().toString());
                startActivity(recordIntent);
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
                    searchRecordPresenter.getMessageList(s.toString());
                    friendAdapter.setkeyString(s.toString());
                    recordAdapter.setKeyString(s.toString());
                } else {
                    cancel.setVisibility(View.GONE);
                    contract_view.setVisibility(View.GONE);
                    record_view.setVisibility(View.GONE);
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
        recordAdapter = new SearchRecordAdapter(chats, mContext);
        StaggeredGridLayoutManager recordGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        record_list.setLayoutManager(recordGridLayoutManager);
        ((SimpleItemAnimator) record_list.getItemAnimator()).setSupportsChangeAnimations(false);
        record_list.setAdapter(recordAdapter);
        record_list.setHasFixedSize(true);
        record_list.setNestedScrollingEnabled(false);
        recordAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            if (groupMessages.get(position).size() > 1) {
                Bundle bundle = new Bundle();
                bundle.putLong("userId", chats.get(position).getSource_id());
                bundle.putString("uName", chats.get(position).getChat_name());
                bundle.putString("keyString", recordAdapter.getSearchString());
                bundle.putParcelableArrayList("listMessage", groupMessages.get(position));
                Intent intent = new Intent();
                intent.setClass(mContext, SearchMessageResultActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else if (groupMessages.get(position).size() == 1) {
                Intent intent = new Intent(mContext, UserChatActivity.class);
                intent.putExtra("userId", chats.get(position).getSource_id());
                intent.putExtra("positionMId", groupMessages.get(position).get(0).getM_id());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void configViews() {
        searchFriendPresenter.attachView(this);
        searchRecordPresenter.attachView(this);
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
                if (friendsBeans.size() > 6) {
                    for (FriendsBean f :
                            friendsBeans) {
                        if (friendsBeans.indexOf(f) < 6) {
                            friendBeans.add(f);
                        } else {
                            break;
                        }
                    }
                } else {
                    friendBeans.addAll(friendsBeans);
                }
                friendAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initMessageList(ArrayList<ArrayList<Message>> groupMessages, ArrayList<Chat> chats) {
        try {
            dismissLoadingDialog();
            if (chats.size() == 0) {
                record_view.setVisibility(View.GONE);
            } else {
                record_view.setVisibility(View.VISIBLE);
                this.chats.clear();
                this.groupMessages.clear();
                if (chats.size() > 6) {
                    for (Chat c :
                            chats) {
                        if (chats.indexOf(c) < 6) {
                            this.chats.add(c);
                            this.groupMessages.add(groupMessages.get(chats.indexOf(c)));
                        } else {
                            break;
                        }
                    }
                } else {
                    this.chats.addAll(chats);
                    this.groupMessages.addAll(groupMessages);
                }
                recordAdapter.notifyDataSetChanged();
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
