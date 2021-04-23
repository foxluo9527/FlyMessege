package com.example.flymessagedome.ui.adapter;

import android.annotation.SuppressLint;
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
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.utils.TextSpanUtil;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchRecordAdapter extends RecyclerView.Adapter {
    private final ArrayList<Chat> chats;
    private final LayoutInflater mLayoutInflater;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private final HttpProxyCacheServer proxyCacheServer;
    String searchString = "";

    public SearchRecordAdapter(ArrayList<Chat> chats, Context context) {
        this.chats = chats;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        proxyCacheServer = FlyMessageApplication.getProxy(context);
    }

    public void setKeyString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        listener = onRecyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchRecordViewHolder(mLayoutInflater.inflate(R.layout.search_record_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            SearchRecordViewHolder viewHolder = (SearchRecordViewHolder) holder;
            Chat chat = chats.get(position);
            Glide.with(context).load(proxyCacheServer.getProxyUrl(chat.getChat_head())).into(viewHolder.headImg);
            viewHolder.name.setText(TextSpanUtil.getInstant().setColor(chat.getChat_name(),
                    searchString, context.getApplicationContext().getColor(R.color.blue_1)));
            viewHolder.m_content.setText(TextSpanUtil.getInstant().setColor(chat.getChat_content(),
                    searchString, context.getApplicationContext().getColor(R.color.blue_1)));
            viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(v, position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @SuppressLint("NonConstantResourceId")
    static class SearchRecordViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.user_head_img)
        CircleImageView headImg;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.m_content)
        TextView m_content;

        public SearchRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
