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
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.model.SearchUserModel;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContractAdapter extends RecyclerView.Adapter {
    private final ArrayList<SearchUserModel.ResultBean> resultBeans;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private final LayoutInflater mLayoutInflater;

    public ContractAdapter(ArrayList<SearchUserModel.ResultBean> resultBeans, Context context) {
        this.resultBeans = resultBeans;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        FlyMessageApplication.getProxy(context);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        listener = onRecyclerViewItemClickListener;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContractViewHolder(mLayoutInflater.inflate(R.layout.contract_item, null, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ContractViewHolder viewHolder = (ContractViewHolder) holder;
            SearchUserModel.ResultBean resultBean = resultBeans.get(position);
            if (listener != null) {
                viewHolder.add_btn.setOnClickListener(v -> listener.onItemClick(v, position));
                viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(v, position));
            }
            Glide.with(context)
                    .load(resultBean.getU_head_img())
                    .into(viewHolder.head);
            if (resultBean.isFriend()) {
                viewHolder.add_btn.setVisibility(View.GONE);
                viewHolder.add_text.setVisibility(View.VISIBLE);
            } else {
                viewHolder.add_btn.setVisibility(View.VISIBLE);
                viewHolder.add_text.setVisibility(View.GONE);
            }
            viewHolder.contract_name.setText("" + resultBean.getU_sign());
            viewHolder.fly_name.setText("飞讯:" + resultBean.getU_nick_name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return resultBeans.size();
    }

    @SuppressLint("NonConstantResourceId")
    static class ContractViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head_img)
        CircleImageView head;
        @BindView(R.id.contract_name)
        TextView contract_name;
        @BindView(R.id.fly_name)
        TextView fly_name;
        @BindView(R.id.add_btn)
        Button add_btn;
        @BindView(R.id.add_text)
        TextView add_text;

        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
