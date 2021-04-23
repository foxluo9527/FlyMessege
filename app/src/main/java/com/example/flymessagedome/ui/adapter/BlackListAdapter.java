package com.example.flymessagedome.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.model.BlackListModel;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlackListAdapter extends RecyclerView.Adapter {
    private final ArrayList<BlackListModel.BlackListsBean> blackListsBeans;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private final LayoutInflater mLayoutInflater;
    private final HttpProxyCacheServer proxyCacheServer;

    public BlackListAdapter(ArrayList<BlackListModel.BlackListsBean> blackListsBeans, Context context) {
        this.blackListsBeans = blackListsBeans;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        proxyCacheServer = FlyMessageApplication.getProxy(context);
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
        return new BlackListViewHolder(mLayoutInflater.inflate(R.layout.blacklist_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            BlackListModel.BlackListsBean blackListsBean = blackListsBeans.get(position);
            ((BlackListViewHolder) holder).remove_btn.setOnClickListener(v -> listener.onItemClick(v, position));
            BlackListModel.BlackListsBean.UserBean userBean = blackListsBean.getUser();
            if (userBean != null) {
                Glide.with(context).load(proxyCacheServer.getProxyUrl(userBean.getU_head_img())).into(((BlackListViewHolder) holder).head);
                ((BlackListViewHolder) holder).name.setText(userBean.getU_nick_name() + "(" + userBean.getU_name() + ")");
            } else {
                ((BlackListViewHolder) holder).name.setText("UN-KNOWN");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override

    public int getItemCount() {
        return blackListsBeans.size();
    }

    @SuppressLint("NonConstantResourceId")
    static class BlackListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head_img)
        CircleImageView head;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.dine_btn)
        Button remove_btn;

        public BlackListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
