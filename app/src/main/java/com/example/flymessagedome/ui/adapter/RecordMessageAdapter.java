package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.utils.StringUtil;
import com.example.flymessagedome.utils.TextSpanUtil;
import com.example.flymessagedome.utils.TimeUtil;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordMessageAdapter extends RecyclerView.Adapter {
    ArrayList<Message> messages;
    String uName;
    String myName;
    String uHead;
    private LayoutInflater mLayoutInflater;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private HttpProxyCacheServer proxyCacheServer;
    String searchString="";

    public RecordMessageAdapter(ArrayList<Message> messages,String searchString, String uName,String uHead,Context context) {
        this.messages = messages;
        this.uName = uName;
        this.searchString=searchString;
        this.uHead=uHead;
        myName=LoginActivity.loginUser.getU_nick_name();
        this.mLayoutInflater=LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
        proxyCacheServer= FlyMessageApplication.getProxy(context);
    }
    public void setKeyString(String searchString){
        this.searchString=searchString;
    }
    public String getSearchString(){
        return searchString;
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        listener=onRecyclerViewItemClickListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchRecordViewHolder(mLayoutInflater.inflate(R.layout.record_message_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchRecordViewHolder viewHolder=(SearchRecordViewHolder)holder;
        Message message=messages.get(position);
        if (message.getM_source_id()!= LoginActivity.loginUser.getU_id()){
            viewHolder.name.setText(uName);
            Glide.with(context).load(proxyCacheServer.getProxyUrl(uHead))
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(viewHolder.headImg);
        }else {
            viewHolder.name.setText(myName);
            Glide.with(context).load(proxyCacheServer.getProxyUrl(LoginActivity.loginUser.getU_head_img()))
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(viewHolder.headImg);
        }
        viewHolder.m_content.setText(TextSpanUtil.getInstant().setColor(message.getM_content()+" ",
                searchString,context.getApplicationContext().getColor(R.color.blue_1)));
        viewHolder.time.setText(TimeUtil.QQFormatTime(message.getM_send_time()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    static class SearchRecordViewHolder extends RecyclerView.ViewHolder{
        @Nullable
        @BindView(R.id.user_head_img)
        CircleImageView headImg;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.m_content)
        TextView m_content;
        @BindView(R.id.tv_msg_time)
        TextView time;
        public SearchRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
