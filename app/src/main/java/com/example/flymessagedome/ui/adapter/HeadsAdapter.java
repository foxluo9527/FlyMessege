package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.model.HeadModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeadsAdapter extends RecyclerView.Adapter {
    ArrayList<HeadModel.HeadsBean> headsBeans;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private LayoutInflater mLayoutInflater;
    private HttpProxyCacheServer proxyCacheServer;

    public HeadsAdapter(ArrayList<HeadModel.HeadsBean> headsBeans, Context context) {
        this.headsBeans = headsBeans;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        proxyCacheServer= FlyMessageApplication.getProxy(context);
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
        return new HeadViewHolder(mLayoutInflater.inflate(R.layout.old_head_item, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HeadModel.HeadsBean headsBean=headsBeans.get(position);
        ((HeadViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
        Glide.with(context).load(proxyCacheServer.getProxyUrl(headsBean.getHead_img_link())).error(R.mipmap.ic_launcher).into(((HeadViewHolder)holder).head);
        ((HeadViewHolder)holder).time.setText(new SimpleDateFormat("yyyy-MM-dd").format(headsBean.getTime()));
    }

    @Override
    public int getItemCount() {
        return headsBeans.size();
    }
    static class HeadViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.old_head_img)
        ImageView head;
        @BindView(R.id.old_head_time)
        TextView time;
        public HeadViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

