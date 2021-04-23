package com.example.flymessagedome.ui.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.CheckBlackListModel;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.model.SendMessageModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.contract.UserMessageContart;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserMessagePresenter extends RxPresenter<UserMessageContart.View> implements UserMessageContart.Presenter<UserMessageContart.View> {
    private final FlyMessageApi flyMessageApi;
    public Context context;
    public Chat userChat = null;
    ArrayList<Message> messages;
    MessageDao messageDao = FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    ChatDao chatDao = FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    UserBeanDao userBeanDao = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();

    @Inject
    public UserMessagePresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getUserMsg(long userId, boolean reset) {
        //如果网络连接则获取更新用户信息，否则获取本地用户信息
        if (NetworkUtils.isConnected(context)) {
            String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
            Subscription rxSubscription = flyMessageApi.getUsersMsg((int) userId, loginToken).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Users>() {
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
                        public void onNext(Users users) {
                            if (users != null && mView != null && users.code == Constant.SUCCESS) {
                                userBeanDao.insertOrReplace(users.getUser());
                                if (reset)
                                    mView.initData();
                                else
                                    mView.initUserMsg();
                            } else if (users != null && mView != null && users.code == Constant.FAILED) {
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

    @Override
    public void getMessages(UserBean user, boolean read) {
        if (LoginActivity.loginUser == null) return;
        long u_id = LoginActivity.loginUser.getU_id();
        messages = (ArrayList<Message>) messageDao.queryBuilder()
                .where(MessageDao.Properties.Login_u_id.eq(LoginActivity.loginUser.getU_id()))
                .whereOr(MessageDao.Properties.M_source_id.eq(user.getU_id()), MessageDao.Properties.M_source_id.eq(u_id))
                .whereOr(MessageDao.Properties.M_object_id.eq(u_id), MessageDao.Properties.M_object_id.eq(user.getU_id()))
                .where(MessageDao.Properties.M_type.eq(0))
                .orderAsc(MessageDao.Properties.M_send_time)
                .distinct()
                .list();
        Chat chat = chatDao.queryBuilder()
                .where(ChatDao.Properties.Chat_type.eq(0))
                .where(ChatDao.Properties.Source_id.eq(user.getU_id()))
                .where(ChatDao.Properties.Object_u_id.eq(u_id))
                .unique();
        if (read) {
            for (Message message :
                    messages) {
                message.setM_receive_state(1);
                message.setCId(chat.getC_id());
                messageDao.update(message);
            }
        }
        if (chat != null) {
            chat.setChat_m_count(0);
            chatDao.update(chat);
        } else {
            initUserChat(user);
        }
        if (mView != null)
            mView.initMessage(messages);
    }

    @Override
    public void login(String userName, String password) {
        Subscription rxSubscription = flyMessageApi.getLogin(userName, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFailed(null);
                    }

                    @Override
                    public void onNext(Login login) {
                        if (login != null && mView != null && login.code == Constant.SUCCESS) {
                            SharedPreferencesUtil.getInstance().putString(Constant.U_NAME, userName);
                            SharedPreferencesUtil.getInstance().putString(Constant.U_PASS, password);
                            SharedPreferencesUtil.getInstance().putString(Constant.LOGIN_TOKEN, login.getToken());
                            MainActivity.serviceBinder.connect();
                        } else {
                            mView.loginFailed(login);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void sendUserMessage(String fileUrl, UserBean userBean, int m_content_type, String content) {
        userChat.setChat_reshow(true);
        userChat.setChat_content(content);
        userChat.setChat_head(userBean.getU_head_img());
        userChat.setChat_m_count(0);
        userChat.setChat_name(userBean.getU_nick_name());
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
        sendMessage.setM_object_id(Integer.parseInt(userBean.getU_id().toString()));
        sendMessage.setM_source_id((int) LoginActivity.loginUser.getU_id());
        sendMessage.setM_send_time(new Date().getTime());
        sendMessage.setM_source_g_id(0);
        sendMessage.setM_type(0);
        sendMessage.setLogin_u_id(LoginActivity.loginUser.getU_id());
        messageDao.insert(sendMessage);
        String loginToken = SharedPreferencesUtil.getInstance().getString(Constant.LOGIN_TOKEN);
        if (!NetworkUtils.isConnected(context) || loginToken == null) {
            mView.loginFailed(null);
            mView.sendFailed(sendMessage.getM_id());
            userChat.setLastSendFailed(true);
            chatDao.update(userChat);
            return;
        }
        Subscription rxSubscription = flyMessageApi.sendUserMessage(fileUrl, loginToken, (int) LoginActivity.loginUser.getU_id(), Integer.parseInt(userBean.getU_id().toString()), m_content_type, content).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SendMessageModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFailed(null);
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
                            mView.loginFailed(null);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void inserting(UserBean userBean, String content) {
        userChat.setChat_content(content);
        userChat.setChat_head(userBean.getU_head_img());
        userChat.setChat_m_count(0);
        userChat.setChat_name(userBean.getU_nick_name());
        userChat.setTime(new Date());
        chatDao.insertOrReplace(userChat);
        userChat = chatDao.queryBuilder()
                .where(ChatDao.Properties.Source_id.eq(userBean.getU_id()))
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(ChatDao.Properties.Chat_type.eq(0))
                .unique();
        userChat.setInEntering(!TextUtils.isEmpty(content));
        if (!TextUtils.isEmpty(content)) {
            userChat.setChat_content(StringUtil.formatFileMessage(content));
            userChat.setTime(new Date());
            userChat.setChat_reshow(true);
        } else if (messages.size() > 0) {
            userChat.setChat_content(messages.get(messages.size() - 1).getM_content());
            userChat.setTime(new Date(messages.get(messages.size() - 1).getM_send_time()));
        } else {
            userChat.setChat_reshow(false);
        }
        chatDao.update(userChat);
    }

    @Override
    public void getEntryText() {
        if (userChat != null && userChat.getInEntering()) {
            String content = userChat.getChat_content();
            mView.initEntryTextToView(content);
        }
    }

    @Override
    public void initUserChat(UserBean userBean) {
        //初始化整个chat聊天bean
        if (userChat == null) {
            if (LoginActivity.loginUser == null) {
                mView.loginFailed(null);
                return;
            }
            userChat = chatDao.queryBuilder()
                    .where(ChatDao.Properties.Source_id.eq(userBean.getU_id()))
                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                    .where(ChatDao.Properties.Chat_type.eq(0))
                    .unique();
            if (userChat == null) {
                userChat = new Chat();
                userChat.setChat_show_remind(true);
                userChat.setChat_up(false);
                userChat.setChat_type(0);
                userChat.setSource_g_id(0);
                userChat.setChat_reshow(false);
                userChat.setSource_id(userBean.getU_id());
                userChat.setObject_u_id(LoginActivity.loginUser.getU_id());
                chatDao.insert(userChat);
            }
        }
        if (!TextUtils.isEmpty(userChat.getBgImg())) {
            mView.initChat(userChat.getBgImg());
        }
    }

    @Override
    public void delMessage(UserBean userBean, int position) {
        messageDao.deleteByKey(messages.get(position).getM_id());
        getMessages(userBean, true);
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
    public void reSendMessage(Message message, UserBean userBean) {
        String fileContent = message.getM_content();
        userChat.setChat_reshow(true);
        userChat.setChat_content(message.getM_content());
        userChat.setChat_head(userBean.getU_head_img());
        userChat.setChat_m_count(0);
        userChat.setChat_name(userBean.getU_nick_name());
        userChat.setTime(new Date());
        chatDao.update(userChat);
        message.setM_send_time(new Date().getTime());
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        if (!NetworkUtils.isConnected(context) || loginToken == null) {
            mView.loginFailed(null);
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

        Subscription rxSubscription = flyMessageApi.sendUserMessage(fileUrl, loginToken, (int) LoginActivity.loginUser.getU_id(), Integer.parseInt(userBean.getU_id().toString()), message.getM_content_type(), message.getM_content()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SendMessageModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.loginFailed(null);
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
                            message.setIsSend(true);
                            message.setM_id((long) base.getMId());
                            message.setM_send_time(base.getTime());
                            message.setM_content(fileContent);
                            messageDao.update(message);
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
                            mView.loginFailed(null);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void addBlackList(UserBean userBean) {
        Subscription rxSubscription = flyMessageApi.checkBlackList(userBean.getU_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckBlackListModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CheckBlackListModel base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            Subscription rxSubscription;
                            if (base.inBlacklist) {
                                rxSubscription = flyMessageApi.delBlackList(userBean.getU_id()).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Base>() {
                                            @Override
                                            public void onCompleted() {
                                                mView.complete();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onNext(Base base) {
                                                if (base != null && mView != null && base.code == Constant.SUCCESS) {
                                                    mView.initBlackList(false);
                                                }
                                            }
                                        });
                            } else {
                                rxSubscription = flyMessageApi.addBlackList(userBean.getU_id()).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Base>() {
                                            @Override
                                            public void onCompleted() {
                                                mView.complete();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onNext(Base base) {
                                                if (base != null && mView != null && base.code == Constant.SUCCESS) {
                                                    mView.initBlackList(true);
                                                }
                                            }
                                        });
                            }
                            addSubscribe(rxSubscription);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void checkBlackList(UserBean userBean) {
        Subscription rxSubscription = flyMessageApi.checkBlackList(userBean.getU_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckBlackListModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CheckBlackListModel base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            mView.initBlackList(base.inBlacklist);
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
