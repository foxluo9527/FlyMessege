package com.example.flymessagedome.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
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
import com.example.flymessagedome.model.PostListResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;

import static com.example.flymessagedome.utils.TimeUtil.QQFormatTime;

public class PostListAdapter extends RecyclerView.Adapter {
    ArrayList<PostListResult.PostsBean> postsBeans;
    OnPostClickListener listener;
    Context context;
    private final HttpProxyCacheServer proxyCacheServer;

    public PostListAdapter(ArrayList<PostListResult.PostsBean> postsBeans, Context context) {
        this.postsBeans = postsBeans;
        this.context = context;
        proxyCacheServer = FlyMessageApplication.getProxy(context);
    }

    public void setListener(OnPostClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_community_list, parent, false));
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            PostListResult.PostsBean post = postsBeans.get(position);
            Glide.with(context).load(proxyCacheServer.getProxyUrl(post.getU_head())).into(viewHolder.uHead);
            viewHolder.uName.setText(post.getU_nick_name());
            viewHolder.time.setText(QQFormatTime(post.getCreateTime()));
            viewHolder.content.setText(post.getCommunityPostContent());
            viewHolder.content.setVisibility(TextUtils.isEmpty(post.getCommunityPostContent()) ? View.GONE : View.VISIBLE);
            viewHolder.discussNum.setText("" + post.getCommentCount());
            viewHolder.zanNum.setText("" + post.getZanCount());
            viewHolder.sawNum.setText("" + post.getShowCount());
            viewHolder.zanState.setImageDrawable(post.getZan_state() == 0 ? context.getResources().getDrawable(R.drawable.zan) : context.getResources().getDrawable(R.drawable.zan_sel));
            ArrayList<String> items = new ArrayList<>();
            for (PostListResult.PostsBean.PostItemsBean postItem : post.getPostItems()) {
                items.add(proxyCacheServer.getProxyUrl(postItem.getCommunity_post_item_url()));
            }
            viewHolder.items.setData(items);
            viewHolder.items.setDelegate(new BGANinePhotoLayout.Delegate() {
                @Override
                public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int index, String model, List<String> models) {
                    listener.clickItemPosition(ninePhotoLayout.getData(), index, position);
                }

                @Override
                public void onClickExpand(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {

                }
            });
            viewHolder.zanView.setOnClickListener(v -> listener.clickZan(position));
            viewHolder.itemView.setOnClickListener(v -> listener.clickPost(position));
            viewHolder.uHead.setOnClickListener(v -> listener.clickHead(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return postsBeans.size();
    }

    @SuppressLint("NonConstantResourceId")
    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head)
        ImageView uHead;
        @BindView(R.id.name)
        TextView uName;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.saw_num)
        TextView sawNum;
        @BindView(R.id.discuss_num)
        TextView discussNum;
        @BindView(R.id.zan_num)
        TextView zanNum;
        @BindView(R.id.zan_state)
        ImageView zanState;
        @BindView(R.id.content_pics)
        BGANinePhotoLayout items;
        @BindView(R.id.zan_view)
        View zanView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPostClickListener {
        /**
         * 点赞
         */
        void clickZan(int position);

        /**
         * 点击帖子
         */
        void clickPost(int position);

        /**
         * 点击发帖人
         */
        void clickHead(int position);

        /**
         * 点击图片
         *
         * @param itemPosition 图片位置
         */
        void clickItemPosition(ArrayList<String> datas, int itemPosition, int position);
    }
}
