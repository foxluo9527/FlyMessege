package com.example.flymessagedome.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.bumptech.glide.Glide;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.DownloadTaskBean;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.FriendsBeanDao;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.service.MessageService;
import com.example.flymessagedome.ui.ImageViewCheckBox;
import com.example.flymessagedome.ui.adapter.ChatPhotoViewAdapter;
import com.example.flymessagedome.ui.adapter.MessageAdapter;
import com.example.flymessagedome.ui.contract.UserMessageContart;
import com.example.flymessagedome.ui.presenter.UserMessagePresenter;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.CommUtil;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.FileChooseUtil;
import com.example.flymessagedome.utils.FileUtil;
import com.example.flymessagedome.utils.NetworkType;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.StringUtil;
import com.example.flymessagedome.utils.ToastUtils;
import com.lewis_v.audiohandle.play.AudioPlayListener;
import com.lewis_v.audiohandle.play.AudioPlayManager;
import com.lewis_v.audiohandle.play.AudioPlayMode;
import com.lewis_v.audiohandle.recoder.AudioRecoderData;
import com.lewis_v.audiohandle.recoder.AudioRecoderListener;
import com.lewis_v.audiohandle.recoder.AudioRecoderManager;
import com.lewis_v.audiohandle.recoder.DefaultRecoderBuilder;
import com.lewis_v.audiohandle.recoder.RecoderBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconMultiAutoCompleteTextView;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.flymessagedome.service.MessageService.MSG_TYPE;
import static com.example.flymessagedome.service.MessageService.SERVICE_DISCONNECT;
import static com.example.flymessagedome.service.MessageService.SOCKET_SERVICE_ACTION;
import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_FILE;
import static com.example.flymessagedome.utils.Constant.RC_CHOOSE_PHOTO;
import static com.example.flymessagedome.utils.DataCleanManager.getFormatSize;

