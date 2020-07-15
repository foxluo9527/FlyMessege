package com.example.flymessagedome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.utils.ActivityCollector;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.DataCleanManager;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.ToastUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CommonSettingActivity extends BaseActivity {
    @BindView(R.id.black_theme_switch)
    Switch black_theme_switch;
    @BindView(R.id.cache_size)
    TextView cache_size;
    ChatDao chatDao=FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    MessageDao messageDao=FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    @Override
    public int getLayoutId() {
        return R.layout.activity_common_setting;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }
    @OnClick({R.id.back,R.id.black_theme,R.id.clean_cache,R.id.clean_download_file,R.id.clean_chat_file,R.id.clean_temp_file,
    R.id.clean_message_record,R.id.clean_chat_record})
    public void onViewClick(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.black_theme:
                black_theme_switch.setChecked(!black_theme_switch.isChecked());
                break;
            case R.id.clean_cache:
                dialog.setMessage("是否清空缓存?包含好友及用户的背景图，头像以及发送的文件消息缓存")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanManager.clearAllCache(mContext);
                                try {
                                    cache_size.setText(DataCleanManager.getTotalCacheSize(mContext));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.cancel();//取消弹出框
                            }})
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
                break;
            case R.id.clean_chat_file:
                dialog.setMessage("是否清空聊天文件?包含聊天拍摄发送的图片")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanManager.deleteFile(new File(Constant.RootPath+"/FlyMessage/file/photograph/"));
                                DataCleanManager.deleteFile(new File(Constant.RootPath+"/FlyMessage/file/takePhoto/"));
                                ToastUtils.showToast("清空聊天文件成功");
                                dialog.cancel();//取消弹出框
                            }})
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
                break;
            case R.id.clean_temp_file:
                dialog.setMessage("是否清空临时文件?包含裁剪的头像，背景临时文件")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanManager.deleteFile(new File(Constant.RootPath+"/FlyMessage/temp"));
                                ToastUtils.showToast("清空临时文件成功");
                                dialog.cancel();//取消弹出框
                            }})
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
                break;
            case R.id.clean_download_file:
                dialog.setMessage("是否清空下载文件?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanManager.deleteFile(new File(Constant.DownloadPath));
                                ToastUtils.showToast("清空下载文件夹成功");
                                dialog.cancel();//取消弹出框
                            }})
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
                break;
            case R.id.clean_message_record:
                dialog.setMessage("是否清空所有聊天记录?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                messageDao.deleteAll();
                                List<Chat> chats=chatDao.loadAll();
                                for (Chat c:
                                        chats) {
                                    c.setChat_reshow(false);
                                    chatDao.update(c);
                                }
                                ToastUtils.showToast("清空聊天记录成功");
                                dialog.cancel();//取消弹出框
                            }})
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
                break;
            case R.id.clean_chat_record:
                dialog.setMessage("是否清空所有消息列表?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<Chat> chats=chatDao.loadAll();
                                for (Chat c:
                                     chats) {
                                    c.setChat_reshow(false);
                                    chatDao.update(c);
                                }
                                ToastUtils.showToast("清空消息列表成功");
                                dialog.cancel();//取消弹出框
                            }})
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
                break;
        }
    }
    @Override
    public void initDatas() {
        try {
            cache_size.setText(DataCleanManager.getTotalCacheSize(mContext));
        } catch (Exception e) {
            e.printStackTrace();
        }
        black_theme_switch.setChecked(SharedPreferencesUtil.getInstance().getBoolean("withSystemDark",true));
        black_theme_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putBoolean("withSystemDark",isChecked);
                FlyMessageApplication.changeDefaultDarkModel(isChecked);
            }
        });
    }

    @Override
    public void configViews() {

    }
}
