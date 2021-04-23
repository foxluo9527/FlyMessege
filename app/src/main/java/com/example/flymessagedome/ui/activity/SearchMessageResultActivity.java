package com.example.flymessagedome.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.ui.adapter.RecordMessageAdapter;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SearchMessageResultActivity extends BaseActivity {

    @BindView(R.id.record_msg)
    TextView recordMsg;
    @BindView(R.id.name)
    TextView name;
    ArrayList<Message> messages;
    UserBean userBean;
    @BindView(R.id.record_message_list)
    RecyclerView record_list;
    String uName;
    String keyString;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_message_result;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.back})
    public void onViewClick(View v) {
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initDatas() {
        Bundle bundle = getIntent().getExtras();
        long userId = bundle.getLong("userId", -1);
        messages = bundle.getParcelableArrayList("listMessage");
        if (messages.size() == 0) {
            ToastUtils.showToast("获取记录失败");
            finish();
            return;
        }
        userBean = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(userId);
        uName = bundle.getString("uName");
        keyString = bundle.getString("keyString");
        name.setText(uName.toString());
        recordMsg.setText("共" + messages.size() + "条关于'" + keyString + "'的聊天记录");
        RecordMessageAdapter adapter = new RecordMessageAdapter(messages, keyString, uName, userBean.getU_head_img(), mContext);
        StaggeredGridLayoutManager recordGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        record_list.setLayoutManager(recordGridLayoutManager);
        ((SimpleItemAnimator) record_list.getItemAnimator()).setSupportsChangeAnimations(false);
        record_list.setAdapter(adapter);
        record_list.setHasFixedSize(true);
        record_list.setNestedScrollingEnabled(false);
        adapter.setOnRecyclerViewItemClickListener((view, position) -> {
            Intent intent = new Intent(mContext, UserChatActivity.class);
            intent.putExtra("userId", userBean.getU_id());
            intent.putExtra("positionMId", messages.get(position).getM_id());
            startActivity(intent);
        });
    }

    @Override
    public void configViews() {

    }
}
