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
import com.example.flymessagedome.bean.User;
import com.example.flymessagedome.ui.activity.AccountActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountAdapter extends RecyclerView.Adapter {
    ArrayList<User> users;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private LayoutInflater mLayoutInflater;
    private HttpProxyCacheServer proxyCacheServer;

    public AccountAdapter(ArrayList<User> users, Context context) {
        this.users = users;
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
        return new AccountViewHolder(mLayoutInflater.inflate(R.layout.account_item, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try{
            User user = users.get(position);
            Glide.with(context).load(proxyCacheServer.getProxyUrl(user.getU_head_img())).error(R.drawable.icon).into(((AccountViewHolder) holder).head);
            ((AccountViewHolder) holder).name.setText(user.getU_name());
            ((AccountViewHolder) holder).nickName.setText(user.getU_nick_name());
            if (AccountActivity.onEditAccount) {
                ((AccountViewHolder) holder).delBtn.setVisibility(View.VISIBLE);
                ((AccountViewHolder) holder).onLogin.setVisibility(View.GONE);
                ((AccountViewHolder) holder).delBtn.setOnClickListener(v -> listener.onItemClick(v, position));
            } else {
                ((AccountViewHolder) holder).itemView.setOnClickListener(v -> listener.onItemClick(v, position));
                ((AccountViewHolder) holder).delBtn.setVisibility(View.GONE);
                if (LoginActivity.loginUser.getU_id() == user.getU_id()) {
                    ((AccountViewHolder) holder).onLogin.setVisibility(View.VISIBLE);
                } else {
                    ((AccountViewHolder) holder).onLogin.setVisibility(View.GONE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    static class AccountViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.head_img)
        CircleImageView head;
        @BindView(R.id.user_name)
        TextView name;
        @BindView(R.id.user_nick_name)
        TextView nickName;
        @BindView(R.id.del_account)
        ImageView delBtn;
        @BindView(R.id.on_login)
        ImageView onLogin;
        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
