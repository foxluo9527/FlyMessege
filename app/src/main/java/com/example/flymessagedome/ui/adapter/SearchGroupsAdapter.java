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
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchGroupsAdapter extends RecyclerView.Adapter {
    ArrayList<GroupBean> userBeans;
    private LayoutInflater mLayoutInflater;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private HttpProxyCacheServer proxyCacheServer;
    String searchString="";
    public SearchGroupsAdapter(ArrayList<GroupBean> userBeans, Context context,OnRecyclerViewItemClickListener listener) {
        this.userBeans = userBeans;
        this.mLayoutInflater=LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
        proxyCacheServer= FlyMessageApplication.getProxy(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(mLayoutInflater.inflate(R.layout.search_group_item, parent, false));
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchViewHolder viewHolder=(SearchViewHolder)holder;
        GroupBean userBean=userBeans.get(position);
        Glide.with(context).load(proxyCacheServer.getProxyUrl(userBean.getG_head_img()))


                .into(viewHolder.headImg);
        viewHolder.name.setText(userBean.getG_name());
        viewHolder.introduce.setText(""+userBean.getG_introduce());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userBeans.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder{
        @Nullable
        @BindView(R.id.head_img)
        CircleImageView headImg;
        @BindView(R.id.group_name)
        TextView name;
        @BindView(R.id.group_introduce)
        TextView introduce;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
