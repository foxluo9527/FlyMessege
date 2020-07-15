package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.ui.activity.UserChatActivity;
import java.util.ArrayList;

public class ChatPhotoViewAdapter extends RecyclerView.Adapter<ChatPhotoViewAdapter.LrcMatchHolder> {
    public static ArrayList<String> photos=new ArrayList<>();
    boolean[] choice;
    Context context;

    HttpProxyCacheServer proxyCacheServer;
    public ChatPhotoViewAdapter(ArrayList<String> photos, Context context,boolean[] choice) {
        ChatPhotoViewAdapter.photos.clear();
        this.photos .addAll(photos) ;
        this.context = context;
        this.choice=choice;
        proxyCacheServer= FlyMessageApplication.getProxy(context);
    }

    @NonNull
    @Override
    public ChatPhotoViewAdapter.LrcMatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_send_pic_item, parent, false);
        return new LrcMatchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatPhotoViewAdapter.LrcMatchHolder holder, int position) {
        if (choice.length>0&&choice[position]){
            holder.radioChoice.setChecked(true);
        }else {
            holder.radioChoice.setChecked(false);
        }
        String photo=photos.get(position);
        if (photo==null){
            return;
        }
        Glide.with(context)
                .load(photo)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.photoView);
        if(mOnRecyclerViewItemClickListener!=null){

            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnRecyclerViewItemClickListener.onItemClick(v, position);
                }
            });
            holder.radioChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRecyclerViewItemClickListener.onItemClick(v, position);
                }
            });
        }
    }
    public class LrcMatchHolder extends RecyclerView.ViewHolder {
        View mainView;
        RadioButton radioChoice;
        ImageView photoView;
        public LrcMatchHolder(@NonNull View itemView) {
            super(itemView);
            radioChoice=itemView.findViewById(R.id.radio_choice);
            photoView=itemView.findViewById(R.id.img_photo);
            mainView=itemView.findViewById(R.id.main_view);
        }
    }
    @Override
    public int getItemCount() {
        return photos.size();
    }

    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        mOnRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
