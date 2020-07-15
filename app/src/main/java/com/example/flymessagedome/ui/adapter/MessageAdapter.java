package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.FriendRequestDao;
import com.example.flymessagedome.bean.GroupBeanDao;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.GroupMemberDao;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.User;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.bean.UserDao;
import com.example.flymessagedome.ui.ImageViewCheckBox;
import com.example.flymessagedome.ui.activity.GroupChatActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.UserChatActivity;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.StringUtil;
import com.example.flymessagedome.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wseemann.media.FFmpegMediaMetadataRetriever;

import static com.example.flymessagedome.utils.ImageUtils.getRoundedCornerBitmap;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Message> mDatas;
    private ArrayList<photoMap> photoMaps;
    private boolean showChoice=false;
    private boolean[] choices;
    private static final int MSG_TYPE_MY_USER_NORMAL=1;
    private static final int MSG_TYPE_OTHER_USER_NORMAL=2;
    private static final int MSG_TYPE_MY_USER_FILE=3;
    private static final int MSG_TYPE_OTHER_USER_FILE=4;
    private static final int MSG_TYPE_MY_GROUP_NORMAL=5;
    private static final int MSG_TYPE_OTHER_GROUP_NORMAL=6;
    private static final int MSG_TYPE_MY_GROUP_FILE=7;
    private static final int MSG_TYPE_OTHER_GROUP_FILE=8;
    private static final int MSG_TYPE_MY_USER_PIC=9;
    private static final int MSG_TYPE_OTHER_PIC=10;
    private static final int MSG_TYPE_MY_USER_VOICE=11;
    private static final int MSG_TYPE_OTHER_USER_VOICE=12;
    private static final int MSG_TYPE_MY_GROUP_PIC=13;
    private static final int MSG_TYPE_OTHER_GROUP_PIC=14;
    private static final int MSG_TYPE_MY_GROUP_VOICE=15;
    private static final int MSG_TYPE_OTHER_GROUP_VOICE=16;
    private HttpProxyCacheServer proxyCacheServer;
    UserDao userDao=FlyMessageApplication.getInstances().getDaoSession().getUserDao();
    UserBeanDao userBeanDao= FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();
    GroupMemberDao groupMemberDao=FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao();

    User me;

    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    int width;
    int height;
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        mOnRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
        void onItemMenuClick(View view,int position,int itemId);
    }

    public MessageAdapter(Context context, List<Message> datas,ArrayList<MessageAdapter.photoMap> photoMaps) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatas = datas;
        this.photoMaps=photoMaps;
        proxyCacheServer=FlyMessageApplication.getProxy(mContext);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        me=LoginActivity.loginUser;
        choices=new boolean[mDatas.size()];
    }

    //添加消息显示在RecyclerView中
    public void addItem(Message msg) {
        mDatas.add(msg);
        notifyDataSetChanged();
    }
    public void showChoice(){
        showChoice=true;
        choices=new boolean[mDatas.size()];
        notifyDataSetChanged();
    }
    public void hideChoice(){
        showChoice=false;
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getChoiceIndex(){
        ArrayList<Integer> index=new ArrayList<>();
        for(int i=0;i< choices.length;i++){
            if (choices[i]){
                index.add(i);
            }
        }
        return index;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        switch (viewType){
            case MSG_TYPE_MY_USER_NORMAL:
                view = mLayoutInflater.inflate(R.layout.user_normal_message_mine, parent, false);
                return new UserChatRightNormalViewHolder(view);
            case MSG_TYPE_OTHER_USER_NORMAL:
                view = mLayoutInflater.inflate(R.layout.user_normal_message_other, parent, false);
                return new UserChatLeftNormalViewHolder(view);
            case MSG_TYPE_MY_USER_FILE:
                view = mLayoutInflater.inflate(R.layout.user_file_message_mine, parent, false);
                return new UserChatRightFileViewHolder(view);
            case MSG_TYPE_OTHER_USER_FILE:
                view = mLayoutInflater.inflate(R.layout.user_file_message_other, parent, false);
                return new UserChatLeftFileViewHolder(view);
            case MSG_TYPE_MY_GROUP_NORMAL:
                view = mLayoutInflater.inflate(R.layout.group_normal_message_mine, parent, false);
                return new GroupChatRightNormalViewHolder(view);
            case MSG_TYPE_OTHER_GROUP_NORMAL:
                view = mLayoutInflater.inflate(R.layout.group_normal_message_other, parent, false);
                return new GroupChatLeftNormalViewHolder(view);
            case MSG_TYPE_MY_GROUP_FILE:
                view = mLayoutInflater.inflate(R.layout.group_file_message_mine, parent, false);
                return new GroupChatRightFileViewHolder(view);
            case MSG_TYPE_OTHER_GROUP_FILE:
                view = mLayoutInflater.inflate(R.layout.group_file_message_other, parent, false);
                return new GroupChatLeftFileViewHolder(view);
            case MSG_TYPE_MY_USER_PIC:
                view = mLayoutInflater.inflate(R.layout.user_pic_message_mine, parent, false);
                return new UserChatRightPivViewHolder(view);
            case MSG_TYPE_OTHER_PIC:
                view = mLayoutInflater.inflate(R.layout.user_pic_message_other, parent, false);
                return new UserChatLeftPicViewHolder(view);
            case MSG_TYPE_MY_USER_VOICE:
                view = mLayoutInflater.inflate(R.layout.user_voice_message_mine, parent, false);
                return new UserChatRightVoiceViewHolder(view);
            case MSG_TYPE_OTHER_USER_VOICE:
                view = mLayoutInflater.inflate(R.layout.user_voice_message_other, parent, false);
                return new UserChatLeftVoiceViewHolder(view);
            case MSG_TYPE_MY_GROUP_PIC:
                view = mLayoutInflater.inflate(R.layout.group_pic_message_mine, parent, false);
                return new GroupChatRightPicViewHolder(view);
            case MSG_TYPE_OTHER_GROUP_PIC:
                view = mLayoutInflater.inflate(R.layout.group_pic_message_other, parent, false);
                return new GroupChatLeftPicViewHolder(view);
            case MSG_TYPE_MY_GROUP_VOICE:
                view = mLayoutInflater.inflate(R.layout.group_voice_message_mine, parent, false);
                return new GroupChatRightVoiceViewHolder(view);
            case MSG_TYPE_OTHER_GROUP_VOICE:
                view = mLayoutInflater.inflate(R.layout.group_voice_message_other, parent, false);
                return new GroupChatLeftVoiceViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message msg = mDatas.get(holder.getAdapterPosition());
        Message lastMsg=null;
        UserBean other=userBeanDao.load((long) msg.getM_source_id());
        if (position!=0)
            lastMsg=mDatas.get(position-1);
        if (msg.getM_type()==0){
            if(holder instanceof UserChatLeftNormalViewHolder) {
                if (showChoice){
                    ((UserChatLeftNormalViewHolder) holder).choice.setVisibility(View.VISIBLE);
                    if (choices.length>position)
                        ((UserChatLeftNormalViewHolder) holder).choice.setChecked(choices[position]);
                    ((UserChatLeftNormalViewHolder) holder).choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                        @Override
                        public void onCheckStateChanged(boolean isChecked) {
                            choices[position]=isChecked;
                        }
                    });
                }else {
                    ((UserChatLeftNormalViewHolder) holder).choice.setVisibility(View.GONE);
                }
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((UserChatLeftNormalViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((UserChatLeftNormalViewHolder) holder).tv_send_time.setText("");
                }
                Glide.with(mContext)
                        .load(other.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((UserChatLeftNormalViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatLeftNormalViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                ((UserChatLeftNormalViewHolder)holder).msg_content.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        //这里可以添加自己的菜单选项（前提是要返回true的）
                        menu.add(1, 2, 0, "删除");
                        return true;//返回false 就是屏蔽ActionMode菜单
                    }
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        mOnRecyclerViewItemClickListener.onItemMenuClick(((UserChatLeftNormalViewHolder)holder).msg_content,position,item.getItemId());
                        return false;
                    }
                });
                ((UserChatLeftNormalViewHolder)holder).msg_content.setText(StringUtil.formatFileMessage(msg.getM_content()));
            }
            else if(holder instanceof UserChatRightNormalViewHolder) {
                if (showChoice){
                    ((UserChatRightNormalViewHolder) holder).choice.setVisibility(View.VISIBLE);
                    if (choices.length>position)
                        ((UserChatRightNormalViewHolder) holder).choice.setChecked(choices[position]);
                    ((UserChatRightNormalViewHolder) holder).choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                        @Override
                        public void onCheckStateChanged(boolean isChecked) {
                            choices[position]=isChecked;
                        }
                    });
                }else {
                    ((UserChatRightNormalViewHolder) holder).choice.setVisibility(View.GONE);
                }
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((UserChatRightNormalViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((UserChatRightNormalViewHolder) holder).tv_send_time.setText("");
                }
                if (msg.getIsSend()){
                    ((UserChatRightNormalViewHolder) holder).sendFailed.setVisibility(View.GONE);
                }else {
                    ((UserChatRightNormalViewHolder) holder).sendFailed.setVisibility(View.VISIBLE);
                    ((UserChatRightNormalViewHolder) holder).sendFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRecyclerViewItemClickListener!=null)
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                Glide.with(mContext)
                        .load(me.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((UserChatRightNormalViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatRightNormalViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                ((UserChatRightNormalViewHolder)holder).msg_content.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        //这里可以添加自己的菜单选项（前提是要返回true的）
                        menu.add(1, 2, 0, "删除");
                        return true;//返回false 就是屏蔽ActionMode菜单
                    }
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        mOnRecyclerViewItemClickListener.onItemMenuClick(((UserChatRightNormalViewHolder)holder).msg_content,position,item.getItemId());
                        return false;
                    }
                });
                ((UserChatRightNormalViewHolder)holder).msg_content.setText(StringUtil.formatFileMessage(msg.getM_content()));
            }
            else if (holder instanceof UserChatLeftFileViewHolder){
                if (showChoice){
                    ((UserChatLeftFileViewHolder) holder).choice.setVisibility(View.VISIBLE);
                    ((UserChatLeftFileViewHolder) holder).choice.setChecked(choices[position]);
                    ((UserChatLeftFileViewHolder) holder).choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                        @Override
                        public void onCheckStateChanged(boolean isChecked) {
                            choices[position]=isChecked;
                        }
                    });
                }else {
                    ((UserChatLeftFileViewHolder) holder).choice.setVisibility(View.GONE);
                }
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((UserChatLeftFileViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((UserChatLeftFileViewHolder) holder).tv_send_time.setText("");
                }
                Glide.with(mContext)
                        .load(other.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((UserChatLeftFileViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatLeftFileViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ((UserChatLeftFileViewHolder) holder).tv_file_name.setText(""+msgFile.getFilename());
                    if (msgFile.getLink().contains("http")){
                        if (msg.getDownloadState()==0)
                            ((UserChatLeftFileViewHolder) holder).tv_file_msg.setText("在线文件");
                        else if (msg.getDownloadState()==1){
                            ((UserChatLeftFileViewHolder) holder).tv_file_msg.setText("正在下载");
                        }else if (msg.getDownloadState()==2){
                            ((UserChatLeftFileViewHolder) holder).tv_file_msg.setText("下载失败");
                        }else if (msg.getDownloadState()==3){
                            ((UserChatLeftFileViewHolder) holder).tv_file_msg.setText("下载暂停");
                        }else if (msg.getDownloadState()==4){
                            ((UserChatLeftFileViewHolder) holder).tv_file_msg.setText("已下载");
                        }
                    }else {
                        ((UserChatLeftFileViewHolder) holder).tv_file_msg.setText("已下载");
                    }
                    int fileHeadResource=-1;
                    if (msgFile.getFileType().equals("word")){
                        fileHeadResource=R.drawable.word;
                    }else if (msgFile.getFileType().equals("ppt")){
                        fileHeadResource=R.drawable.ppt;
                    }else if (msgFile.getFileType().equals("excel")){
                        fileHeadResource=R.drawable.excel;
                    }else if (msgFile.getFileType().equals("pdf")){
                        fileHeadResource=R.drawable.pdf;
                    }else if (msgFile.getFileType().equals("music")){
                        fileHeadResource=R.drawable.music;
                    }else if (msgFile.getFileType().equals("video")){
                        fileHeadResource=R.drawable.video;
                    }else if (msgFile.getFileType().equals("file")){
                        fileHeadResource=R.drawable.unknow;
                    }
                    Glide.with(mContext)
                            .load(fileHeadResource)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(((UserChatLeftFileViewHolder) holder).file_type_img);
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((UserChatLeftFileViewHolder) holder).file_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((UserChatLeftFileViewHolder) holder).file_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                                menu.add(0, position, 1, "下载");
                                menu.add(0, position, 2, "直链分享");
                            }
                        });
                    }
                }else {
                    ((UserChatLeftFileViewHolder) holder).tv_file_msg.setVisibility(View.GONE);
                    ((UserChatLeftFileViewHolder) holder).tv_file_name.setText("获取文件失败");
                    Glide.with(mContext)
                            .load(R.mipmap.ic_launcher_round)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(((UserChatLeftFileViewHolder) holder).file_type_img);
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((UserChatRightFileViewHolder) holder).file_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((UserChatRightFileViewHolder) holder).file_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof UserChatRightFileViewHolder){
                if (showChoice){
                    ((UserChatRightFileViewHolder) holder).choice.setVisibility(View.VISIBLE);
                    ((UserChatRightFileViewHolder) holder).choice.setChecked(choices[position]);
                    ((UserChatRightFileViewHolder) holder).choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                        @Override
                        public void onCheckStateChanged(boolean isChecked) {
                            choices[position]=isChecked;
                        }
                    });
                }else {
                    ((UserChatRightFileViewHolder) holder).choice.setVisibility(View.GONE);
                }
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((UserChatRightFileViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((UserChatRightFileViewHolder) holder).tv_send_time.setText("");
                }
                if (msg.getIsSend()){
                    ((UserChatRightFileViewHolder) holder).sendFailed.setVisibility(View.GONE);
                }else {
                    ((UserChatRightFileViewHolder) holder).sendFailed.setVisibility(View.VISIBLE);
                    ((UserChatRightFileViewHolder) holder).sendFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRecyclerViewItemClickListener!=null)
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                Glide.with(mContext)
                        .load(me.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((UserChatRightFileViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatRightFileViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ((UserChatRightFileViewHolder) holder).tv_file_name.setText(""+msgFile.getFilename());
                    if (msg.getIsSend()){
                        ((UserChatRightFileViewHolder) holder).tv_file_msg.setText("发送成功");
                    }else {
                        ((UserChatRightFileViewHolder) holder).tv_file_msg.setText("发送失败");
                    }
                    int fileHeadResource=-1;
                    if (msgFile.getFileType().equals("word")){
                        fileHeadResource=R.drawable.word;
                    }else if (msgFile.getFileType().equals("ppt")){
                        fileHeadResource=R.drawable.ppt;
                    }else if (msgFile.getFileType().equals("excel")){
                        fileHeadResource=R.drawable.excel;
                    }else if (msgFile.getFileType().equals("pdf")){
                        fileHeadResource=R.drawable.pdf;
                    }else if (msgFile.getFileType().equals("music")){
                        fileHeadResource=R.drawable.music;
                    }else if (msgFile.getFileType().equals("video")){
                        fileHeadResource=R.drawable.video;
                    }else if (msgFile.getFileType().equals("file")){
                        fileHeadResource=R.drawable.unknow;
                    }
                    Glide.with(mContext)
                            .load(fileHeadResource)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(((UserChatRightFileViewHolder) holder).file_type_img);
                }
                else {
                    ((UserChatRightFileViewHolder) holder).tv_file_msg.setVisibility(View.GONE);
                    ((UserChatRightFileViewHolder) holder).tv_file_name.setText("获取文件失败");
                    Glide.with(mContext)
                            .load(R.mipmap.ic_launcher_round)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(((UserChatRightFileViewHolder) holder).file_type_img);

                }
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatRightFileViewHolder) holder).file_msg_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                    ((UserChatRightFileViewHolder) holder).file_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            menu.add(0, position, 0, "删除");
                        }
                    });
                }
            }
            else if (holder instanceof UserChatLeftPicViewHolder){
                if (showChoice){
                    ((UserChatLeftPicViewHolder) holder).choice.setVisibility(View.VISIBLE);
                    ((UserChatLeftPicViewHolder) holder).choice.setChecked(choices[position]);
                    ((UserChatLeftPicViewHolder) holder).choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                        @Override
                        public void onCheckStateChanged(boolean isChecked) {
                            choices[position]=isChecked;
                        }
                    });
                }else {
                    ((UserChatLeftPicViewHolder) holder).choice.setVisibility(View.GONE);
                }
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((UserChatLeftPicViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((UserChatLeftPicViewHolder) holder).tv_send_time.setText("");
                }
                Glide.with(mContext)
                        .load(other.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((UserChatLeftPicViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatLeftPicViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ViewGroup.LayoutParams params = ((UserChatLeftPicViewHolder) holder).msg_pic.getLayoutParams();
                    params.width= width;
                    ((UserChatLeftPicViewHolder) holder).msg_pic.setLayoutParams(params);
                    ((UserChatLeftPicViewHolder) holder).msg_pic.setVisibility(View.VISIBLE);
                    String link=null;
                    if (msgFile.getLink().contains("http"))
                        link=proxyCacheServer.getProxyUrl(msgFile.getLink());
                    else
                        link=msgFile.getLink();
                    if (!photoMaps.contains(new photoMap(position,link)))
                        photoMaps.add(new photoMap(position,link));
                    Glide.with(mContext)
                            .asBitmap()
                            .load(link)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    resource=getRoundedCornerBitmap(resource,30f);
                                    ((UserChatLeftPicViewHolder) holder).msg_pic.setImageBitmap(resource);
                                }
                            });
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((UserChatLeftPicViewHolder) holder).msg_pic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((UserChatLeftPicViewHolder) holder).msg_pic.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof UserChatRightPivViewHolder){
                if (showChoice){
                    ((UserChatRightPivViewHolder) holder).choice.setVisibility(View.VISIBLE);
                    ((UserChatRightPivViewHolder) holder).choice.setChecked(choices[position]);
                    ((UserChatRightPivViewHolder) holder).choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                        @Override
                        public void onCheckStateChanged(boolean isChecked) {
                            choices[position]=isChecked;
                        }
                    });
                }else {
                    ((UserChatRightPivViewHolder) holder).choice.setVisibility(View.GONE);
                }
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((UserChatRightPivViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((UserChatRightPivViewHolder) holder).tv_send_time.setText("");
                }
                if (msg.getIsSend()){
                    ((UserChatRightPivViewHolder) holder).sendFailed.setVisibility(View.GONE);
                }else {
                    ((UserChatRightPivViewHolder) holder).sendFailed.setVisibility(View.VISIBLE);
                    ((UserChatRightPivViewHolder) holder).sendFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRecyclerViewItemClickListener!=null)
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                Glide.with(mContext)
                        .load(me.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((UserChatRightPivViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatRightPivViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    String link=null;
                    if (msgFile.getLink().contains("http"))
                        link=proxyCacheServer.getProxyUrl(msgFile.getLink());
                    else
                        link=msgFile.getLink();
                    if (!photoMaps.contains(new photoMap(position,link)))
                        photoMaps.add(new photoMap(position,link));
                    Glide.with(mContext)
                            .asBitmap()
                            .load(link)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    resource=getRoundedCornerBitmap(resource,30f);
                                    ((UserChatRightPivViewHolder) holder).msg_pic.setImageBitmap(resource);
                                }
                            });
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((UserChatRightPivViewHolder) holder).msg_pic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((UserChatRightPivViewHolder) holder).msg_pic.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof UserChatLeftVoiceViewHolder){
                if (showChoice){
                    ((UserChatLeftVoiceViewHolder) holder).choice.setVisibility(View.VISIBLE);
                    ((UserChatLeftVoiceViewHolder) holder).choice.setChecked(choices[position]);
                    ((UserChatLeftVoiceViewHolder) holder).choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                        @Override
                        public void onCheckStateChanged(boolean isChecked) {
                            choices[position]=isChecked;
                        }
                    });
                }else {
                    ((UserChatLeftVoiceViewHolder) holder).choice.setVisibility(View.GONE);
                }
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((UserChatLeftVoiceViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((UserChatLeftVoiceViewHolder) holder).tv_send_time.setText("");
                }
                Glide.with(mContext)
                        .load(other.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((UserChatLeftVoiceViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatLeftVoiceViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ((UserChatLeftVoiceViewHolder) holder).voice_msg_view.setVisibility(View.VISIBLE);
                    ((UserChatLeftVoiceViewHolder) holder).voice_play_state.setText(""+getVoiceLength(msgFile.getLink()));
                    if (UserChatActivity.viocePlay.length>position&&UserChatActivity.viocePlay[position]){
                        ((UserChatLeftVoiceViewHolder) holder).voice_play_control.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));
                    }else {
                        ((UserChatLeftVoiceViewHolder) holder).voice_play_control.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play));
                    }
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((UserChatLeftVoiceViewHolder) holder).voice_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((UserChatLeftVoiceViewHolder) holder).voice_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof UserChatRightVoiceViewHolder){
                if (showChoice){
                    ((UserChatRightVoiceViewHolder) holder).choice.setVisibility(View.VISIBLE);
                    ((UserChatRightVoiceViewHolder) holder).choice.setChecked(choices[position]);
                    ((UserChatRightVoiceViewHolder) holder).choice.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
                        @Override
                        public void onCheckStateChanged(boolean isChecked) {
                            choices[position]=isChecked;
                        }
                    });
                }else {
                    ((UserChatRightVoiceViewHolder) holder).choice.setVisibility(View.GONE);
                }
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((UserChatRightVoiceViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((UserChatRightVoiceViewHolder) holder).tv_send_time.setText("");
                }
                if (msg.getIsSend()){
                    ((UserChatRightVoiceViewHolder) holder).sendFailed.setVisibility(View.GONE);
                }else {
                    ((UserChatRightVoiceViewHolder) holder).sendFailed.setVisibility(View.VISIBLE);
                    ((UserChatRightVoiceViewHolder) holder).sendFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRecyclerViewItemClickListener!=null)
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                Glide.with(mContext)
                        .load(me.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((UserChatRightVoiceViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((UserChatRightVoiceViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ((UserChatRightVoiceViewHolder) holder).voice_msg_view.setVisibility(View.VISIBLE);
                    ((UserChatRightVoiceViewHolder) holder).voice_play_state.setText(""+getVoiceLength(msgFile.getLink()));
                    if (UserChatActivity.viocePlay.length>position&&UserChatActivity.viocePlay[position]){
                        ((UserChatRightVoiceViewHolder) holder).voice_play_control.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));
                    }else {
                        ((UserChatRightVoiceViewHolder) holder).voice_play_control.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play));
                    }
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((UserChatRightVoiceViewHolder) holder).voice_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((UserChatRightVoiceViewHolder) holder).voice_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
        }
        else {
            List<GroupMember> members =groupMemberDao.queryBuilder()
                    .where(GroupMemberDao.Properties.U_id.eq(msg.getM_source_id()))
                    .where(GroupMemberDao.Properties.G_id.eq(msg.getM_source_g_id()))
                    .list();
            GroupMember groupMember=null;
            if (members.size()>0){
                groupMember=members.get(0);
            }
            if(holder instanceof GroupChatLeftNormalViewHolder) {
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((GroupChatLeftNormalViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((GroupChatLeftNormalViewHolder) holder).tv_send_time.setText("");
                }
                if (groupMember!=null)
                    ((GroupChatLeftNormalViewHolder) holder).tv_group_member_name.setText(groupMember.getG_nick_name()+"");
                else if (other!=null)
                    ((GroupChatLeftNormalViewHolder) holder).tv_group_member_name.setText(other.getU_nick_name()+"");
                else
                    ((GroupChatLeftNormalViewHolder) holder).tv_group_member_name.setText("UnKnown");
                Glide.with(mContext)
                        .load(other.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((GroupChatLeftNormalViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((GroupChatLeftNormalViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                ((GroupChatLeftNormalViewHolder)holder).msg_content.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        //这里可以添加自己的菜单选项（前提是要返回true的）
                        menu.add(1, 2, 0, "删除");
                        return true;//返回false 就是屏蔽ActionMode菜单
                    }
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        mOnRecyclerViewItemClickListener.onItemMenuClick(((GroupChatLeftNormalViewHolder)holder).msg_content,position,item.getItemId());
                        return false;
                    }
                });
                ((GroupChatLeftNormalViewHolder)holder).msg_content.setText(StringUtil.formatFileMessage(msg.getM_content()));
            }
            else if(holder instanceof GroupChatRightNormalViewHolder) {
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((GroupChatRightNormalViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((GroupChatRightNormalViewHolder) holder).tv_send_time.setText("");
                }
                if(groupMember!=null)
                    ((GroupChatRightNormalViewHolder) holder).tv_group_member_name.setText(groupMember.getG_nick_name()+"");
                else
                    ((GroupChatRightNormalViewHolder) holder).tv_group_member_name.setText(me.getU_nick_name()+"");
                if (msg.getIsSend()){
                    ((GroupChatRightNormalViewHolder) holder).sendFailed.setVisibility(View.GONE);
                }else {
                    ((GroupChatRightNormalViewHolder) holder).sendFailed.setVisibility(View.VISIBLE);
                    ((GroupChatRightNormalViewHolder) holder).sendFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRecyclerViewItemClickListener!=null)
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                Glide.with(mContext)
                        .load(me.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((GroupChatRightNormalViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((GroupChatRightNormalViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                ((GroupChatRightNormalViewHolder)holder).msg_content.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        //这里可以添加自己的菜单选项（前提是要返回true的）
                        menu.add(1, 2, 0, "删除");
                        return true;//返回false 就是屏蔽ActionMode菜单
                    }
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        mOnRecyclerViewItemClickListener.onItemMenuClick(((GroupChatRightNormalViewHolder)holder).msg_content,position,item.getItemId());
                        return false;
                    }
                });
                ((GroupChatRightNormalViewHolder)holder).msg_content.setText(StringUtil.formatFileMessage(msg.getM_content()));
            }
            else if (holder instanceof GroupChatLeftFileViewHolder){
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((GroupChatLeftFileViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((GroupChatLeftFileViewHolder) holder).tv_send_time.setText("");
                }
                if (groupMember!=null)
                    ((GroupChatLeftFileViewHolder) holder).tv_group_member_name.setText(groupMember.getG_nick_name()+"");
                else if (other!=null)
                    ((GroupChatLeftFileViewHolder) holder).tv_group_member_name.setText(other.getU_nick_name()+"");
                else
                    ((GroupChatLeftFileViewHolder) holder).tv_group_member_name.setText("UnKnown");
                Glide.with(mContext)
                        .load(other.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((GroupChatLeftFileViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((GroupChatLeftFileViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ((GroupChatLeftFileViewHolder) holder).tv_file_name.setText(""+msgFile.getFilename());
                    if (msgFile.getLink().contains("http")){
                        if (msg.getDownloadState()==0)
                            ((GroupChatLeftFileViewHolder) holder).tv_file_msg.setText("在线文件");
                        else if (msg.getDownloadState()==1){
                            ((GroupChatLeftFileViewHolder) holder).tv_file_msg.setText("正在下载");
                        }else if (msg.getDownloadState()==2){
                            ((GroupChatLeftFileViewHolder) holder).tv_file_msg.setText("下载失败");
                        }else if (msg.getDownloadState()==3){
                            ((GroupChatLeftFileViewHolder) holder).tv_file_msg.setText("下载暂停");
                        }else if (msg.getDownloadState()==4){
                            ((GroupChatLeftFileViewHolder) holder).tv_file_msg.setText("已下载");
                        }
                    }else {
                        ((GroupChatLeftFileViewHolder) holder).tv_file_msg.setText("已下载");
                    }
                    int fileHeadResource=-1;
                    if (msgFile.getFileType().equals("word")){
                        fileHeadResource=R.drawable.word;
                    }else if (msgFile.getFileType().equals("ppt")){
                        fileHeadResource=R.drawable.ppt;
                    }else if (msgFile.getFileType().equals("excel")){
                        fileHeadResource=R.drawable.excel;
                    }else if (msgFile.getFileType().equals("pdf")){
                        fileHeadResource=R.drawable.pdf;
                    }else if (msgFile.getFileType().equals("music")){
                        fileHeadResource=R.drawable.music;
                    }else if (msgFile.getFileType().equals("video")){
                        fileHeadResource=R.drawable.video;
                    }else if (msgFile.getFileType().equals("file")){
                        fileHeadResource=R.drawable.unknow;
                    }
                    Glide.with(mContext)
                            .load(fileHeadResource)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(((GroupChatLeftFileViewHolder) holder).file_type_img);
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((GroupChatLeftFileViewHolder) holder).file_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((GroupChatLeftFileViewHolder) holder).file_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                                menu.add(0, position, 1, "下载");
                            }
                        });
                    }
                }else {
                    ((GroupChatLeftFileViewHolder) holder).tv_file_msg.setVisibility(View.GONE);
                    ((GroupChatLeftFileViewHolder) holder).tv_file_name.setText("获取文件失败");
                    Glide.with(mContext)
                            .load(R.mipmap.ic_launcher_round)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(((GroupChatLeftFileViewHolder) holder).file_type_img);
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((GroupChatLeftFileViewHolder) holder).file_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((GroupChatLeftFileViewHolder) holder).file_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof GroupChatRightFileViewHolder){
                User me=userDao.load(LoginActivity.loginUser.getU_id());
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((GroupChatRightFileViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((GroupChatRightFileViewHolder) holder).tv_send_time.setText("");
                }
                if(groupMember!=null)
                    ((GroupChatRightFileViewHolder) holder).tv_group_member_name.setText(groupMember.getG_nick_name()+"");
                else
                    ((GroupChatRightFileViewHolder) holder).tv_group_member_name.setText(me.getU_nick_name()+"");
                if (msg.getIsSend()){
                    ((GroupChatRightFileViewHolder) holder).sendFailed.setVisibility(View.GONE);
                }else {
                    ((GroupChatRightFileViewHolder) holder).sendFailed.setVisibility(View.VISIBLE);
                    ((GroupChatRightFileViewHolder) holder).sendFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRecyclerViewItemClickListener!=null)
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                Glide.with(mContext)
                        .load(me.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((GroupChatRightFileViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((GroupChatRightFileViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ((GroupChatRightFileViewHolder) holder).tv_file_name.setText(""+msgFile.getFilename());
                    if (msg.getIsSend()){
                        ((GroupChatRightFileViewHolder) holder).tv_file_msg.setText("发送成功");
                    }else {
                        ((GroupChatRightFileViewHolder) holder).tv_file_msg.setText("发送失败");
                    }
                    int fileHeadResource=-1;
                    if (msgFile.getFileType().equals("word")){
                        fileHeadResource=R.drawable.word;
                    }else if (msgFile.getFileType().equals("ppt")){
                        fileHeadResource=R.drawable.ppt;
                    }else if (msgFile.getFileType().equals("excel")){
                        fileHeadResource=R.drawable.excel;
                    }else if (msgFile.getFileType().equals("pdf")){
                        fileHeadResource=R.drawable.pdf;
                    }else if (msgFile.getFileType().equals("music")){
                        fileHeadResource=R.drawable.music;
                    }else if (msgFile.getFileType().equals("video")){
                        fileHeadResource=R.drawable.video;
                    }else if (msgFile.getFileType().equals("file")){
                        fileHeadResource=R.drawable.unknow;
                    }
                    Glide.with(mContext)
                            .load(fileHeadResource)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(((GroupChatRightFileViewHolder) holder).file_type_img);
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((GroupChatRightFileViewHolder) holder).file_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((GroupChatRightFileViewHolder) holder).file_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }else {
                    ((GroupChatRightFileViewHolder) holder).tv_file_msg.setVisibility(View.GONE);
                    ((GroupChatRightFileViewHolder) holder).tv_file_name.setText("获取文件失败");
                    Glide.with(mContext)
                            .load(R.mipmap.ic_launcher_round)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(((GroupChatRightFileViewHolder) holder).file_type_img);
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((GroupChatRightFileViewHolder) holder).file_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((GroupChatRightFileViewHolder) holder).file_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof GroupChatLeftPicViewHolder){
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((GroupChatLeftPicViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((GroupChatLeftPicViewHolder) holder).tv_send_time.setText("");
                }
                if (groupMember!=null)
                    ((GroupChatLeftPicViewHolder) holder).tv_group_member_name.setText(groupMember.getG_nick_name()+"");
                else if (other!=null)
                    ((GroupChatLeftPicViewHolder) holder).tv_group_member_name.setText(other.getU_nick_name()+"");
                else
                    ((GroupChatLeftPicViewHolder) holder).tv_group_member_name.setText("UnKnown");
                Glide.with(mContext)
                        .load(other.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((GroupChatLeftPicViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((GroupChatLeftPicViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ViewGroup.LayoutParams params = ((GroupChatLeftPicViewHolder) holder).msg_pic.getLayoutParams();
                    params.width= width;
                    ((GroupChatLeftPicViewHolder) holder).msg_pic.setLayoutParams(params);
                    ((GroupChatLeftPicViewHolder) holder).msg_pic.setVisibility(View.VISIBLE);
                    String link=null;
                    if (msgFile.getLink().contains("http"))
                        link=proxyCacheServer.getProxyUrl(msgFile.getLink());
                    else
                        link=msgFile.getLink();
                    if (!photoMaps.contains(new photoMap(position,link)))
                        photoMaps.add(new photoMap(position,link));
                    Glide.with(mContext)
                            .asBitmap()
                            .load(link)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    resource=getRoundedCornerBitmap(resource,30f);
                                    ((GroupChatLeftPicViewHolder) holder).msg_pic.setImageBitmap(resource);
                                }
                            });
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((GroupChatLeftPicViewHolder) holder).msg_pic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((GroupChatLeftPicViewHolder) holder).msg_pic.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof GroupChatRightPicViewHolder){
                User me=userDao.load(LoginActivity.loginUser.getU_id());
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((GroupChatRightPicViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((GroupChatRightPicViewHolder) holder).tv_send_time.setText("");
                }
                if(groupMember!=null)
                    ((GroupChatRightPicViewHolder) holder).tv_group_member_name.setText(groupMember.getG_nick_name()+"");
                else
                    ((GroupChatRightPicViewHolder) holder).tv_group_member_name.setText(me.getU_nick_name()+"");
                if (msg.getIsSend()){
                    ((GroupChatRightPicViewHolder) holder).sendFailed.setVisibility(View.GONE);
                }else {
                    ((GroupChatRightPicViewHolder) holder).sendFailed.setVisibility(View.VISIBLE);
                    ((GroupChatRightPicViewHolder) holder).sendFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRecyclerViewItemClickListener!=null)
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                Glide.with(mContext)
                        .load(me.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((GroupChatRightPicViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((GroupChatRightPicViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    String link=null;
                    if (msgFile.getLink().contains("http"))
                        link=proxyCacheServer.getProxyUrl(msgFile.getLink());
                    else
                        link=msgFile.getLink();
                    if (!photoMaps.contains(new photoMap(position,link)))
                        photoMaps.add(new photoMap(position,link));
                    Glide.with(mContext)
                            .asBitmap()
                            .load(link)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    resource=getRoundedCornerBitmap(resource,30f);
                                    ((GroupChatRightPicViewHolder) holder).msg_pic.setImageBitmap(resource);
                                }
                            });
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((GroupChatRightPicViewHolder) holder).msg_pic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((GroupChatRightPicViewHolder) holder).msg_pic.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof GroupChatLeftVoiceViewHolder){
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((GroupChatLeftVoiceViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((GroupChatLeftVoiceViewHolder) holder).tv_send_time.setText("");
                }
                if (groupMember!=null)
                    ((GroupChatLeftVoiceViewHolder) holder).tv_group_member_name.setText(groupMember.getG_nick_name()+"");
                else if (other!=null)
                    ((GroupChatLeftVoiceViewHolder) holder).tv_group_member_name.setText(other.getU_nick_name()+"");
                else
                    ((GroupChatLeftVoiceViewHolder) holder).tv_group_member_name.setText("UnKnown");
                Glide.with(mContext)
                        .load(other.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((GroupChatLeftVoiceViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((GroupChatLeftVoiceViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ((GroupChatLeftVoiceViewHolder) holder).voice_msg_view.setVisibility(View.VISIBLE);
                    ((GroupChatLeftVoiceViewHolder) holder).voice_play_state.setText(""+getVoiceLength(msgFile.getLink()));
                    if (GroupChatActivity.viocePlay.length>position&&GroupChatActivity.viocePlay[position]){
                        ((GroupChatLeftVoiceViewHolder) holder).voice_play_control.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));
                    }else {
                        ((GroupChatLeftVoiceViewHolder) holder).voice_play_control.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play));
                    }
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((GroupChatLeftVoiceViewHolder) holder).voice_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((GroupChatLeftVoiceViewHolder) holder).voice_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
            else if (holder instanceof GroupChatRightVoiceViewHolder){
                User me=userDao.load(LoginActivity.loginUser.getU_id());
                if (lastMsg==null||(msg.getM_send_time()-lastMsg.getM_send_time())>10*60*1000){
                    //第一条消息或两条信息之间超过10分钟则显示发送时间
                    ((GroupChatRightVoiceViewHolder) holder).tv_send_time.setText(TimeUtil.QQFormatTime(msg.getM_send_time()));
                }else {
                    ((GroupChatRightVoiceViewHolder) holder).tv_send_time.setText("");
                }
                if(groupMember!=null)
                    ((GroupChatRightVoiceViewHolder) holder).tv_group_member_name.setText(groupMember.getG_nick_name()+"");
                else
                    ((GroupChatRightVoiceViewHolder) holder).tv_group_member_name.setText(me.getU_nick_name()+"");
                if (msg.getIsSend()){
                    ((GroupChatRightVoiceViewHolder) holder).sendFailed.setVisibility(View.GONE);
                }else {
                    ((GroupChatRightVoiceViewHolder) holder).sendFailed.setVisibility(View.VISIBLE);
                    ((GroupChatRightVoiceViewHolder) holder).sendFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnRecyclerViewItemClickListener!=null)
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                Glide.with(mContext)
                        .load(me.getU_head_img())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(((GroupChatRightVoiceViewHolder) holder).headImg);
                if(mOnRecyclerViewItemClickListener!=null){
                    ((GroupChatRightVoiceViewHolder) holder).headImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnRecyclerViewItemClickListener.onItemClick(v, position);
                        }
                    });
                }
                StringUtil.MsgFile msgFile=StringUtil.getMsgFile(msg.getM_content());
                if (msgFile!=null){
                    ((GroupChatRightVoiceViewHolder) holder).voice_msg_view.setVisibility(View.VISIBLE);
                    ((GroupChatRightVoiceViewHolder) holder).voice_play_state.setText(""+getVoiceLength(msgFile.getLink()));
                    if (GroupChatActivity.viocePlay.length>position&&GroupChatActivity.viocePlay[position]){
                        ((GroupChatRightVoiceViewHolder) holder).voice_play_control.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));
                    }else {
                        ((GroupChatRightVoiceViewHolder) holder).voice_play_control.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play));
                    }
                    if(mOnRecyclerViewItemClickListener!=null){
                        ((GroupChatRightVoiceViewHolder) holder).voice_msg_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnRecyclerViewItemClickListener.onItemClick(v, position);
                            }
                        });
                        ((GroupChatRightVoiceViewHolder) holder).voice_msg_view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(0, position, 0, "删除");
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        long loginUserId=me.getU_id();
        Message msg = mDatas.get(position);
        if (msg.getM_type()==0){
            if (msg.getM_content_type()==0&&msg.getM_source_id()!=(int)loginUserId){
                //普通用户消息，左
                return MSG_TYPE_OTHER_USER_NORMAL;
            }
            else  if (msg.getM_content_type()==0&&msg.getM_source_id()==(int)loginUserId){
                //普通用户消息，右
                return MSG_TYPE_MY_USER_NORMAL;
            }
            else  if (msg.getM_source_id()!=(int)loginUserId&&msg.getM_content_type()==4){
                return MSG_TYPE_OTHER_USER_FILE;
            }
            else  if (msg.getM_source_id()==(int)loginUserId&&msg.getM_content_type()==4){
                return MSG_TYPE_MY_USER_FILE;
            }
            else if (msg.getM_source_id()!=(int)loginUserId&&msg.getM_content_type()==3){
                return MSG_TYPE_OTHER_USER_VOICE;
            }
            else if (msg.getM_source_id()==(int)loginUserId&&msg.getM_content_type()==3){
                return MSG_TYPE_MY_USER_VOICE;
            }
            else if (msg.getM_source_id()!=(int)loginUserId&&msg.getM_content_type()==2){
                return MSG_TYPE_OTHER_PIC;
            }
            else{
                return MSG_TYPE_MY_USER_PIC;
            }
        }
        else {
            if (msg.getM_content_type()==0&&msg.getM_source_id()!=(int)loginUserId){
                return MSG_TYPE_OTHER_GROUP_NORMAL;
            }
            else if (msg.getM_content_type()==0&&msg.getM_source_id()==(int)loginUserId){
                return MSG_TYPE_MY_GROUP_NORMAL;
            }
            else if (msg.getM_source_id()!=(int)loginUserId&&msg.getM_content_type()==4){
                return MSG_TYPE_OTHER_GROUP_FILE;
            }
            else if (msg.getM_source_id()==(int)loginUserId&&msg.getM_content_type()==4){
                return MSG_TYPE_MY_GROUP_FILE;
            }
            else if (msg.getM_source_id()!=(int)loginUserId&&msg.getM_content_type()==2){
                return MSG_TYPE_OTHER_GROUP_PIC;
            }
            else if (msg.getM_source_id()==(int)loginUserId&&msg.getM_content_type()==2){
                return MSG_TYPE_MY_GROUP_PIC;
            }
            else if (msg.getM_source_id()==(int)loginUserId&&msg.getM_content_type()==3){
                return MSG_TYPE_MY_GROUP_VOICE;
            }
            else {
                return MSG_TYPE_OTHER_GROUP_VOICE;
            }
        }
    }
    /**
     * 获取在线音频时间长度
     *
     * @param url
     * @return
     */
    private String getVoiceLength(String url){
        if (url.contains("http"))
            url+= "?loginToken="+SharedPreferencesUtil.getInstance().getString("loginToken");
        String total;
        try {
            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(url);
            int duration = Integer.parseInt(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
            mmr.release();//释放资源
            if (0 != duration) {
                int s = duration / 1000;
                //设置文件时长，单位 "分:秒" 格式
                total = s / 60 + ":" + s % 60;
                return total;
            }else{
                return "获取失败";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "获取失败";
        }
    }
    @Override
    public int getItemCount() {
        if (mDatas!=null)
        return mDatas.size();
        else return 0;
    }

    public class photoMap {
        public photoMap(int position, String url) {
            this.position = position;
            this.url = url;
        }

        private int position;
        private String url;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof photoMap) {
                photoMap inItem = (photoMap) o;
                return position == inItem.getPosition();
            }
            return false;
        }

    }

    static class UserChatLeftNormalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_u_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.msg_content)
        EditText msg_content;
        @BindView(R.id.message_choice)
        ImageViewCheckBox choice;
        UserChatLeftNormalViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class UserChatRightNormalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_u_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.msg_content)
        EditText msg_content;
        @BindView(R.id.send_failed_img)
        ImageView sendFailed;
        @BindView(R.id.message_choice)
        ImageViewCheckBox choice;
        UserChatRightNormalViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class UserChatLeftFileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_u_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.file_msg_view)
        View file_msg_view;
        @BindView(R.id.tv_file_name)
        TextView tv_file_name;
        @BindView(R.id.tv_file_msg)
        TextView tv_file_msg;
        @BindView(R.id.file_type_img)
        ImageView file_type_img;
        @BindView(R.id.message_choice)
        ImageViewCheckBox choice;
        UserChatLeftFileViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class UserChatRightFileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_u_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.file_msg_view)
        View file_msg_view;
        @BindView(R.id.tv_file_name)
        TextView tv_file_name;
        @BindView(R.id.tv_file_msg)
        TextView tv_file_msg;
        @BindView(R.id.file_type_img)
        ImageView file_type_img;
        @BindView(R.id.send_failed_img)
        ImageView sendFailed;
        @BindView(R.id.message_choice)
        ImageViewCheckBox choice;
        UserChatRightFileViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class UserChatLeftPicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_u_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.msg_pic)
        ImageView msg_pic;
        @BindView(R.id.message_choice)
        ImageViewCheckBox choice;
        UserChatLeftPicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class UserChatRightPivViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_u_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.msg_pic)
        ImageView msg_pic;
        @BindView(R.id.send_failed_img)
        ImageView sendFailed;
        @BindView(R.id.message_choice)
        ImageViewCheckBox choice;
        UserChatRightPivViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class UserChatLeftVoiceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_u_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.voice_msg_view)
        View voice_msg_view;
        @BindView(R.id.voice_play_state)
        TextView voice_play_state;
        @BindView(R.id.voice_play_control)
        ImageView voice_play_control;
        @BindView(R.id.message_choice)
        ImageViewCheckBox choice;
        UserChatLeftVoiceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class UserChatRightVoiceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_u_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.voice_msg_view)
        View voice_msg_view;
        @BindView(R.id.voice_play_state)
        TextView voice_play_state;
        @BindView(R.id.voice_play_control)
        ImageView voice_play_control;
        @BindView(R.id.send_failed_img)
        ImageView sendFailed;
        @BindView(R.id.message_choice)
        ImageViewCheckBox choice;
        UserChatRightVoiceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class GroupChatLeftNormalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_g_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.msg_content)
        EditText msg_content;
        @BindView(R.id.tv_group_member_name)
        TextView tv_group_member_name;

        GroupChatLeftNormalViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class GroupChatRightNormalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_g_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.msg_content)
        EditText msg_content;
        @BindView(R.id.tv_group_member_name)
        TextView tv_group_member_name;
        @BindView(R.id.send_failed_img)
        ImageView sendFailed;
        GroupChatRightNormalViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class GroupChatLeftFileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_g_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.file_msg_view)
        View file_msg_view;
        @BindView(R.id.tv_file_name)
        TextView tv_file_name;
        @BindView(R.id.tv_file_msg)
        TextView tv_file_msg;
        @BindView(R.id.file_type_img)
        ImageView file_type_img;
        @BindView(R.id.tv_group_member_name)
        TextView tv_group_member_name;
        GroupChatLeftFileViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class GroupChatRightFileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_g_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.tv_file_name)
        TextView tv_file_name;
        @BindView(R.id.tv_file_msg)
        TextView tv_file_msg;
        @BindView(R.id.file_type_img)
        ImageView file_type_img;
        @BindView(R.id.tv_group_member_name)
        TextView tv_group_member_name;
        @BindView(R.id.send_failed_img)
        ImageView sendFailed;
        @BindView(R.id.file_msg_view)
        View file_msg_view;
        GroupChatRightFileViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class GroupChatLeftPicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_g_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.tv_group_member_name)
        TextView tv_group_member_name;
        @BindView(R.id.msg_pic)
        ImageView msg_pic;
        GroupChatLeftPicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class GroupChatRightPicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_g_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.tv_group_member_name)
        TextView tv_group_member_name;
        @BindView(R.id.send_failed_img)
        ImageView sendFailed;
        @BindView(R.id.msg_pic)
        ImageView msg_pic;
        GroupChatRightPicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class GroupChatLeftVoiceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_g_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.voice_play_state)
        TextView voice_play_state;
        @BindView(R.id.voice_play_control)
        ImageView voice_play_control;
        @BindView(R.id.tv_group_member_name)
        TextView tv_group_member_name;
        @BindView(R.id.voice_msg_view)
        View voice_msg_view;
        GroupChatLeftVoiceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    static class GroupChatRightVoiceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_g_head_img)
        ImageView headImg;
        @BindView(R.id.tv_send_time)
        TextView tv_send_time;
        @BindView(R.id.tv_group_member_name)
        TextView tv_group_member_name;
        @BindView(R.id.voice_play_state)
        TextView voice_play_state;
        @BindView(R.id.voice_play_control)
        ImageView voice_play_control;
        @BindView(R.id.send_failed_img)
        ImageView sendFailed;
        @BindView(R.id.voice_msg_view)
        View voice_msg_view;
        GroupChatRightVoiceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
