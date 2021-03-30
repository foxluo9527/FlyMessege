package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.utils.StringUtil;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendAdapter extends RecyclerView.Adapter {
    public ArrayList<FriendGroupMap> friendGroupMaps = new ArrayList<>();
    ArrayList<FriendsBean> friendsBeans;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private final LayoutInflater mLayoutInflater;
    private HttpProxyCacheServer proxyCacheServer;

    public FriendAdapter(ArrayList<FriendsBean> friendsBeans, Context context) {
        this.context = context;
        this.friendsBeans = friendsBeans;
        mLayoutInflater = LayoutInflater.from(context);
        proxyCacheServer = FlyMessageApplication.getProxy(context);
        initFriendMsg(friendsBeans);
    }

    public void initFriendMsg(ArrayList<FriendsBean> friendsBeans) {
        ArrayList<Character> groups = new ArrayList<>();
        friendGroupMaps = new ArrayList<>();
        for (FriendsBean friend :
                friendsBeans) {
            String name;
            if (!TextUtils.isEmpty(friend.getF_remarks_name()))
                name = friend.getF_remarks_name();
            else {
                if (friend.getFriendUser() != null)
                    name = friend.getFriendUser().getU_nick_name();
                else
                    name = "";
            }
            String topKey;
            if (TextUtils.isEmpty(name) || StringUtil.getFirstLetter(name.charAt(0)) == null)
                topKey = "null";
            else
                topKey = StringUtil.getFirstLetter(name.charAt(0)).toString();
            if (topKey.equals("null")) {
                friendGroupMaps.add(new FriendGroupMap(friend, "#", false));
            } else {
                friendGroupMaps.add(new FriendGroupMap(friend, topKey, false));
            }
        }
        for (FriendGroupMap friendGroupMap :
                friendGroupMaps) {
            if (!groups.contains(friendGroupMap.getTopKey().charAt(0))) {
                groups.add(friendGroupMap.getTopKey().charAt(0));
            }
        }
        char[] letterGroups = new char[groups.size()];
        int index = 0;
        for (Character c :
                groups) {
            letterGroups[index] = c;
            index++;
        }
        Arrays.sort(letterGroups);
        Map<Character, Integer> groupOrder = new HashMap<>(letterGroups.length);
        for (int i = 0; i < groups.size(); i++) {
            groupOrder.put(letterGroups[i], i);
        }
        Comparator<FriendGroupMap> orderlyGroupComparator = (o1, o2) -> {
            int diff = groupOrder.get(o1.getTopKey().charAt(0)).compareTo(groupOrder.get(o2.getTopKey().charAt(0)));
            return diff == 0 ? o2.getFriendsBean().getF_remarks_name().compareTo(o1.getFriendsBean().getF_remarks_name()) : diff;
        };
        try {
            Collections.sort(friendGroupMaps, orderlyGroupComparator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        listener = onRecyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = mLayoutInflater.inflate(R.layout.friend_list_first_item, null, false);
            return new TopFriendViewHolder(view);
        } else {
            view = mLayoutInflater.inflate(R.layout.friend_list_item, null, false);
            return new NormalFriendViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendGroupMap map = friendGroupMaps.get(position);
        if (map.friendsBean.getFriendUser() == null) {
            return;
        }
        proxyCacheServer = FlyMessageApplication.getProxy(context);
        if (holder instanceof TopFriendViewHolder) {
            ((TopFriendViewHolder) holder).topTv.setText(friendGroupMaps.get(position).getTopKey());
            ((TopFriendViewHolder) holder).name.setText(friendGroupMaps.get(position).friendsBean.getF_remarks_name());
            if (friendGroupMaps.get(position).friendsBean.isIsOnline())
                ((TopFriendViewHolder) holder).state.setText("[在线]" + friendGroupMaps.get(position).friendsBean.getFriendUser().getU_sign());
            else
                ((TopFriendViewHolder) holder).state.setText("[离线]" + friendGroupMaps.get(position).friendsBean.getFriendUser().getU_sign());
            String headUrl = friendGroupMaps.get(position).friendsBean.getFriendUser().getU_head_img();
            if (headUrl.contains("http")) {
                headUrl = proxyCacheServer.getProxyUrl(headUrl);
            }
            Glide.with(context).load(headUrl).into(((TopFriendViewHolder) holder).head);
            if (listener != null) {
                ((TopFriendViewHolder) holder).main.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.onItemLongClick(v, position);
                        return false;
                    }
                });
                ((TopFriendViewHolder) holder).main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(v, position);
                    }
                });
            }
        } else {
            ((NormalFriendViewHolder) holder).name.setText(friendGroupMaps.get(position).friendsBean.getF_remarks_name());
            if (friendGroupMaps.get(position).friendsBean.isIsOnline())
                ((NormalFriendViewHolder) holder).state.setText("[在线]" + friendGroupMaps.get(position).friendsBean.getFriendUser().getU_sign());
            else
                ((NormalFriendViewHolder) holder).state.setText("[离线]" + friendGroupMaps.get(position).friendsBean.getFriendUser().getU_sign());
            String headUrl = friendGroupMaps.get(position).friendsBean.getFriendUser().getU_head_img();
            if (headUrl.contains("http")) {
                headUrl = proxyCacheServer.getProxyUrl(headUrl);
            }
            Glide.with(context).load(headUrl).into(((NormalFriendViewHolder) holder).head);
            if (listener != null) {
                ((NormalFriendViewHolder) holder).main.setOnLongClickListener(v -> {
                    listener.onItemLongClick(v, position);
                    return false;
                });
                ((NormalFriendViewHolder) holder).main.setOnClickListener(v -> listener.onItemClick(v, position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return friendGroupMaps.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else if (!friendGroupMaps.get(position).getTopKey().equals(friendGroupMaps.get(position - 1).getTopKey())) {
            return 1;
        } else {
            return 0;
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    static class NormalFriendViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.f_head)
        CircleImageView head;
        @BindView(R.id.f_name)
        TextView name;
        @BindView(R.id.f_state)
        TextView state;
        @BindView(R.id.main_view)
        View main;

        public NormalFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class TopFriendViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.f_group)
        TextView topTv;
        @BindView(R.id.f_head)
        CircleImageView head;
        @BindView(R.id.f_name)
        TextView name;
        @BindView(R.id.f_state)
        TextView state;
        @BindView(R.id.main_view)
        View main;

        public TopFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FriendGroupMap {
        FriendsBean friendsBean;
        String topKey;
        boolean isTop;
        int position;

        public FriendGroupMap() {
        }

        public FriendGroupMap(FriendsBean friendsBean, String topKey, boolean isTop) {
            this.friendsBean = friendsBean;
            this.topKey = topKey;
            this.isTop = isTop;
        }

        public FriendsBean getFriendsBean() {
            return friendsBean;
        }

        public void setFriendsBean(FriendsBean friendsBean) {
            this.friendsBean = friendsBean;
        }

        public String getTopKey() {
            return topKey;
        }

        public void setTopKey(String topKey) {
            this.topKey = topKey;
        }

        public boolean isTop() {
            return isTop;
        }

        public void setTop(boolean top) {
            isTop = top;
        }
    }
}