@SuppressLint({"NonConstantResourceId", "SetTextI18n"})
public class UserChatActivity extends BaseActivity implements UserMessageContart.View
        , EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener
        , EasyPermissions.PermissionCallbacks, MessageAdapter.OnRecyclerViewItemClickListener {
    private static final String TAG = "FlyMessage";
    @BindView(R.id.send_voice)
    ImageViewCheckBox send_voice;
    @BindView(R.id.send_pic)
    ImageViewCheckBox send_pic;
    @BindView(R.id.take_photo)
    ImageViewCheckBox take_photo;
    @BindView(R.id.send_file)
    ImageViewCheckBox send_file;
    @BindView(R.id.send_voice_control)
    ImageViewCheckBox send_voice_control;
    @BindView(R.id.send_voice_cancel)
    ImageView send_voice_cancel;
    @BindView(R.id.tv_name)
    TextView name;
    @BindView(R.id.tv_online_state)
    TextView onlineState;
    @BindView(R.id.send_voice_state_tv)
    TextView send_voice_state_tv;
    @BindView(R.id.msg_et)
    EmojiconMultiAutoCompleteTextView msgTv;
    @BindView(R.id.send_btn)
    Button sendBtn;
    @BindView(R.id.messge_list)
    RecyclerView recyclerView;
    @BindView(R.id.voice_view)
    View voiceView;
    @BindView(R.id.photo_view)
    View photoView;
    @BindView(R.id.photos_list)
    RecyclerView photoList;
    @BindView(R.id.get_photo)
    TextView getPhoto;
    @BindView(R.id.send_img_btn)
    Button sendImgBtn;
    @BindView(R.id.un_friend_view)
    View unFriendView;
    @BindView(R.id.emojicons)
    View emojisView;
    @BindView(R.id.cancel_choice)
    TextView cancelChoice;
    @BindView(R.id.tv_record)
    TextView recordMsg;
    @BindView(R.id.blacklist_state)
    TextView blacklistState;
    @BindView(R.id.bg_img)
    ImageView bg_img;
    @Inject
    UserMessagePresenter userMessagePresenter;

    public Context context = this;
    ArrayList<String> photoUrls = new ArrayList<>();
    UserBeanDao userBeanDao = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();
    MessageDao messageDao = FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    FriendsBeanDao friendsBeanDao = FlyMessageApplication.getInstances().getDaoSession().getFriendsBeanDao();
    MessageAdapter messageAdapter;
    UserBean user;
    private ArrayList<Message> messages;
    private final ArrayList<MessageAdapter.photoMap> photoMaps = new ArrayList<>();
    private final ArrayList<String> showPhotoUrls = new ArrayList<>();
    public ArrayList<Integer> choiceIndex = new ArrayList<>();
    public ArrayList<DownloadTaskBean> downloadTasks = new ArrayList<>();
    public static boolean[] choice;
    public static boolean[] viocePlay;
    private boolean playStateChanged = false;
    private String content = null;
    private boolean stopRefresh = false;
    private boolean onRecording = false;
    private long lastPlayMid = 0;
    //选中发送的图片张数
    private int choiceNum = 0;
    private long mPositionId = -1;
    private int showPhotoPosition = 0;
    ChatPhotoViewAdapter photoViewAdapter;
    long userId;
    //浏览图片返回不刷新消息列表
    public static boolean resultRefresh = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Aria.download(this).register();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @SuppressLint("HandlerLeak")
    Handler onRecordHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            if (recordMsg != null) {
                recordMsg.setText(msg.what + "/120s");
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler voicePlayDownHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            if (messageAdapter != null) {
                messageAdapter.notifyItemChanged(msg.what);
            }
        }
    };

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.back, R.id.more, R.id.send_btn, R.id.cancel_choice, R.id.send_img_btn, R.id.get_photo, R.id.send_voice_cancel, R.id.send_voice_control, R.id.add_blacklist, R.id.add_friend})
    public void onViewClicked(View view) {
        try {
            switch (view.getId()) {
                case R.id.back:
                    if (!TextUtils.isEmpty(content))
                        userMessagePresenter.inserting(user, content);
                    finish();
                    break;
                case R.id.more:
                    Intent intent = new Intent(mContext, ChatSettingActivity.class);
                    intent.putExtra("userName", user.getU_name());
                    startActivity(intent);
                    break;
                case R.id.send_btn:
                    showLoadingDialog(true, "消息发送中");
                    String content = msgTv.getText().toString();
                    userMessagePresenter.sendUserMessage(null, user, 0, content);
                    msgTv.setText("");
                    userMessagePresenter.inserting(user, null);
                    sendBtn.setEnabled(false);
                    break;
                case R.id.cancel_choice:
                    if (choiceNum > 0) {
                        for (Integer index :
                                choiceIndex) {
                            choice[index] = false;
                        }
                        choiceIndex.clear();
                        choiceNum = 0;
                        sendImgBtn.setEnabled(false);
                        photoList.getAdapter().notifyDataSetChanged();
                        cancelChoice.setText("取消选中(0)");
                    }
                    break;
                case R.id.send_img_btn:
                    if (choiceNum > 0) {
                        showLoadingDialog(true, "图片发送中");
                        for (Integer index :
                                choiceIndex) {
                            if (choice[index]) {
                                userMessagePresenter.sendUserMessage(photoUrls.get(index), user, 2, "");
                                choice[index] = !choice[index];
                                photoViewAdapter.notifyItemChanged(index);
                            }
                        }
                        choiceIndex.clear();
                        choiceNum = 0;
                        sendImgBtn.setEnabled(false);
                        cancelChoice.setText("取消选中(0)");
                    }
                    break;
                case R.id.get_photo:
                    choicePhotoWrapper();
                    break;
                case R.id.send_voice_cancel:
                    try {
                        if (onRecording)
                            AudioRecoderManager.getInstance().cancel(this);//取消录音
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.send_voice_control:
                    try {
                        if (onRecording) {
                            AudioRecoderManager.getInstance().stop(this);//结束录音
                        } else {
                            takeAudio();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.add_blacklist:
                    userMessagePresenter.addBlackList(user);
                    break;
                case R.id.add_friend:
                    Intent rq_intent = new Intent(mContext, RequestFriendActivity.class);
                    rq_intent.putExtra("uName", user.getU_name());
                    startActivity(rq_intent);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initEvent() {
        send_voice.setOnCheckStateChangedListener(isChecked -> {
            if (isChecked) {
                photoView.setVisibility(View.GONE);
                voiceView.setVisibility(View.VISIBLE);
                emojisView.setVisibility(View.GONE);
                send_pic.setChecked(false);
                send_file.setChecked(false);
                take_photo.setChecked(false);
                CommUtil.closeKeybord(msgTv, UserChatActivity.this);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            } else {
                voiceView.setVisibility(View.GONE);
            }
        });
        send_pic.setOnCheckStateChangedListener(isChecked -> {
            if (isChecked) {
                if (photoUrls.size() == 0)
                    getPhoto();
                photoView.setVisibility(View.VISIBLE);
                voiceView.setVisibility(View.GONE);
                emojisView.setVisibility(View.GONE);
                send_voice.setChecked(false);
                send_file.setChecked(false);
                take_photo.setChecked(false);
                CommUtil.closeKeybord(msgTv, UserChatActivity.this);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            } else {
                photoView.setVisibility(View.GONE);
            }
        });
        send_file.setOnCheckStateChangedListener(isChecked -> {
            if (isChecked) {
                photoView.setVisibility(View.GONE);
                voiceView.setVisibility(View.GONE);
                emojisView.setVisibility(View.GONE);
                send_pic.setChecked(false);
                send_voice.setChecked(false);
                take_photo.setChecked(false);
                CommUtil.closeKeybord(msgTv, UserChatActivity.this);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                takeFile();
            }
        });
        take_photo.setOnCheckStateChangedListener(isChecked -> {
            if (isChecked) {
                photoView.setVisibility(View.GONE);
                voiceView.setVisibility(View.GONE);
                emojisView.setVisibility(View.VISIBLE);
                send_pic.setChecked(false);
                send_voice.setChecked(false);
                send_file.setChecked(false);
                CommUtil.closeKeybord(msgTv, UserChatActivity.this);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
        recyclerView.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledUp() {
                super.onScrolledUp();
                CommUtil.closeKeybord(msgTv, UserChatActivity.this);
                photoView.setVisibility(View.GONE);
                voiceView.setVisibility(View.GONE);
                emojisView.setVisibility(View.GONE);
                send_pic.setChecked(false);
                send_voice.setChecked(false);
                send_file.setChecked(false);
                take_photo.setChecked(false);
            }

            @Override
            public void onScrolledDown() {
                super.onScrolledDown();
                CommUtil.closeKeybord(msgTv, UserChatActivity.this);
                photoView.setVisibility(View.GONE);
                voiceView.setVisibility(View.GONE);
                emojisView.setVisibility(View.GONE);
                send_pic.setChecked(false);
                send_voice.setChecked(false);
                send_file.setChecked(false);
                take_photo.setChecked(false);
            }

            @Override
            public void onScrolledToBottom() {
                super.onScrolledToBottom();
            }
        });
        msgTv.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                photoView.setVisibility(View.GONE);
                voiceView.setVisibility(View.GONE);
                emojisView.setVisibility(View.GONE);
                send_pic.setChecked(false);
                send_voice.setChecked(false);
                send_file.setChecked(false);
                take_photo.setChecked(false);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            } else {
                CommUtil.closeKeybord(msgTv, UserChatActivity.this);
            }
        });
        msgTv.setOnClickListener(v -> {
            photoView.setVisibility(View.GONE);
            voiceView.setVisibility(View.GONE);
            emojisView.setVisibility(View.GONE);
            send_pic.setChecked(false);
            send_voice.setChecked(false);
            send_file.setChecked(false);
            take_photo.setChecked(false);
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        });
        msgTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 198) {
                    ToastUtils.showToast("超过输入限制字符数:200");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                content = s.toString();
                if (s.length() > 0) {
                    sendBtn.setEnabled(true);

                } else {
                    userMessagePresenter.inserting(user, null);
                    sendBtn.setEnabled(false);
                }
            }
        });
        AudioPlayManager.getInstance().init(context)//初始化播放
                .setPlayListener(new AudioPlayListener() {//设置播放监听
                    @Override
                    public void onPlay(String audioPath) {//开始播放

                    }

                    @Override
                    public void onProgress(int progress, int maxSize) {//播放进度（未实现）

                    }

                    @Override
                    public void onPause() {//播放暂停（未实现）

                    }

                    @Override
                    public void onStop() {//停止播放
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    sleep(400);
                                    for (int i = 0; i < messages.size(); i++) {
                                        if (messages.get(i).getM_id() == lastPlayMid) {
                                            if (viocePlay[i] && !playStateChanged) {
                                                viocePlay[i] = false;
                                                voicePlayDownHandler.sendEmptyMessage(i);
                                            } else {
                                                playStateChanged = false;
                                            }
                                            break;
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }

                    @Override
                    public void onFail(Exception e, String msg) {//播放时出错
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    sleep(400);
                                    for (int i = 0; i < messages.size(); i++) {
                                        if (messages.get(i).getM_id() == lastPlayMid) {
                                            if (viocePlay[i] && !playStateChanged) {
                                                viocePlay[i] = false;
                                                voicePlayDownHandler.sendEmptyMessage(i);
                                            } else {
                                                playStateChanged = false;
                                            }
                                            break;
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
    }

    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        try {
            for (DownloadTaskBean downloadTask :
                    downloadTasks) {
                if (downloadTask.getUrl().equals(task.getKey())) {
                    Message message = messageDao.load(downloadTask.getMessageId());
                    message.setDownloadId(downloadTask.getDownloadId());
                    message.setDownloadState(1);
                    messageDao.update(message);
                    messageAdapter.notifyDataSetChanged();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Download.onTaskFail
    void taskFail(DownloadTask task, Exception e) {
        try {
            e.printStackTrace();
            for (DownloadTaskBean downloadTask :
                    downloadTasks) {
                if (downloadTask.getUrl().equals(task.getKey())) {
                    Message message = messageDao.load(downloadTask.getMessageId());
                    message.setDownloadState(2);
                    messageDao.update(message);
                    messageAdapter.notifyDataSetChanged();
                    ToastUtils.showToast("文件下载失败");
                    break;
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        try {
            for (DownloadTaskBean downloadTask :
                    downloadTasks) {
                if (downloadTask.getUrl().equals(task.getKey())) {
                    Message message = messageDao.load(downloadTask.getMessageId());
                    message.setDownloadState(4);
                    StringUtil.MsgFile msgFile = StringUtil.getMsgFile(message.getM_content());
                    msgFile.setLink(task.getFilePath());
                    message.setM_content(msgFile.toString());
                    messageDao.update(message);
                    messageAdapter.notifyDataSetChanged();
                    ToastUtils.showToast("文件下载完成");
                    downloadTasks.remove(downloadTask);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Download.onTaskStop
    void taskStop(DownloadTask task) {
        try {
            for (DownloadTaskBean downloadTask :
                    downloadTasks) {
                if (downloadTask.getUrl().equals(task.getKey())) {
                    Message message = messageDao.load(downloadTask.getMessageId());
                    message.setDownloadState(3);
                    messageDao.update(message);
                    messageAdapter.notifyDataSetChanged();
                    ToastUtils.showToast("下载暂停");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getGroupId() == 0 && item.getOrder() == 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setMessage("是否删除此条消息")
                    .setPositiveButton("确定", (dialog1, which) -> {
                        dialog1.cancel();
                        if (lastPlayMid == messages.get(item.getItemId()).getM_id() && messages.get(item.getItemId()).getM_content_type() == 3) {
                            for (int i = 0; i < messages.size(); i++) {
                                if (messages.get(i).getM_id() == lastPlayMid) {
                                    if (viocePlay[i]) {
                                        AudioPlayManager.getInstance().stop();
                                    }
                                    break;
                                }
                            }
                        }
                        userMessagePresenter.delMessage(user, item.getItemId());
                    })
                    .setNegativeButton("取消", (dialog12, which) -> dialog12.cancel())
                    .create().show();
        } else if (item.getGroupId() == 0 && item.getOrder() == 1) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setMessage("是否下载此文件")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            StringUtil.MsgFile msgFile = StringUtil.getMsgFile(messages.get(item.getItemId()).getM_content());
                            if (messages.get(item.getItemId()).getDownloadState() == 0 && msgFile.getLink().contains("http")) {
                                long taskId = Aria.download(this)
                                        .load(msgFile.getLink())     //读取下载地址
                                        .ignoreCheckPermissions()
                                        .ignoreFilePathOccupy()
                                        .setFilePath(Constant.DownloadPath + msgFile.getFilename()) //设置文件保存的完整路径
                                        .create();
                                ToastUtils.showToast("下载开始");
                                downloadTasks.add(new DownloadTaskBean(taskId, messages.get(item.getItemId()).getM_id(), msgFile.getLink()));
                            } else if (messages.get(item.getItemId()).getDownloadState() == 2 && msgFile.getLink().contains("http")) {
                                long taskId = Aria.download(this)
                                        .load(msgFile.getLink())     //读取下载地址
                                        .ignoreCheckPermissions()
                                        .ignoreFilePathOccupy()
                                        .setFilePath(Constant.DownloadPath + msgFile.getFilename()) //设置文件保存的完整路径
                                        .create();
                                ToastUtils.showToast("下载开始");
                                downloadTasks.add(new DownloadTaskBean(taskId, messages.get(item.getItemId()).getM_id(), msgFile.getLink()));
                            } else if (messages.get(item.getItemId()).getDownloadState() == 3 && msgFile.getLink().contains("http")) {
                                boolean hadAdded = false;
                                for (DownloadTaskBean downloadTask :
                                        downloadTasks) {
                                    if (downloadTask.getMessageId() == messages.get(item.getItemId()).getM_id()) {
                                        Aria.download(this)
                                                .load(downloadTask.getDownloadId())     //读取任务id
                                                .resume();    // 恢复任务
                                        ToastUtils.showToast("下载恢复");
                                        break;
                                    }
                                }
                                if (!hadAdded) {
                                    long taskId = Aria.download(this)
                                            .load(msgFile.getLink())     //读取下载地址
                                            .ignoreCheckPermissions()
                                            .ignoreFilePathOccupy()
                                            .setFilePath(Constant.DownloadPath + msgFile.getFilename()) //设置文件保存的完整路径
                                            .create();
                                    ToastUtils.showToast("下载开始");
                                    downloadTasks.add(new DownloadTaskBean(taskId, messages.get(item.getItemId()).getM_id(), msgFile.getLink()));
                                }
                            } else {
                                Message message = messages.get(item.getItemId());
                                message.setDownloadState(4);
                                messageDao.update(message);
                                messageAdapter.notifyDataSetChanged();
                                ToastUtils.showToast("文件已下载！");
                            }
                        }
                    })
                    .setNegativeButton("取消", (dialog13, which) -> dialog13.cancel())
                    .create().show();
        } else if (item.getGroupId() == 0 && item.getOrder() == 2) {
            StringUtil.MsgFile msgFile = StringUtil.getMsgFile(messages.get(item.getItemId()).getM_content());
            ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setPrimaryClip(ClipData.newPlainText("url", msgFile.getLink()));
            if (messages.get(item.getItemId()).getDownloadState() == 0 && msgFile.getLink().contains("http")) {
                ToastUtils.showToast("文件直链已复制在剪切板");
            } else {
                ToastUtils.showToast("文件路径已复制在剪切板");
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //退出时若输入框内有文字
        if (!TextUtils.isEmpty(content))
            userMessagePresenter.inserting(user, content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resultRefresh) {
            userMessagePresenter.getUserMsg(userId, true);
            initDatas();
        } else
            resultRefresh = true;
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        super.onNetConnected(networkType);
        try {
            initDatas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetDisconnected() {
        super.onNetDisconnected();
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);
        if (userId == -1) {
            ToastUtils.showToast("用户ID错误");
            finish();
            return;
        }
        mPositionId = intent.getLongExtra("positionMId", -1);
        if (user == null) {
            showLoadingDialog(true, "加载中");
            userMessagePresenter.getUserMsg(userId, true);
        } else {
            initUserMsg();
            if (NetworkUtils.isConnected(mContext)) {
                new Thread(refreshUserMsgRunnable).start();
            }
        }
        setEmojiIconFragment();
        initEvent();
    }

    @Override
    public void sendSuccess() {
        try {
            dismissLoadingDialog();
            userMessagePresenter.getMessages(user, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initEntryTextToView(String text) {
        try {
            if (!TextUtils.isEmpty(text) && msgTv != null) {
                msgTv.setText("" + text);
                sendBtn.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRefresh = false;
        refreshUserMsgRunnable = null;
        SharedPreferencesUtil.getInstance().putInt("onChatUserId", -1);
        if (!TextUtils.isEmpty(content))
            userMessagePresenter.inserting(user, content);
        if (!AudioPlayManager.getInstance().getPlayStatus().toString().equals("FREE")) {
            AudioPlayManager.getInstance().stop();
            AudioPlayManager.getInstance().destory();
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            if (!AudioPlayManager.getInstance().getPlayStatus().toString().equals("FREE")) {
                AudioPlayManager.getInstance().stop();
                AudioPlayManager.getInstance().destory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPressHome() {
        try {
            super.onPressHome();
            resultRefresh = false;
            if (!AudioPlayManager.getInstance().getPlayStatus().toString().equals("FREE")) {
                AudioPlayManager.getInstance().stop();
                AudioPlayManager.getInstance().destory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        try {
            super.onUserLeaveHint();
            resultRefresh = false;
            if (!AudioPlayManager.getInstance().getPlayStatus().toString().equals("FREE")) {
                AudioPlayManager.getInstance().stop();
                AudioPlayManager.getInstance().destory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void receiveUserMessage() {
        try {
            super.receiveUserMessage();
            int onChatUId = SharedPreferencesUtil.getInstance().getInt("onChatUserId", -1);
            userMessagePresenter.getMessages(user, user != null && MessageService.isRunningForeground(this) && onChatUId == user.getU_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable refreshUserMsgRunnable = new Runnable() {
        @Override
        public void run() {
            while (stopRefresh) {
                userMessagePresenter.getUserMsg(user.getU_id(), false);
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void configViews() {
        userMessagePresenter.attachView(this);
        userMessagePresenter.context = this;
    }

    private void setEmojiIconFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(false))
                .commit();
    }

    @Override
    public void initData() {
        try {
            //重试加载数据
            user = userBeanDao.load(userId);
            if (!NetworkUtils.isConnected(mContext)) {
                ToastUtils.showToast("更新用户信息失败，请检查网络连接");
            }
            userMessagePresenter.initUserChat(user);
            userMessagePresenter.getEntryText();
            userMessagePresenter.getMessages(user, true);
            initDatas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initChat(String filePath) {
        try {
            Glide.with(mContext).asBitmap().load(filePath).into(bg_img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initUserMsg() {
        try {
            dismissLoadingDialog();
            //只刷新用户信息
            user = userBeanDao.load(userId);
            List<FriendsBean> friendsBeans = friendsBeanDao.queryBuilder().where(FriendsBeanDao.Properties.F_object_u_id.eq(user.getU_id())).list();
            if (friendsBeans.size() > 0) {
                FriendsBean friendsBean = friendsBeans.get(0);
                if (friendsBean != null && friendsBean.getFriendUser() != null) {
                    if (!TextUtils.isEmpty(friendsBean.getF_remarks_name())) {
                        name.setText("" + friendsBean.getF_remarks_name());
                    } else {
                        name.setText("" + user.getU_nick_name());
                    }
                }
            } else {
                name.setText("" + user.getU_nick_name());
            }
            if (user.getIsOnline())
                onlineState.setText("在线");
            else
                onlineState.setText("离线");
            if (!user.getIsFriend())
                unFriendView.setVisibility(View.VISIBLE);
            else
                unFriendView.setVisibility(View.GONE);
            SharedPreferencesUtil.getInstance().putInt("onChatUserId", Integer.parseInt(user.getU_id().toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initBlackList(boolean inBlacklist) {
        try {
            dismissLoadingDialog();
            if (inBlacklist) {
                blacklistState.setText(R.string.move_out_blacklist);
            } else {
                blacklistState.setText(R.string.move_into_blacklist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initDataFailed() {
        try {
            dismissLoadingDialog();
            ToastUtils.showToast(getString(R.string.get_data_failed));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendFailed(Long m_id) {
        try {
            userMessagePresenter.getMessages(user, true);
            dismissLoadingDialog();
            ToastUtils.showToast("消息发送失败");
            if (m_id != null) {
                messageDao.load(m_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loginFailed(Login login) {
        try {
            dismissLoadingDialog();
            userMessagePresenter.getMessages(user, true);
            if (!NetworkUtils.isConnected(mContext) || login == null) {
                Intent actionIntent = new Intent(SOCKET_SERVICE_ACTION);
                actionIntent.putExtra(MSG_TYPE, SERVICE_DISCONNECT);
                mContext.sendBroadcast(actionIntent);
            } else if (login != null && login.code == Constant.FAILED) {
                Log.e(TAG, "获取用户登录信息失败，请重新登录");
                ToastUtils.showToast("获取用户登录信息失败，请重新登录");
                SharedPreferencesUtil.getInstance().removeAll();
                ActivityCollector.finishAll();
                SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, false);
                LoginActivity.startActivity(mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void initMessage(ArrayList<Message> messages) {
        try {
            this.messages = messages;
            dismissLoadingDialog();
            viocePlay = new boolean[messages.size()];
            messageAdapter = new MessageAdapter(this, messages, photoMaps);
            StaggeredGridLayoutManager msgGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(msgGridLayoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(messageAdapter);
            if (mPositionId == -1)
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            else {
                int position = -1;
                boolean hadPosition = false;
                for (Message m :
                        messages) {
                    position++;
                    if (m.getM_id() == mPositionId) {
                        hadPosition = true;
                        break;
                    }
                }
                if (hadPosition) {
                    recyclerView.scrollToPosition(position);
                }
                mPositionId = -1;
            }
            messageAdapter.setOnRecyclerViewItemClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void tokenExceed() {
        try {
            dismissLoadingDialog();
            String u_name = SharedPreferencesUtil.getInstance().getString(Constant.U_NAME);
            String u_pass = SharedPreferencesUtil.getInstance().getString(Constant.U_PASS);
            if (u_name != null && u_pass != null) {
                userMessagePresenter.login(u_name, u_pass);
            } else {
                Log.e(TAG, "获取用户登录信息失败，请重新登录");
                ToastUtils.showToast("获取用户登录信息失败，请重新登录");
                SharedPreferencesUtil.getInstance().removeAll();
                ActivityCollector.finishAll();
                SharedPreferencesUtil.getInstance().putBoolean(Constant.AUTO_LOGIN, false);
                LoginActivity.startActivity(mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(msgTv, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(msgTv);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == Constant.REQUEST_CODE_PERMISSION_CHOICE_PHOTO) {
            //同意获取读写权限
            getPhoto();
        } else if (requestCode == Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO) {
            choicePhotoWrapper();
        } else if (requestCode == Constant.REQUEST_CODE_SHOW_PHOTOS) {
            photoPreviewWrapper();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == Constant.REQUEST_CODE_PERMISSION_CHOICE_PHOTO) {
            ToastUtils.showToast("你拒绝了读写存储空间权限");
        } else if (requestCode == Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO) {
            ToastUtils.showToast("你拒绝了摄像头权限");
        } else if (requestCode == Constant.REQUEST_CODE_PERMISSION_CHOICE_FILE) {
            ToastUtils.showToast("你拒绝了读写存储空间权限");
        } else if (requestCode == Constant.REQUEST_CODE_PERMISSION_RECORD_AUDIO) {
            ToastUtils.showToast("你拒绝了录音和麦克风权限");
        } else if (requestCode == Constant.REQUEST_CODE_SHOW_PHOTOS) {
            ToastUtils.showToast("访问设备上的照片权限");
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1010);
            resultRefresh = false;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.l_u_head_img:
                Intent showIntent = new Intent(mContext, ShowUserActivity.class);
                showIntent.putExtra("userName", user.getU_name());
                startActivity(showIntent);
                break;
            case R.id.r_u_head_img:
                startActivity(new Intent(mContext, LoginUserMsgActivity.class));
                break;
            case R.id.msg_pic:
                int photoIndex = 0;
                showPhotoUrls.clear();
                for (MessageAdapter.photoMap photoMap : photoMaps) {
                    showPhotoUrls.add(photoMap.getUrl());
                }
                for (MessageAdapter.photoMap photoMap :
                        photoMaps) {
                    if (photoMap.getPosition() == position) {
                        showPhotoPosition = photoIndex;
                        photoPreviewWrapper();
                        break;
                    }
                    photoIndex++;
                }
                break;
            case R.id.file_msg_view:
                StringUtil.MsgFile msgFile = StringUtil.getMsgFile(messages.get(position).getM_content());
                if (!msgFile.getLink().contains("http")) {
                    FileUtil.openFile(mContext, new File(msgFile.getLink()));
                    resultRefresh = false;
                } else if (msgFile.getFileType().equals("ppt") || msgFile.getFileType().equals("word")
                        || msgFile.getFileType().equals("excel")
                        || msgFile.getFileType().equals("pdf")) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("URLString", Constant.ONLINE_OPEN_OFFICE_FILE + msgFile.getLink());
                    startActivity(intent);
                    resultRefresh = false;
                } else if (msgFile.getFileType().equals("video") || msgFile.getFileType().equals("music") || msgFile.getFileType().equals("gif")) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("URLString", msgFile.getLink());
                    startActivity(intent);
                    resultRefresh = false;
                } else {
                    if (messages.get(position).getDownloadState() != 4) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setMessage("是否下载此文件")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        StringUtil.MsgFile msgFile = StringUtil.getMsgFile(messages.get(position).getM_content());
                                        if (messages.get(position).getDownloadState() == 0) {
                                            long taskId = Aria.download(this)
                                                    .load(msgFile.getLink())     //读取下载地址
                                                    .ignoreCheckPermissions()
                                                    .ignoreFilePathOccupy()
                                                    .setFilePath(Constant.DownloadPath + msgFile.getFilename()) //设置文件保存的完整路径
                                                    .create();
                                            downloadTasks.add(new DownloadTaskBean(taskId, messages.get(position).getM_id(), msgFile.getLink()));
                                        }
                                    }
                                })
                                .setNegativeButton("取消", (dialog1, which) -> dialog1.cancel())
                                .create().show();
                    } else if (messages.get(position).getDownloadState() == 2) {
                        long taskId = Aria.download(this)
                                .load(msgFile.getLink())     //读取下载地址
                                .ignoreCheckPermissions()
                                .ignoreFilePathOccupy()
                                .setFilePath(Constant.DownloadPath + msgFile.getFilename()) //设置文件保存的完整路径
                                .create();
                        ToastUtils.showToast("下载开始");
                        downloadTasks.add(new DownloadTaskBean(taskId, messages.get(position).getM_id(), msgFile.getLink()));
                    } else if (messages.get(position).getDownloadState() == 3) {
                        boolean hadAdded = false;
                        for (DownloadTaskBean downloadTask :
                                downloadTasks) {
                            if (downloadTask.getMessageId() == messages.get(position).getM_id()) {
                                Aria.download(this)
                                        .load(downloadTask.getDownloadId())     //读取任务id
                                        .resume();    // 恢复任务
                                break;
                            }
                        }
                        if (!hadAdded) {
                            long taskId = Aria.download(this)
                                    .load(msgFile.getLink())     //读取下载地址
                                    .ignoreCheckPermissions()
                                    .ignoreFilePathOccupy()
                                    .setFilePath(Constant.DownloadPath + msgFile.getFilename()) //设置文件保存的完整路径
                                    .create();
                            ToastUtils.showToast("下载开始");
                            downloadTasks.add(new DownloadTaskBean(taskId, messages.get(position).getM_id(), msgFile.getLink()));
                        }
                    }
                }
                break;
            case R.id.send_failed_img:
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("重新发送此条消息")
                        .setPositiveButton("确定", (mDialog, which) -> {
                            mDialog.cancel();
                            showLoadingDialog(true, "消息发送中");
                            userMessagePresenter.reSendMessage(messages.get(position), user);
                        })
                        .setNegativeButton("取消", (mDialog, which) -> mDialog.cancel())
                        .create().show();
                break;
            case R.id.voice_msg_view:
                String state = AudioPlayManager.getInstance().getPlayStatus().toString();
                String link = StringUtil.getMsgFile(messages.get(position).getM_content()).getLink();
                Log.e(TAG, link);
                Log.e(TAG, state);
                if (lastPlayMid == 0 || state.equals("FREE")) {
                    lastPlayMid = messages.get(position).getM_id();
                    playVoice(link, false);
                    viocePlay[position] = true;
                    playStateChanged = false;
                } else if (lastPlayMid == messages.get(position).getM_id()) {
                    lastPlayMid = messages.get(position).getM_id();
                    if (state.equals("FREE")) {
                        playVoice(link, false);
                        viocePlay[position] = true;
                        playStateChanged = true;
                    } else {
                        playVoice(null, true);
                        viocePlay[position] = false;
                    }
                    playStateChanged = false;
                } else {
                    playVoice(null, true);
                    playVoice(link, false);
                    for (int i = 0; i < messages.size(); i++) {
                        if (messages.get(i).getM_id() == lastPlayMid) {
                            viocePlay[i] = false;
                            messageAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    lastPlayMid = messages.get(position).getM_id();
                    viocePlay[position] = true;
                    playStateChanged = true;
                }
                messageAdapter.notifyItemChanged(position);
                break;
        }
    }

    @Override
    public void onItemMenuClick(View view, int position, int itemId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        if (itemId == 2) {//删除
            dialog.setMessage("是否删除此条消息")
                    .setPositiveButton("确定", (dialog14, which) -> {
                        dialog14.cancel();
                        userMessagePresenter.delMessage(user, position);
                    })
                    .setNegativeButton("取消", (dialog15, which) -> dialog15.cancel())
                    .create().show();
        }
    }

    public abstract static class OnVerticalScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(-1)) {
                onScrolledToTop();
            } else if (!recyclerView.canScrollVertically(1)) {
                onScrolledToBottom();
            } else if (dy < 0) {
                onScrolledUp();
            } else if (dy > 0) {
                onScrolledDown();
            }
        }

        public void onScrolledUp() {
        }

        public void onScrolledDown() {
        }

        public void onScrolledToTop() {
        }

        public void onScrolledToBottom() {
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_CHOICE_PHOTO)
    @SuppressLint("StaticFieldLeak")
    public void getPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            resultRefresh = false;
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        ContentResolver cr = context.getContentResolver();
                        if (cr != null) {
                            @SuppressLint("Recycle") Cursor cursor = cr.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                            if (null == cursor) {
                                return false;
                            }
                            photoUrls.clear();
                            if (cursor.moveToFirst()) {
                                do {
                                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                    photoUrls.add(path);
                                } while (cursor.moveToNext());
                                choice = new boolean[photoUrls.size()];
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    try {
                        if (success) {
                            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
                            photoList.setLayoutManager(staggeredGridLayoutManager);
                            photoViewAdapter = new ChatPhotoViewAdapter(photoUrls, context, choice);
                            photoList.setAdapter(photoViewAdapter);
                            photoViewAdapter.setOnRecyclerViewItemClickListener(new ChatPhotoViewAdapter.OnRecyclerViewItemClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onItemClick(View view, int position) {
                                    if (view.getId() == R.id.main_view || view.getId() == R.id.radio_choice) {
                                        if (choiceNum == 20 && !choice[position]) {
                                            ToastUtils.showToast("最多选择20张");
                                            return;
                                        }
                                        choice[position] = !choice[position];
                                        photoList.getAdapter().notifyDataSetChanged();
                                        if (choice[position]) {
                                            choiceNum++;
                                            choiceIndex.add(position);
                                            sendImgBtn.setEnabled(true);
                                        } else {
                                            choiceIndex.remove(Integer.valueOf(position));
                                            choiceNum--;
                                            if (choiceNum == 0) {
                                                sendImgBtn.setEnabled(false);
                                            }
                                        }
                                        cancelChoice.setText("取消选中(" + choiceNum + ")");
                                    }
                                }

                            });
                        } else {
                            ToastUtils.showToast("获取图片信息失败");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } else {
            EasyPermissions.requestPermissions(this, "请开起读写存储空间权限，以正常使用", Constant.REQUEST_CODE_PERMISSION_CHOICE_PHOTO, perms);
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/photograph/");
            ArrayList<String> checkPhotos = new ArrayList<>();
            if (choiceNum > 0) {
                for (Integer index :
                        choiceIndex) {
                    checkPhotos.add(photoUrls.get(index));
                }
            }
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(20) // 图片选择张数的最大值
                    .selectedPhotos(checkPhotos) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
            resultRefresh = false;
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", Constant.REQUEST_CODE_PERMISSION_TAKE_PHOTO, perms);
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_RECORD_AUDIO)
    private void playVoice(String link, boolean stop) {
        try {
            String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(this, perms)) {
                if (stop) {
                    AudioPlayManager.getInstance().stop();
                } else {
                    AudioPlayManager.getInstance().play(link, context, AudioPlayMode.MEGAPHONE);
                }
            } else {
                EasyPermissions.requestPermissions(this, "播放语音选择需要以下权限:\n\n1.访问文件", Constant.REQUEST_CODE_PERMISSION_RECORD_AUDIO, perms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_RECORD_AUDIO)
    private void takeAudio() {
        try {
            String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(this, perms)) {
                try {
                    File takeRecordDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/chat/record/");
                    if (!takeRecordDir.exists()) {
                        takeRecordDir.mkdirs();
                    }

                    AudioManager magager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    AudioManager.OnAudioFocusChangeListener listener = focusChange -> {
                    };
                    magager.requestAudioFocus(listener, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);

                    RecoderBuilder builder = new DefaultRecoderBuilder()
                            .setMAX_LENGTH(2 * 60 * 1000)//最大录音120秒
                            .setMIN_LENGTH(500)//最小录音0.5秒
                            .setSAMPLEING_RATE(200)//录音监听回调间隔，200ms回调一次
                            .setSaveFolderPath(takeRecordDir.getPath() + "/");
                    AudioRecoderManager.getInstance()//获取单例
                            .setAudioRecoderData(builder.create())//设置自定义配置，已有默认的配置，可不用配置
                            .setAudioRecoderListener(new AudioRecoderListener() {//设置监听
                                @Override
                                public void onStart() {//开始录音
                                    send_voice_state_tv.setText("点击停止发送，点击取消取消发送");
                                    send_voice_cancel.setVisibility(View.VISIBLE);
                                    onRecording = true;
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();
                                            for (int i = 1; i < 121; i++) {
                                                if (!onRecording) {
                                                    this.interrupt();
                                                    break;
                                                } else {
                                                    android.os.Message msg = new android.os.Message();
                                                    msg.what = i;
                                                    onRecordHandler.sendMessage(msg);
                                                }
                                                try {
                                                    sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }.start();
                                }

                                @Override
                                public void onStop(AudioRecoderData audioRecoderData) {//停止/结束播放
                                    try {
                                        Log.e(TAG, audioRecoderData.getFilePath());
                                        showLoadingDialog(true, "语音发送中");
                                        send_voice_control.setChecked(false);
                                        userMessagePresenter.sendUserMessage(audioRecoderData.getFilePath(), user, 3, "");
                                        send_voice_state_tv.setText("点击开始录音");
                                        send_voice_cancel.setVisibility(View.GONE);
                                        recordMsg.setText("0/120s");
                                        onRecording = false;
                                        magager.abandonAudioFocus(listener);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onFail(Exception e, String msg) {//录音时出现的错误
                                    try {
                                        e.printStackTrace();
                                        send_voice_control.setChecked(false);
                                        ToastUtils.showToast("语音录制错误");
                                        send_voice_state_tv.setText("点击开始录音");
                                        send_voice_cancel.setVisibility(View.GONE);
                                        recordMsg.setText("0/120s");
                                        onRecording = false;
                                        magager.abandonAudioFocus(listener);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onCancel() {//录音取消
                                    try {
                                        send_voice_control.setChecked(false);
                                        ToastUtils.showToast("语音录制取消");
                                        send_voice_state_tv.setText("点击开始录音");
                                        send_voice_cancel.setVisibility(View.GONE);
                                        recordMsg.setText("0/120s");
                                        onRecording = false;
                                        magager.abandonAudioFocus(listener);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onSoundSize(int level) {//录音时声音大小的回调，分贝
                                    Log.e(TAG, "level:" + level);
                                }
                            });
                    AudioRecoderManager.getInstance().start(this);//开始录音
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                EasyPermissions.requestPermissions(this, "发送语音选择需要以下权限:\n\n1.录音\n\n2.麦克风\n\n3.访问文件", Constant.REQUEST_CODE_PERMISSION_RECORD_AUDIO, perms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_PERMISSION_CHOICE_FILE)
    private void takeFile() {
        try {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(this, perms)) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT < 19) {//因为Android SDK在4.4版本后图片action变化了 所以在这里先判断一下
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, RC_CHOOSE_FILE);
                resultRefresh = false;
            } else {
                EasyPermissions.requestPermissions(this, "文件选择需要以下权限:\n\n1.读取存储空间\n\n2.写入存储空间", Constant.REQUEST_CODE_PERMISSION_CHOICE_FILE, perms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE_SHOW_PHOTOS)
    public void photoPreviewWrapper() {
        if (showPhotoUrls == null || showPhotoUrls.size() == 0) {
            return;
        }
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "FlyMessage/File/download/");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(this)
                    .saveImgDir(downloadDir); // 保存图片的目录，如果传 null，则没有保存图片功能
            if (showPhotoUrls.size() == 1) {
                // 预览单张图片
                photoPreviewIntentBuilder.previewPhoto(showPhotoUrls.get(showPhotoPosition));
            } else if (showPhotoUrls.size() > 1) {
                // 预览多张图片
                photoPreviewIntentBuilder.previewPhotos(showPhotoUrls)
                        .currentPosition(showPhotoPosition); // 当前预览图片的索引
            }
            startActivityForResult(photoPreviewIntentBuilder.build(), -1);
            resultRefresh = false;
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", Constant.REQUEST_CODE_SHOW_PHOTOS, perms);
        }
    }

    @Override
    @SuppressLint("StaticFieldLeak")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            resultRefresh = false;
            if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
                List<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
                if (selectedPhotos.size() > 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setMessage("是否发送选中的" + selectedPhotos.size() + "张图片")
                            .setPositiveButton("确定", (dialog13, which) -> {
                                dialog13.cancel();
                                showLoadingDialog(false, "图片发送中");
                                for (String url :
                                        selectedPhotos) {
                                    userMessagePresenter.sendUserMessage(url, user, 2, "");
                                }
                                choiceIndex.clear();
                                choiceNum = 0;
                                sendImgBtn.setEnabled(false);
                                cancelChoice.setText("取消选中(0)");
                            })
                            .setNegativeButton("取消", (dialog12, which) -> dialog12.cancel())
                            .create().show();
                }
            } else if (requestCode == RC_CHOOSE_FILE) {
                send_file.setChecked(false);
                if (resultCode != RESULT_OK) {
                    return;
                }
                Uri uri = data.getData();
                try {
                    String chooseFilePath = FileChooseUtil.getInstance(mContext).getChooseFileResultPath(uri);
                    showLoadingDialog(true, "获取文件信息中");
                    new AsyncTask<Void, Void, Long>() {
                        @Override
                        protected Long doInBackground(Void... voids) {
                            long fileSize = 0;
                            try {
                                fileSize = new File(chooseFilePath).length();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return fileSize;
                        }

                        @Override
                        protected void onPostExecute(Long fileSize) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                            dismissLoadingDialog();
                            try {
                                if (0 < fileSize && fileSize < 20 * 1024 * 1024) {
                                    dialog.setMessage("是否发送文件:" + new File(chooseFilePath).getName() + "?（" + getFormatSize(fileSize) + ")")
                                            .setPositiveButton("确定", (dialog1, which) -> {
                                                dialog1.cancel();
                                                showLoadingDialog(false, "文件发送中");
                                                userMessagePresenter.sendUserMessage(chooseFilePath, user, 4, "");
                                            })
                                            .setNegativeButton("取消", (dialog14, which) -> dialog14.cancel())
                                            .create().show();
                                } else {
                                    dialog.setMessage("文件过大，请将文件大小限制在20M以下)")
                                            .setPositiveButton("确定", (dialog15, which) -> dialog15.cancel()).create().show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
