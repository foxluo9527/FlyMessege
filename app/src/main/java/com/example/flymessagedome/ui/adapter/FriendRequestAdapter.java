package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.model.FriendRequestModel;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendRequestAdapter extends RecyclerView.Adapter {
    public ArrayList<FriendRequestModel.FriendRequestsBean> friendRequests;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private LayoutInflater mLayoutInflater;
    private HttpProxyCacheServer proxyCacheServer;

    public FriendRequestAdapter(ArrayList<FriendRequestModel.FriendRequestsBean> friendRequests, Context context) {
        this.friendRequests = friendRequests;
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
        View view = mLayoutInflater.inflate(R.layout.friend_request_item, null, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendRequestModel.FriendRequestsBean friendRequest = friendRequests.get(position);
        FriendRequestModel.FriendRequestsBean.RqUserBean userBean = friendRequest.getRqUser();
        ((FriendRequestViewHolder) holder).sure_btn.setOnClickListener(v -> listener.onItemClick(v, position));
        ((FriendRequestViewHolder) holder).itemView.setOnClickListener(v -> listener.onItemClick(v, position));
        ((FriendRequestViewHolder) holder).un_sure_btn.setOnClickListener(v -> listener.onItemClick(v, position));
        Glide.with(context).load(proxyCacheServer.getProxyUrl(userBean.getU_head_img()))
                .into(((FriendRequestViewHolder) holder).head);
        if (userBean.getU_sex() == null) {
            ((FriendRequestViewHolder) holder).sex.setVisibility(View.GONE);
        } else {
            ((FriendRequestViewHolder) holder).sex.setVisibility(View.GONE);
            if (userBean.getU_sex().equals("男")) {
                ((FriendRequestViewHolder) holder).sex.setImageDrawable(AppCompatResources.getDrawable(context, R.mipmap.man));
            } else {
                ((FriendRequestViewHolder) holder).sex.setImageDrawable(AppCompatResources.getDrawable(context, R.mipmap.women));
            }
        }
        ((FriendRequestViewHolder) holder).content.setText(friendRequest.getRq_content());
        ((FriendRequestViewHolder) holder).name.setText(userBean.getU_nick_name());
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    static class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head_img)
        CircleImageView head;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.sex)
        ImageView sex;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.agree_btn)
        Button sure_btn;
        @BindView(R.id.dine_btn)
        Button un_sure_btn;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
