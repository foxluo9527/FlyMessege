package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.utils.TextSpanUtil;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchUserAdapter extends RecyclerView.Adapter {
    ArrayList<SearchUserModel.ResultBean> userBeans;
    private LayoutInflater mLayoutInflater;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private HttpProxyCacheServer proxyCacheServer;
    String searchString="";
    public SearchUserAdapter(ArrayList<SearchUserModel.ResultBean> userBeans, Context context, OnRecyclerViewItemClickListener listener) {
        this.userBeans = userBeans;
        this.mLayoutInflater=LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
        proxyCacheServer= FlyMessageApplication.getProxy(context);
    }
    public void setKeyString(String searchString){
        this.searchString=searchString;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(mLayoutInflater.inflate(R.layout.search_result_item, parent, false));
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchViewHolder viewHolder=(SearchViewHolder)holder;
        SearchUserModel.ResultBean userBean=userBeans.get(position);
        Glide.with(context).load(proxyCacheServer.getProxyUrl(userBean.getU_head_img()))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(viewHolder.headImg);
        viewHolder.name.setText(TextSpanUtil.getInstant().setColor(userBean.getU_nick_name()+"("+userBean.getU_name()+")",
                searchString,context.getApplicationContext().getColor(R.color.blue_1)));
        if (TextUtils.isEmpty(userBean.getU_sex())){
            viewHolder.sex.setVisibility(View.GONE);
        }else if (userBean.getU_sex().equals("男")){
            viewHolder.sex.setImageDrawable(AppCompatResources.getDrawable(context,R.mipmap.man));
        }else {
            viewHolder.sex.setImageDrawable(AppCompatResources.getDrawable(context,R.mipmap.women));
        }
        viewHolder.position.setText(""+userBean.getU_position());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userBeans.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder{
        @Nullable
        @BindView(R.id.user_head_img)
        CircleImageView headImg;
        @BindView(R.id.position)
        TextView position;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.sex)
        ImageView sex;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
