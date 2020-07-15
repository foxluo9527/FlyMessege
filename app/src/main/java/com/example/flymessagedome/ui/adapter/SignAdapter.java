package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.model.UserSignModel;
import com.example.flymessagedome.ui.ImageViewCheckBox;
import com.example.flymessagedome.ui.activity.ShowHistorySignActivity;
import com.example.flymessagedome.view.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignAdapter extends RecyclerView.Adapter {
    private ArrayList<UserSignModel.SignBean> signBeans;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private LayoutInflater mLayoutInflater;
    private HttpProxyCacheServer proxyCacheServer;
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SignAdapter(ArrayList<UserSignModel.SignBean> signBeans, Context context) {
        this.signBeans = signBeans;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        proxyCacheServer= FlyMessageApplication.getProxy(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SignViewHolder(mLayoutInflater.inflate(R.layout.sign_list_item, null, false));
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        listener=onRecyclerViewItemClickListener;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SignViewHolder)holder).sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
        ((SignViewHolder)holder).delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
        ((SignViewHolder)holder).choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
        UserSignModel.SignBean signBean=signBeans.get(position);
        if (ShowHistorySignActivity.onEditSigns){
            if (ShowHistorySignActivity.choices!=null&&ShowHistorySignActivity.choices.length>position){
                ((SignViewHolder)holder).choice.setVisibility(View.VISIBLE);
                ((SignViewHolder)holder).sendBtn.setVisibility(View.GONE);
                ((SignViewHolder)holder).delBtn.setVisibility(View.GONE);
                if (ShowHistorySignActivity.choices[position]){
                    ((SignViewHolder)holder).choice.setChecked(true);
                }else {
                    ((SignViewHolder)holder).choice.setChecked(false);
                }
            }else {
                ((SignViewHolder)holder).choice.setVisibility(View.GONE);
                ((SignViewHolder)holder).sendBtn.setVisibility(View.VISIBLE);
                ((SignViewHolder)holder).delBtn.setVisibility(View.VISIBLE);
            }
        }else {
            ((SignViewHolder)holder).choice.setVisibility(View.GONE);
            ((SignViewHolder)holder).sendBtn.setVisibility(View.VISIBLE);
            ((SignViewHolder)holder).delBtn.setVisibility(View.VISIBLE);
        }
        ((SignViewHolder)holder).time.setText(format.format(signBean.getTime()));
        ((SignViewHolder)holder).content.setText(signBean.getS_content());
    }

    @Override
    public int getItemCount() {
        return signBeans.size();
    }
    static class SignViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.sign_choice)
        ImageViewCheckBox choice;
        @BindView(R.id.sign_time)
        TextView time;
        @BindView(R.id.sign_content)
        TextView content;
        @BindView(R.id.send_btn)
        Button sendBtn;
        @BindView(R.id.del_btn)
        Button delBtn;
        public SignViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
