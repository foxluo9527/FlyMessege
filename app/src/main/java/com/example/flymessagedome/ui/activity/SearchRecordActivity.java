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
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.adapter.SearchRecordAdapter;
import com.example.flymessagedome.ui.contract.SearchRecordContract;
import com.example.flymessagedome.ui.presenter.SearchRecordPresenter;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SearchRecordActivity extends BaseActivity implements SearchRecordContract.View {

    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.cancel)
    ImageView cancel;
    @BindView(R.id.record_view)
    View record_view;
    @BindView(R.id.record_list)
    RecyclerView record_list;
    ArrayList<ArrayList<Message>> groupMessages = new ArrayList<>();
    ArrayList<Chat> chats = new ArrayList<>();
    @Inject
    SearchRecordPresenter searchRecordPresenter;

    SearchRecordAdapter recordAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_record;
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
                    searchRecordPresenter.getMessageList(s.toString());
                    recordAdapter.setKeyString(s.toString());
                } else {
                    cancel.setVisibility(View.GONE);
                    record_view.setVisibility(View.GONE);
                }
            }
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
        String searchString = getIntent().getStringExtra("searchString");
        searchEt.setText(searchString);
    }

    @Override
    public void configViews() {
        searchRecordPresenter.attachView(this);
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
                this.chats.addAll(chats);
                this.groupMessages.addAll(groupMessages);
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
