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
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.FriendsBeanDao;
import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupBeanDao;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.bean.GroupMemberDao;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.model.GroupMemberModel;
import com.example.flymessagedome.model.GroupModel;
import com.example.flymessagedome.model.Login;
import com.example.flymessagedome.model.MessageModel;
import com.example.flymessagedome.model.Users;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.adapter.ChatAdapter;
import com.example.flymessagedome.ui.contract.ChatContract;
import com.example.flymessagedome.ui.fragment.MessageFragment;
import com.example.flymessagedome.utils.Constant;
import com.example.flymessagedome.utils.HttpRequest;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.utils.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChatFragmentPresenter extends RxPresenter<ChatContract.View> implements ChatContract.Presenter<ChatContract.View> {
    private final FlyMessageApi flyMessageApi;
    public Context context;
    List<Message> messages = null;
    ChatDao chatDao = FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    UserBeanDao userBeanDao = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();
    FriendsBeanDao friendsBeanDao = FlyMessageApplication.getInstances().getDaoSession().getFriendsBeanDao();
    GroupBeanDao groupBeanDao = FlyMessageApplication.getInstances().getDaoSession().getGroupBeanDao();
    GroupMemberDao groupMemberDao = FlyMessageApplication.getInstances().getDaoSession().getGroupMemberDao();
    MessageDao messageDao = FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    public Users users = null;
    public GroupModel groupModel = null;
    public GroupMemberModel groupMember = null;

    @Inject
    public ChatFragmentPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getChatList(Context context) {
        this.context = context;
        if (LoginActivity.loginUser == null) {
            mView.loginFailed(null);
            return;
        }
        ArrayList<Chat> chats;
        chats = (ArrayList<Chat>) chatDao.queryBuilder()
                .where(ChatDao.Properties.Chat_up.eq(true))
                .where(ChatDao.Properties.Chat_reshow.eq(true))
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .orderDesc(ChatDao.Properties.Time).list();
        chats.addAll((ArrayList<Chat>) chatDao.queryBuilder()
                .where(ChatDao.Properties.Chat_up.eq(false))
                .where(ChatDao.Properties.Chat_reshow.eq(true))
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .orderDesc(ChatDao.Properties.Time).list());
        for (Chat chat :
                chats) {
            if (chat.getChat_type() == 0) {
                List<FriendsBean> friendsBeans = friendsBeanDao.queryBuilder().where(FriendsBeanDao.Properties.F_object_u_id.eq(chat.getSource_id())).list();
                if (friendsBeans.size() > 0) {
                    FriendsBean friendsBean = friendsBeans.get(0);
                    if (friendsBean != null && friendsBean.getFriendUser() != null) {
                        chat.setChat_head(friendsBean.getFriendUser().getU_head_img());
                        if (!TextUtils.isEmpty(friendsBean.getF_remarks_name())) {
                            chat.setChat_name(friendsBean.getF_remarks_name());
                        } else {
                            chat.setChat_name(friendsBean.getFriendUser().getU_nick_name());
                        }
                    }
                } else {
                    UserBean userBean = userBeanDao.load(chat.getSource_id());
                    if (userBean != null) {
                        chat.setChat_head(userBean.getU_head_img());
                        chat.setChat_name(userBean.getU_nick_name());
                    }
                }
            } else {
                GroupBean groupBean = groupBeanDao.load(chat.getSource_g_id());
                chat.setChat_head(groupBean.getG_head_img());
                chat.setChat_name(groupBean.getG_name());
            }
        }
        ChatAdapter adapter = new ChatAdapter(chats, context);
        MessageFragment.chats = chats;
        mView.initChatList(adapter);
    }

    @Override
    public void delChatList(Chat chat, boolean delMessage) {
        if (delMessage) {
            //完全删除
            messageDao.queryBuilder().where(MessageDao.Properties.CId.eq(chat.getC_id())).buildDelete().executeDeleteWithoutDetachingEntities();
        }
        //下条消息出现前不再展示
        chat.setChat_reshow(false);
        chatDao.update(chat);
        getChatList(context);
    }

    @Override
    public void upChatList(Chat chat) {
        chat.setChat_up(true);
        chatDao.update(chat);
        getChatList(context);
    }

    @Override
    public void cancelUpChat(Chat chat) {
        chat.setChat_up(false);
        chatDao.update(chat);
        getChatList(context);
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

    int nowMessageIndex = 0;

    @Override
    public void getMessage(int pageSize, int pageNum) {
        messages = null;
        String loginToken = SharedPreferencesUtil.getInstance().getString("loginToken");
        if (loginToken != null) {
            Subscription rxSubscription = flyMessageApi.getMessage(loginToken, pageSize, pageNum).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MessageModel>() {
                        @Override
                        public void onCompleted() {
                            mView.complete();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mView.loginFailed(null);
                            messages = null;
                        }

                        @SuppressLint("StaticFieldLeak")
                        @Override
                        public void onNext(MessageModel messageModel) {
                            if (messageModel != null && mView != null) {
                                switch (messageModel.code) {
                                    case Constant.FAILED:
                                        mView.showError(messageModel.msg);
                                        break;
                                    case Constant.NOT_LOGIN:
                                    case Constant.TOKEN_EXCEED:
                                        String u_name = SharedPreferencesUtil.getInstance().getString(Constant.U_NAME);
                                        String u_pass = SharedPreferencesUtil.getInstance().getString(Constant.U_PASS);
                                        if (u_name != null && u_pass != null) {
                                            login(u_name, u_pass);
                                        } else {
                                            mView.loginFailed(null);
                                            messages = null;
                                        }
                                        break;
                                    case Constant.SUCCESS:
                                        messages = messageModel.getMessages();
                                        nowMessageIndex = 0;
                                        for (Message message :
                                                messageModel.getMessages()) {
                                            message.setM_id(null);
                                            message.setIsSend(true);
                                            message.setLogin_u_id(LoginActivity.loginUser.getU_id());
                                            new AsyncTask<Void, Void, Void>() {
                                                @Override
                                                protected Void doInBackground(Void... voids) {
                                                    groupMember = null;
                                                    if (userBeanDao.load((long) message.getM_source_id()) == null) {
                                                        users = HttpRequest.getUserMsg(message.getM_source_id(), loginToken);
                                                    }
                                                    if (message.getM_type() == 1 && groupBeanDao.load((long) message.getM_source_g_id()) == null) {
                                                        groupModel = HttpRequest.getGroupMsg(message.getM_source_g_id(), loginToken);
                                                        if (groupMemberDao.queryBuilder()
                                                                .where(GroupMemberDao.Properties.G_id.eq(message.getM_source_g_id()))
                                                                .where(GroupMemberDao.Properties.U_id.eq(message.getM_source_id()))
                                                                .list().size() == 0) {
                                                            groupMember = HttpRequest.getGroupMember(message.getM_source_g_id(), message.getM_source_id(), loginToken);
                                                        }
                                                    }
                                                    return null;
                                                }

                                                @Override
                                                protected void onPostExecute(Void aVoid) {
                                                    try {
                                                        if (users != null && users.code == Constant.SUCCESS) {
                                                            userBeanDao.insertOrReplace(users.getUser());
                                                            users = null;
                                                        }
                                                        if (groupModel != null && groupModel.code == Constant.SUCCESS) {
                                                            groupBeanDao.insertOrReplace(groupModel.getGroup());
                                                            groupModel = null;
                                                            if (groupMember != null && groupMember.code == Constant.SUCCESS) {
                                                                GroupMember groupMember1 = groupMember.getGroup_member();
                                                                groupMemberDao.insertOrReplace(groupMember1);
                                                            }
                                                        }
                                                        String content = StringUtil.formatFileMessage(message.getM_content());
                                                        UserBean userBean = userBeanDao.load((long) message.getM_source_id());
                                                        if (userBean == null) {
                                                            return;
                                                        }
                                                        if (message.getM_type() == 0) {//用户消息
                                                            //添加或更新消息列表
                                                            List<Message> messages = messageDao.queryBuilder()
                                                                    .where(MessageDao.Properties.M_source_id.eq(message.getM_source_id()))
                                                                    .where(MessageDao.Properties.M_type.eq(0))
                                                                    .where(MessageDao.Properties.M_receive_state.eq(0))
                                                                    .list();
                                                            Chat userChat = chatDao.queryBuilder()
                                                                    .where(ChatDao.Properties.Chat_type.eq(0))
                                                                    .where(ChatDao.Properties.Source_id.eq(userBean.getU_id()))
                                                                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                                                                    .unique();
                                                            if (userChat == null) {
                                                                userChat = new Chat();
                                                                userChat.setChat_show_remind(true);
                                                                userChat.setChat_up(false);
                                                                userChat.setChat_type(0);
                                                                userChat.setSource_g_id(0);
                                                                userChat.setSource_id(userBean.getU_id());
                                                                userChat.setObject_u_id(LoginActivity.loginUser.getU_id());
                                                            }
                                                            userChat.setChat_reshow(true);
                                                            userChat.setChat_content(content);
                                                            userChat.setChat_head(userBean.getU_head_img());
                                                            userChat.setChat_m_count(messages.size() + 1);
                                                            userChat.setChat_name(userBean.getU_nick_name());
                                                            userChat.setTime(new Date(message.getM_send_time()));
                                                            chatDao.insertOrReplace(userChat);
                                                            userChat = chatDao.queryBuilder()
                                                                    .where(ChatDao.Properties.Chat_type.eq(0))
                                                                    .where(ChatDao.Properties.Source_id.eq(userBean.getU_id()))
                                                                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                                                                    .unique();
                                                            message.setCId(userChat.getC_id());
                                                            messageDao.insertOrReplace(message);
                                                        } else {//群聊消息
                                                            List<Message> messages = messageDao.queryBuilder()
                                                                    .where(MessageDao.Properties.M_type.eq(1))
                                                                    .where(MessageDao.Properties.M_receive_state.eq(0))
                                                                    .where(MessageDao.Properties.M_source_g_id.eq(message.getM_source_g_id()))
                                                                    .list();
                                                            GroupBean groupBean = groupBeanDao.load((long) message.getM_source_g_id());
                                                            if (groupBean == null) {
                                                                return;
                                                            }
                                                            if (groupMember != null && groupMember.code == Constant.SUCCESS) {
                                                                content = groupMember.getGroup_member().getG_nick_name() + ":" + content;
                                                                groupMember = null;
                                                            } else {
                                                                content = userBean.getU_nick_name() + ":" + content;
                                                            }
                                                            Chat userChat = chatDao.queryBuilder()
                                                                    .where(ChatDao.Properties.Chat_type.eq(1))
                                                                    .where(ChatDao.Properties.Source_g_id.eq(groupBean.getG_id()))
                                                                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                                                                    .unique();
                                                            if (userChat == null) {
                                                                userChat = new Chat();
                                                                userChat.setChat_show_remind(true);
                                                                userChat.setChat_up(false);
                                                                userChat.setChat_type(1);
                                                                userChat.setSource_id(0);
                                                                userChat.setSource_g_id(groupBean.getG_id());
                                                                userChat.setObject_u_id(LoginActivity.loginUser.getU_id());
                                                            }
                                                            userChat.setChat_reshow(true);
                                                            userChat.setChat_content(content);
                                                            userChat.setChat_head(groupBean.getG_head_img());
                                                            userChat.setChat_m_count(messages.size() + 1);
                                                            userChat.setChat_name(groupBean.getG_name());
                                                            userChat.setTime(new Date(message.getM_send_time()));
                                                            chatDao.insertOrReplace(userChat);
                                                            userChat = chatDao.queryBuilder()
                                                                    .where(ChatDao.Properties.Chat_type.eq(1))
                                                                    .where(ChatDao.Properties.Source_g_id.eq(groupBean.getG_id()))
                                                                    .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                                                                    .unique();
                                                            message.setCId(userChat.getC_id());
                                                            messageDao.insertOrReplace(message);
                                                        }
                                                        nowMessageIndex++;
                                                        if (nowMessageIndex == messageModel.getMessages().size()) {
                                                            getChatList(context);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }.execute();
                                        }
                                        if (messages != null && messages.size() == 20) {
                                            getMessage(20, pageNum + 1);
                                        } else {
                                            getChatList(context);
                                        }
                                        break;
                                }
                            } else {
                                mView.loginFailed(null);
                            }
                        }
                    });
            addSubscribe(rxSubscription);
        }
    }

}
