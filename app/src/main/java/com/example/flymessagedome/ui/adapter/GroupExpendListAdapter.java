package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

public class GroupExpendListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private String[] groupNames={"我创建的群聊","我加入的群聊"};
    private ArrayList<ArrayList<GroupBean>> groups;
    private LayoutInflater mLayoutInflater;
    private OnGroupItemClickListener listener;
    private HttpProxyCacheServer proxyCacheServer;
    public GroupExpendListAdapter(Context context, ArrayList<ArrayList<GroupBean>> groups) {
        this.context = context;
        this.groups=groups;
        mLayoutInflater = LayoutInflater.from(context);
        proxyCacheServer= FlyMessageApplication.getProxy(context);
    }
    public interface OnGroupItemClickListener{
        void onClick(int groupPosition,int childPosition);
    };
    public void setOnGroupItemClickListener(OnGroupItemClickListener onGroupClickListener){
        listener=onGroupClickListener;
    }
    @Override
    public int getGroupCount() {
        return groupNames.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupNames[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item,parent,false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView)convertView.findViewById(R.id.label_group_normal);
            groupViewHolder.count=(TextView)convertView.findViewById(R.id.group_count);
            convertView.setTag(groupViewHolder);
        }else {
            groupViewHolder = (GroupViewHolder)convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(groupNames[groupPosition]);
        groupViewHolder.count.setText(groups.get(groupPosition).size()+"");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item,parent,false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.groupHead = (CircleImageView) convertView.findViewById(R.id.group_head);
            childViewHolder.groupName = (TextView)convertView.findViewById(R.id.group_name);
            convertView.setTag(childViewHolder);
        }else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        Glide.with(context).load(proxyCacheServer.getProxyUrl(groups.get(groupPosition).get(childPosition).getG_head_img())).error(R.mipmap.ic_launcher_round)
                .into(childViewHolder.groupHead);
        childViewHolder.groupName.setText(groups.get(groupPosition).get(childPosition).getG_name());
        if (listener!=null){
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(groupPosition,childPosition);
                }
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    static class GroupViewHolder {
        TextView tvTitle;
        TextView count;
    }
    static class ChildViewHolder {
        CircleImageView groupHead;
        TextView groupName;
    }
}
