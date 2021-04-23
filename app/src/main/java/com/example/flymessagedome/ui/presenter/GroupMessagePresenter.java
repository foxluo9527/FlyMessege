package com.example.flymessagedome.ui.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupBeanDao;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.GroupMemberDao;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.model.GroupMemberModel;
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.model.SendMessageModel;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.contract.GroupMessageContract;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupMessagePresenter extends RxPresenter<GroupMessageContract.View> implements GroupMessageContract.Presenter<GroupMessageContract.View> {
    private final FlyMessageApi flyMessageApi;
    public Context context;
    public Chat userChat = null;
    ArrayList<Message> messages;
    MessageDao messageDao = FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    GroupBeanDao groupBeanDao = FlyMessageApplication.getInstances().getDaoSession().getGroupBeanDao();
    GroupMemberDao groupMemberDao = FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao();
    ChatDao chatDao = FlyMessageApplication.getInstances().getDaoSession().getChatDao();

    @Inject
    public GroupMessagePresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getGroupMsg(long groupId, boolean reset) {
        //如果网络连接则获取更新群聊信息，否则获取本地群聊信息
        if (NetworkUtils.isConnected(context)) {
            String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
            Subscription rxSubscription = flyMessageApi.getGroupMsg((int) groupId, loginToken).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GroupModel>() {
                        @Override
                        public void onCompleted() {
                            mView.complete();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mView.tokenExceed();
                        }

                        @Override
                        public void onNext(GroupModel groupModel) {
                            if (groupModel != null && mView != null && groupModel.code == Constant.SUCCESS) {
                                groupBeanDao.insertOrReplace(groupModel.getGroup());
                                if (reset)
                                    mView.initData();
                                else
                                    mView.initGroupMsg();
                            } else if (groupModel != null && mView != null && groupModel.code == Constant.FAILED) {
                                mView.initDataFailed();
                            } else {
                                mView.tokenExceed();
                            }
                        }
                    });
            addSubscribe(rxSubscription);
        } else {
            mView.initData();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getMessages(GroupBean group, boolean read) {
        if (LoginActivity.loginUser == null) return;
        long u_id = LoginActivity.loginUser.getU_id();
        messages = (ArrayList<Message>) messageDao.queryBuilder()
                .where(MessageDao.Properties.Login_u_id.eq(u_id))
                .whereOr(MessageDao.Properties.M_source_g_id.eq(group.getG_id()), MessageDao.Properties.M_source_id.eq(u_id))
                .whereOr(MessageDao.Properties.M_object_id.eq(u_id), MessageDao.Properties.M_source_g_id.eq(group.getG_id()))
                .where(MessageDao.Properties.M_type.eq(1))
                .orderAsc(MessageDao.Properties.M_send_time)
                .distinct()
                .list();
        Chat chat = chatDao.queryBuilder()
                .where(ChatDao.Properties.Chat_type.eq(1))
                .where(ChatDao.Properties.Source_g_id.eq(group.getG_id()))
                .where(ChatDao.Properties.Object_u_id.eq(u_id))
                .unique();
        for (Message message :
                messages) {
            if (read) {
                message.setM_receive_state(1);
                message.setCId(chat.getC_id());
                messageDao.update(message);
            }
            List<GroupMember> members = groupMemberDao.queryBuilder()
                    .where(GroupMemberDao.Properties.G_id.eq(group.getG_id()))
                    .where(GroupMemberDao.Properties.U_id.eq(message.getM_source_id()))
                    .list();
            GroupMember groupMember = null;
            if (members.size() > 0) {
                groupMember = members.get(0);
            }
            if (groupMember == null) {
                new AsyncTask<Void, Void, GroupMemberModel>() {
                    @Override
                    protected GroupMemberModel doInBackground(Void... voids) {
                        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
                        return HttpRequest.getGroupMember(message.getM_source_g_id(), message.getM_source_id(), loginToken);
                    }


                    @Override
                    protected void onPostExecute(GroupMemberModel aVoid) {
                        try {
                            if (aVoid != null && aVoid.code == Constant.SUCCESS) {
                                groupMemberDao.insertOrReplace(aVoid.getGroup_member());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.execute();
            }
        }
        if (chat != null) {
            chat.setChat_m_count(0);
            chatDao.update(chat);
        } else {
            initUserChat(group);
        }
        if (mView != null)
            mView.initMessage(messages);
    }


    @Override
    public void sendUserMessage(String fileUrl, GroupBean group, int m_content_type, String content) {
        userChat.setChat_reshow(true);
        userChat.setChat_content(content);
        userChat.setChat_head(group.getG_head_img());
        userChat.setChat_m_count(0);
        userChat.setChat_name(group.getG_name());
        userChat.setTime(new Date());
        chatDao.update(userChat);
        Message sendMessage = new Message();
        sendMessage.setCId(userChat.getC_id());
        sendMessage.setM_id(null);
        sendMessage.setM_receive_state(0);
        sendMessage.setIsSend(false);
        if (m_content_type == 0) {
            sendMessage.setM_content(content);
        } else if (fileUrl != null) {
            File file = new File(fileUrl);
            String baseName = file.getName();
            String fileType = fileUrl.substring(fileUrl.lastIndexOf('.'));
            String fileContent = "";
            if (m_content_type == 2) {
                fileContent = "[picture:" + baseName + ",link:" + fileUrl + "]";
            } else if (m_content_type == 3) {
                fileContent = "[voice:" + baseName + ",link:" + fileUrl + "]";
            } else if (m_content_type == 4) {
                if (fileType.equalsIgnoreCase(".doc") || fileType.equalsIgnoreCase(".docx")) {
                    fileContent = "[word:" + baseName + ",link:" + fileUrl + "]";
                } else if (fileType.equalsIgnoreCase(".pptx")) {
                    fileContent = "[ppt:" + baseName + ",link:" + fileUrl + "]";
                } else if (fileType.equalsIgnoreCase(".xls") || fileType.equalsIgnoreCase(".xlsx")) {
                    fileContent = "[excel:" + baseName + ",link:" + fileUrl + "]";
                } else if (fileType.equalsIgnoreCase(".pdf")) {
                    fileContent = "[pdf:" + baseName + ",link:" + fileUrl + "]";
                } else if (fileType.equalsIgnoreCase(".mp3")
                        || fileType.equalsIgnoreCase(".flac")
                        || fileType.equalsIgnoreCase(".aac")
                        || fileType.equalsIgnoreCase(".acc")) {
                    fileContent = "[music:" + baseName + ",link:" + fileUrl + "]";
                } else if (fileType.equalsIgnoreCase(".mp4")) {
                    fileContent = "[video:" + baseName + ",link:" + fileUrl + "]";
                } else if (fileType.equalsIgnoreCase(".JPG") || fileType.equalsIgnoreCase(".PNG") || fileType.equalsIgnoreCase(".JPEG") || fileType.equalsIgnoreCase(".GIF")) {
                    fileContent = "[picture:" + baseName + ",link:" + fileUrl + "]";
                    sendMessage.setM_content_type(2);
                    m_content_type = 2;
                } else {
                    fileContent = "[file:" + baseName + ",link:" + fileUrl + "]";
                }
            }
            userChat.setChat_content(StringUtil.formatFileMessage(fileContent));
            sendMessage.setM_content(fileContent);
            chatDao.update(userChat);
        }
        sendMessage.setM_content_type(m_content_type);
        sendMessage.setM_source_g_id(Integer.parseInt(String.valueOf(group.getG_id())));
        sendMessage.setM_source_id((int) LoginActivity.loginUser.getU_id());
        sendMessage.setM_send_time(new Date().getTime());
        sendMessage.setM_type(1);
        sendMessage.setLogin_u_id(LoginActivity.loginUser.getU_id());
        messageDao.insert(sendMessage);
        String loginToken = SharedPreferencesUtil.getInstance().getString(Constant.LOGIN_TOKEN);
        if (!NetworkUtils.isConnected(context) || loginToken == null) {
            mView.tokenExceed();
            mView.sendFailed(sendMessage.getM_id());
            userChat.setLastSendFailed(true);
            chatDao.update(userChat);
            return;
        }
        Subscription rxSubscription = flyMessageApi.sendGroupMessage(fileUrl, loginToken, LoginActivity.loginUser.getU_id(), group.getG_id(), m_content_type, content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SendMessageModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.tokenExceed();
                        userChat.setLastSendFailed(true);
                        chatDao.update(userChat);
                        mView.sendFailed(sendMessage.getM_id());
                    }

                    @Override
                    public void onNext(SendMessageModel base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            userChat.setChat_content(StringUtil.formatFileMessage(sendMessage.getM_content()));
                            userChat.setChat_reshow(true);
                            userChat.setLastSendFailed(false);
                            chatDao.update(userChat);
                            messageDao.delete(sendMessage);
                            sendMessage.setIsSend(true);
                            sendMessage.setM_id((long) base.getMId());
                            sendMessage.setM_send_time(base.getTime());
                            messageDao.insertOrReplace(sendMessage);
                            mView.sendSuccess();
                        } else if (base != null && mView != null && base.code == Constant.FAILED) {
                            userChat.setLastSendFailed(true);
                            chatDao.update(userChat);
                            mView.sendFailed(sendMessage.getM_id());
                        } else if (base != null && base.code == Constant.NOT_LOGIN) {
                            userChat.setLastSendFailed(true);
                            chatDao.update(userChat);
                            mView.tokenExceed();
                        } else {
                            userChat.setLastSendFailed(true);
                            chatDao.update(userChat);
                            mView.sendFailed(sendMessage.getM_id());
                            mView.tokenExceed();
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void inserting(GroupBean group, String content) {
        //用户输入时，动态改变聊天列表显示正在输入的内容
        userChat.setChat_content(content);
        userChat.setChat_head(group.getG_head_img());
        userChat.setChat_m_count(0);
        userChat.setChat_name(group.getG_name());
        userChat.setTime(new Date());
        chatDao.insertOrReplace(userChat);
        userChat = chatDao.queryBuilder()
                .where(ChatDao.Properties.Source_g_id.eq(group.getG_id()))
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(ChatDao.Properties.Chat_type.eq(1))
                .unique();
        userChat.setInEntering(!TextUtils.isEmpty(content));
        if (!TextUtils.isEmpty(content)) {
            userChat.setChat_content(StringUtil.formatFileMessage(content));
            userChat.setTime(new Date());
            userChat.setChat_reshow(true);
        } else if (messages.size() > 0) {
            if (messages.get(messages.size() - 1).getM_source_id() == LoginActivity.loginUser.getU_id()) {
                userChat.setChat_content(messages.get(messages.size() - 1).getM_content());
            } else {
                GroupMember groupMember = groupMemberDao.queryBuilder()
                        .where(GroupMemberDao.Properties.G_id.eq(group.getG_id()))
                        .where(GroupMemberDao.Properties.U_id.eq((long) messages.get(messages.size() - 1).getM_source_id()))
                        .unique();
                userChat.setChat_content(groupMember.getG_nick_name() + ":" + messages.get(messages.size() - 1).getM_content());
            }
            userChat.setTime(new Date(messages.get(messages.size() - 1).getM_send_time()));
        } else {
            userChat.setChat_reshow(false);
        }
        chatDao.update(userChat);
    }

    @Override
    public void getEntryText() {
        //再次进入聊天界面获取上次正在输入的草稿
        if (userChat != null && userChat.getInEntering()) {
            String content = userChat.getChat_content();
            mView.initEntryTextToView(content);
        }
    }

    @Override
    public void initUserChat(GroupBean group) {
        //初始化整个chat聊天bean
        if (userChat == null) {
            if (LoginActivity.loginUser == null) {
                mView.tokenExceed();
                return;
            }
            userChat = chatDao.queryBuilder()
                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                    .where(ChatDao.Properties.Source_g_id.eq(group.getG_id()))
                    .where(ChatDao.Properties.Chat_type.eq(1))
                    .unique();
            if (userChat == null) {
                userChat = new Chat();
                userChat.setChat_show_remind(true);
                userChat.setChat_up(false);
                userChat.setChat_type(1);
                userChat.setSource_g_id(group.getG_id());
                userChat.setChat_reshow(false);
                userChat.setObject_u_id(LoginActivity.loginUser.getU_id());
                chatDao.insert(userChat);
            }
        }
        if (!TextUtils.isEmpty(userChat.getBgImg())) {
            mView.initChat(userChat.getBgImg());
        }
    }

    @Override
    public void delMessage(GroupBean group, int position) {
        //删除单挑消息
        messageDao.deleteByKey(messages.get(position).getM_id());
        getMessages(group, true);
        if (messages == null) {
            return;
        } else if (messages.size() > 0 && !userChat.getInEntering()) {
            userChat.setChat_content(messages.get(messages.size() - 1).getM_content());
            userChat.setTime(new Date(messages.get(messages.size() - 1).getM_send_time()));
        } else if (messages.size() == 0 && !userChat.getInEntering()) {
            userChat.setChat_reshow(false);
        }
        chatDao.update(userChat);
    }

    @Override
    public void reSendMessage(Message message, GroupBean group) {
        //重发消息
        String fileContent = message.getM_content();
        userChat.setChat_reshow(true);
        userChat.setChat_content(message.getM_content());
        userChat.setChat_head(group.getG_head_img());
        userChat.setChat_m_count(0);
        userChat.setChat_name(group.getG_name());
        userChat.setTime(new Date());
        chatDao.update(userChat);
        message.setM_send_time(new Date().getTime());
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        if (!NetworkUtils.isConnected(context) || loginToken == null) {
            mView.tokenExceed();
            mView.sendFailed(message.getM_id());
            userChat.setLastSendFailed(true);
            chatDao.update(userChat);
            return;
        }
        String fileUrl = null;
        if (message.getM_content_type() > 0) {
            fileUrl = StringUtil.getMsgFile(message.getM_content()).getLink();
            message.setM_content("");
        }

        Subscription rxSubscription = flyMessageApi.sendGroupMessage(fileUrl, loginToken, (int) LoginActivity.loginUser.getU_id(), group.getG_id(), message.getM_content_type(), message.getM_content()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SendMessageModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.tokenExceed();
                        userChat.setChat_content(StringUtil.formatFileMessage(fileContent));
                        userChat.setLastSendFailed(true);
                        chatDao.update(userChat);
                        mView.sendFailed(message.getM_id());
                        message.setM_content(fileContent);
                        message.setIsSend(false);
                        messageDao.update(message);
                    }

                    @Override
                    public void onNext(SendMessageModel base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            userChat.setChat_content(StringUtil.formatFileMessage(fileContent));
                            userChat.setChat_reshow(true);
                            userChat.setLastSendFailed(false);
                            chatDao.update(userChat);
                            messageDao.delete(message);
                            message.setIsSend(true);
                            message.setM_id((long) base.getMId());
                            message.setM_send_time(base.getTime());
                            message.setM_content(fileContent);
                            messageDao.insertOrReplace(message);
                            mView.sendSuccess();
                        } else if (base != null && mView != null && base.code == Constant.FAILED) {
                            userChat.setLastSendFailed(true);
                            userChat.setChat_content(StringUtil.formatFileMessage(fileContent));
                            chatDao.update(userChat);
                            message.setM_content(fileContent);
                            message.setIsSend(false);
                            messageDao.update(message);
                            mView.sendFailed(message.getM_id());
                        } else if (base != null && base.code == Constant.NOT_LOGIN) {
                            userChat.setLastSendFailed(true);
                            userChat.setChat_content(StringUtil.formatFileMessage(fileContent));
                            chatDao.update(userChat);
                            mView.tokenExceed();
                        } else {
                            userChat.setLastSendFailed(true);
                            userChat.setChat_content(StringUtil.formatFileMessage(fileContent));
                            chatDao.update(userChat);
                            message.setM_content(fileContent);
                            message.setIsSend(false);
                            messageDao.update(message);
                            mView.sendFailed(message.getM_id());
                            mView.tokenExceed();
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
