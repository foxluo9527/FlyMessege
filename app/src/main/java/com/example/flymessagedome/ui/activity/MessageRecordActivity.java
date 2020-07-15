package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.ui.adapter.MessageAdapter;
import com.example.flymessagedome.ui.contract.MessageRecordContract;
import com.example.flymessagedome.ui.presenter.MessageRecordPresenter;
import com.example.flymessagedome.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

public class MessageRecordActivity extends BaseActivity implements MessageRecordContract.View,BGARefreshLayout.BGARefreshLayoutDelegate{
    MessageAdapter adapter;
    @BindView(R.id.record_msg_list)
    RecyclerView recyclerView;
    @BindView(R.id.message_refresh)
    BGARefreshLayout mRefreshLayout;
    @BindView(R.id.del_messages)
    View delMsg;
    @BindView(R.id.edit_message)
    TextView editMsg;
    ArrayList<Message> messages=new ArrayList<>();
    private ChatDao chatDao=FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    private Chat userChat;
    private ArrayList<MessageAdapter.photoMap> photoMaps=new ArrayList<>();
    @Inject
    MessageRecordPresenter messageRecordPresenter;
    UserBean user;
    boolean onEdit=false;
    boolean canLoadMore=true;
    int pageIndex=0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_record;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }
    @OnClick({R.id.back,R.id.del_messages,R.id.edit_message})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.del_messages:
                ArrayList<Integer> onChoiceIndex=adapter.getChoiceIndex();
                if (onChoiceIndex.size()==0){
                    ToastUtils.showToast("未勾选任何聊天记录");
                    return;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("是否删除选中消息")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                                for (Integer i:
                                        onChoiceIndex) {
                                    messageRecordPresenter.delMessage(messages.get(i));
                                }
                                editMsg.setText("编辑");
                                adapter.hideChoice();
                                delMsg.setVisibility(View.GONE);
                                mRefreshLayout.endRefreshing();
                                UserChatActivity.resultRefresh=true;
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
            case R.id.edit_message:
                onEdit=!onEdit;
                if (onEdit){
                    editMsg.setText("取消");
                    adapter.showChoice();
                    delMsg.setVisibility(View.VISIBLE);
                }else {
                    editMsg.setText("编辑");
                    adapter.hideChoice();
                    delMsg.setVisibility(View.GONE);
                }
                break;
        }
    }
    @Override
    public void initDatas() {
        long u_id=getIntent().getLongExtra("userId",-1);
        if (u_id==-1){
            ToastUtils.showToast("用户id错误");
            finish();
            return;
        }
        initRefreshLayout();
        onEdit=false;
        canLoadMore=true;
        pageIndex=0;
        messages=new ArrayList<>();
        photoMaps=new ArrayList<>();
        user=FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao().load(u_id);
        userChat=chatDao.queryBuilder()
                .where(ChatDao.Properties.Source_id.eq(user.getU_id()))
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(ChatDao.Properties.Chat_type.eq(0))
                .unique();
        adapter=new MessageAdapter(mContext,messages,photoMaps);
        StaggeredGridLayoutManager msgGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(msgGridLayoutManager);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
        adapter.setOnRecyclerViewItemClickListener(new MessageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {}
            @Override
            public void onItemMenuClick(View view, int position, int itemId) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                switch (itemId){
                    case 2:
                        //删除
                        dialog.setMessage("是否删除此条消息")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();//取消弹出框
                                        messageRecordPresenter.delMessage(messages.get(position));
                                        UserChatActivity.resultRefresh=true;
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
                }
            }
        });
        mRefreshLayout.beginRefreshing();
    }

    @Override
    public void configViews() {
        messageRecordPresenter.attachView(this);
    }

    @Override
    public void initMessages(ArrayList<Message> receiveMessages) {
        mRefreshLayout.endRefreshing();
        canLoadMore=(receiveMessages.size()==20);
        if (messages.size()>0&&messages.get(0).getM_id()<receiveMessages.get(receiveMessages.size()-1).getM_id()){
            canLoadMore=false;
            ToastUtils.showToast("没有更多数据咯");
        }
        messages.clear();
        messages.addAll ((ArrayList<Message>) FlyMessageApplication.getInstances().getDaoSession().getMessageDao().queryBuilder()
                .whereOr(MessageDao.Properties.M_source_id.eq(user.getU_id()),MessageDao.Properties.M_source_id.eq(LoginActivity.loginUser.getU_id()))
                .whereOr(MessageDao.Properties.M_object_id.eq(LoginActivity.loginUser.getU_id()),MessageDao.Properties.M_object_id.eq(user.getU_id()))
                .where(MessageDao.Properties.M_type.eq(0))
                .orderAsc(MessageDao.Properties.M_send_time)
                .list());
        MessageDao messageDao=FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
        for (Message m:
             messages) {
            m.setIsSend(true);
            messageDao.update(m);
        }
        if (messages.size()>0){
            userChat.setChat_reshow(true);
            userChat.setTime(new Date(messages.get(messages.size()-1).getM_send_time()));
            userChat.setChat_content(messages.get(messages.size()-1).getM_content());
        }else {
            userChat.setChat_reshow(false);
        }
        chatDao.update(userChat);
        adapter.notifyDataSetChanged();
        if (pageIndex==1){
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {
        mRefreshLayout.endRefreshing();
        ToastUtils.showToast(msg);
    }

    @Override
    public void complete() {
        messages.clear();
        messages.addAll ((ArrayList<Message>) FlyMessageApplication.getInstances().getDaoSession().getMessageDao().queryBuilder()
                .whereOr(MessageDao.Properties.M_source_id.eq(user.getU_id()),MessageDao.Properties.M_source_id.eq(LoginActivity.loginUser.getU_id()))
                .whereOr(MessageDao.Properties.M_object_id.eq(LoginActivity.loginUser.getU_id()),MessageDao.Properties.M_object_id.eq(user.getU_id()))
                .where(MessageDao.Properties.M_type.eq(0))
                .orderAsc(MessageDao.Properties.M_send_time)
                .list());
        if (messages.size()>0){
            userChat.setChat_reshow(true);
            userChat.setTime(new Date(messages.get(messages.size()-1).getM_send_time()));
            userChat.setChat_content(messages.get(messages.size()-1).getM_content());
        }else {
            userChat.setChat_reshow(false);
        }
        chatDao.update(userChat);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void tokenExceed() {

    }
    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (canLoadMore){
            pageIndex++;
            messageRecordPresenter.getMessages(20,pageIndex,user.getU_id());
        }else {
            ToastUtils.showToast("没有更多数据咯");
            mRefreshLayout.endRefreshing();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
