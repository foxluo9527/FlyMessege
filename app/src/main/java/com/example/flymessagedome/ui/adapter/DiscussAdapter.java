package com.example.flymessagedome.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.model.Post;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.utils.TimeUtil;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscussAdapter extends RecyclerView.Adapter {
    ArrayList<Post.PostBean.CommentsBean> comments;
    OnCommentClickListener listener;
    Context context;
    HttpProxyCacheServer proxy;

    public DiscussAdapter(ArrayList<Post.PostBean.CommentsBean> comments, Context context) {
        this.comments = comments;
        this.context = context;
        proxy = FlyMessageApplication.getProxy(context);
    }

    public void setListener(OnCommentClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_post_discuss_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Post.PostBean.CommentsBean comment = comments.get(position);
        viewHolder.uName.setText(comment.getSend_u_nick_name());
        viewHolder.content.setText(comment.getCommunity_post_comment_content());
        viewHolder.time.setText(TimeUtil.QQFormatTime(comment.getCreate_time()));
        viewHolder.zanNum.setText(comment.getZan_num() + "");
        viewHolder.zanNum.setVisibility(comment.getZan_num() > 0 ? View.VISIBLE : View.INVISIBLE);
        viewHolder.zanState.setImageDrawable(context.getResources().getDrawable(comment.getZan_state() == 0 ? R.drawable.zan : R.drawable.zan_sel));
        Glide.with(context).load(proxy.getProxyUrl(comment.getSend_u_head())).into(viewHolder.uHead);
        DiscussReplyAdapter adapter
                = new DiscussReplyAdapter((ArrayList<Post.PostBean.CommentsBean.RepliesBean>) comment.getReplies(), position);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        viewHolder.replies_list.setLayoutManager(layoutManager);
        viewHolder.replies_list.setAdapter(adapter);
        viewHolder.zanState.setOnClickListener(v -> listener.clickZan(position));
        viewHolder.uHead.setOnClickListener(v -> listener.clickHead(position));
        viewHolder.itemView.setOnClickListener(v -> listener.clickComment(position));
        viewHolder.zanState.setOnClickListener(v -> listener.clickZan(position));
        viewHolder.itemView.setOnLongClickListener(v -> {
            if (comment.getSend_u_id() == LoginActivity.loginUser.getU_id()) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                listener.longPressComment(viewHolder.content, position);
                return true;
            } else
                return false;
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @SuppressLint("NonConstantResourceId")
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head)
        CircleImageView uHead;
        @BindView(R.id.name)
        TextView uName;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.zan_num)
        TextView zanNum;
        @BindView(R.id.zan_state)
        ImageView zanState;
        @BindView(R.id.replies_list)
        RecyclerView replies_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class DiscussReplyAdapter extends RecyclerView.Adapter {
        ArrayList<Post.PostBean.CommentsBean.RepliesBean> replies;
        int index;

        public DiscussReplyAdapter(ArrayList<Post.PostBean.CommentsBean.RepliesBean> replies, int index) {
            this.replies = replies;
            this.index = index;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_post_discuss_reply_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            Post.PostBean.CommentsBean.RepliesBean reply = replies.get(position);
            viewHolder.uName.setText(reply.getSend_u_nick_name());
            viewHolder.replier.setText(reply.getReply_u_name());
            viewHolder.content.setText(reply.getReply_content());
            viewHolder.time.setText(TimeUtil.QQFormatTime(reply.getCreate_time()));
            Glide.with(context).load(proxy.getProxyUrl(reply.getSend_u_head())).into(viewHolder.uHead);
            viewHolder.uHead.setOnClickListener(v -> listener.clickReplyHead(position, index));
            viewHolder.itemView.setOnClickListener(v -> listener.clickReply(position, index));

            viewHolder.itemView.setOnLongClickListener(v -> {
                if (reply.getSend_u_id() == LoginActivity.loginUser.getU_id()) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    listener.longPressReply(viewHolder.content, position, index);
                    return true;
                } else
                    return false;
            });

        }

        @Override
        public int getItemCount() {
            return replies.size();
        }

        @SuppressLint("NonConstantResourceId")
        public class ItemViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.head)
            CircleImageView uHead;
            @BindView(R.id.name)
            TextView uName;
            @BindView(R.id.time)
            TextView time;
            @BindView(R.id.content)
            TextView content;
            @BindView(R.id.replier_name)
            TextView replier;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public interface OnCommentClickListener {
        /**
         * 点赞
         */
        void clickZan(int position);

        /**
         * 点击评论人
         */
        void clickHead(int position);

        /**
         * 点击评论回复人
         */
        void clickReplyHead(int replyIndex, int position);

        /**
         * 点击评论
         *
         * @param position 评论的位置
         */
        void clickComment(int position);

        /**
         * 点击回复
         *
         * @param replyIndex 回复的位置
         * @param position   评论的位置
         */
        void clickReply(int replyIndex, int position);

        void longPressComment(View v, int position);

        void longPressReply(View v, int replyIndex, int position);
    }
}
