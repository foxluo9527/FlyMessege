package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.ui.ImageViewCheckBox;
import com.example.flymessagedome.ui.activity.ShowGroupMembersActivity;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMembersAdapter extends RecyclerView.Adapter {
    private ArrayList<GroupMember> groupMembers;
    private LayoutInflater mLayoutInflater;
    private Context context;
    private OnRecyclerViewItemClickListener listener;
    private HttpProxyCacheServer proxyCacheServer;
    private UserBeanDao userBeanDao;
    public GroupMembersAdapter(ArrayList<GroupMember> groupMembers, Context context) {
        this.groupMembers = groupMembers;
        this.mLayoutInflater=LayoutInflater.from(context);
        this.context = context;
        proxyCacheServer= FlyMessageApplication.getProxy(context);
        userBeanDao=FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();
    }

    private boolean onEdit=false;
    public void changeOnEdit(){
        onEdit=!onEdit;
        notifyDataSetChanged();
    }
    public boolean isOnEdit(){
        return onEdit;
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
        return new GroupMemberViewHolder(mLayoutInflater.inflate(R.layout.group_member_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupMemberViewHolder viewHolder=(GroupMemberViewHolder)holder;
        GroupMember groupMember=groupMembers.get(position);
        UserBean userBean=userBeanDao.load(groupMember.getU_id());
        if (onEdit){
            if (groupMember.getPower()==1)
                viewHolder.choice.setVisibility(View.GONE);
            else{
                viewHolder.choice.setVisibility(View.VISIBLE);
                viewHolder.choice.setChecked(ShowGroupMembersActivity.choices[position]);
                viewHolder.choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                    @Override
                    public void onCheckStateChanged(boolean isChecked) {
                        ShowGroupMembersActivity.choices[position]=!ShowGroupMembersActivity.choices[position];
                    }
                });
            }
        }
        else viewHolder.choice.setVisibility(View.GONE);
        viewHolder.name.setText(groupMember.getG_nick_name());
        if (groupMember.getPower()>0){
            viewHolder.flag.setVisibility(View.VISIBLE);
        }else {
            viewHolder.flag.setVisibility(View.GONE);
        }
        if (userBean!=null){
            Glide.with(context).load(FlyMessageApplication.getProxy(context).getProxyUrl(userBean.getU_head_img())).into(viewHolder.headImg);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }
    static class GroupMemberViewHolder extends RecyclerView.ViewHolder{
        @Nullable
        @BindView(R.id.head_img)
        CircleImageView headImg;
        @BindView(R.id.group_nick_name)
        TextView name;
        @BindView(R.id.group_creator_flag)
        TextView flag;
        @BindView(R.id.group_choice)
        ImageViewCheckBox choice;
        public GroupMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
