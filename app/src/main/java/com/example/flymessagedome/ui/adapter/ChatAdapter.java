package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.utils.TimeUtil;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    ArrayList<Chat> chats;
    Context context;

    public ChatAdapter(ArrayList<Chat> chats, Context context) {
        this.chats = chats;
        this.context = context;
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = chats.get(position);
        ChatViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.home_message_item, parent, false);
            holder = new ChatViewHolder();
            holder.headImg = convertView.findViewById(R.id.img_head);
            holder.tvContent = convertView.findViewById(R.id.tv_msg);
            holder.tvCount = convertView.findViewById(R.id.tv_msg_count);
            holder.tvSender = convertView.findViewById(R.id.tv_msg_sender);
            holder.tvTime = convertView.findViewById(R.id.tv_msg_time);
            holder.chatView = convertView.findViewById(R.id.chat_item);
            holder.tvMsgState = convertView.findViewById(R.id.tv_msg_state);
            convertView.setTag(holder);
        } else {
            holder = (ChatViewHolder) convertView.getTag();
        }
        if (chat.getChat_show_remind()) {
            holder.tvCount.setBackground(context.getResources().getDrawable(R.drawable.home_msg_impt_count_shape));
        } else {
            holder.tvCount.setBackground(context.getResources().getDrawable(R.drawable.home_msg_unimpt_count_shape));
        }
        if (chat.getChat_m_count() > 0) {
            holder.tvCount.setText(chat.getChat_m_count() + "");
        } else {
            holder.tvCount.setVisibility(View.INVISIBLE);
        }
        if (chat.getChat_up()) {
            holder.chatView.setBackground(context.getResources().getDrawable(R.drawable.up_chat_item_bg));
        } else {
            holder.chatView.setBackground(context.getResources().getDrawable(R.color.white));
        }
        if (chat.getInEntering()) {
            holder.tvMsgState.setText("[草稿]");
        } else if (chat.getLastSendFailed()) {
            holder.tvMsgState.setText("[发送失败]");
        } else {
            holder.tvMsgState.setText("");
        }
        holder.tvTime.setText(TimeUtil.QQFormatTime(chat.getTime().getTime()));
        holder.tvSender.setText(chat.getChat_name());
        holder.tvContent.setText(chat.getChat_content() + "");
        Glide.with(context).load(chat.getChat_head())
                .into(holder.headImg);
        return convertView;
    }

    private class ChatViewHolder {
        View chatView;
        ImageView headImg;
        TextView tvMsgState;
        TextView tvSender;
        TextView tvTime;
        TextView tvContent;
        TextView tvCount;
    }
}
