package com.example.flymessagedome.ui.presenter;

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
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.bean.UserBeanDao;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.contract.SearchRecordContract;
import com.example.flymessagedome.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SearchRecordPresenter extends RxPresenter<SearchRecordContract.View> implements SearchRecordContract.Presenter<SearchRecordContract.View> {
    MessageDao messageDao = FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    FriendsBeanDao friendsBeanDao = FlyMessageApplication.getInstances().getDaoSession().getFriendsBeanDao();
    ChatDao chatDao = FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    UserBeanDao userBeanDao = FlyMessageApplication.getInstances().getDaoSession().getUserBeanDao();
    GroupBeanDao groupBeanDao = FlyMessageApplication.getInstances().getDaoSession().getGroupBeanDao();

    @Inject
    public SearchRecordPresenter(FlyMessageApi flyMessageApi) {
    }

    @Override
    public void getMessageList(String searchString) {
        ArrayList<Message> messages = (ArrayList<Message>) messageDao.queryBuilder()
                .where(MessageDao.Properties.M_type.eq(0))
                .where(MessageDao.Properties.M_content.like("%" + searchString + "%"))
                .list();
        ArrayList<Chat> temp = (ArrayList<Chat>) chatDao.queryBuilder()
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .orderDesc(ChatDao.Properties.Time).list();
        ArrayList<Chat> chats = new ArrayList<>();
        for (Chat c :
                temp) {
            chats.add(new Chat(c.getC_id(), c.getTime(), c.getChat_head(), c.getChat_name(),
                    c.getChat_content(), c.getChat_m_count(), c.getChat_show_remind(),
                    c.getChat_up(), c.getChat_reshow(), c.getObject_u_id(), c.getSource_id(),
                    c.getSource_g_id(), c.getChat_type(), c.getBgImg(), c.getInEntering(), c.getLastSendFailed()));
        }
        ArrayList<ArrayList<Message>> groupMessages = new ArrayList<>(chats.size());
        if (chats.size() > 0) {
            for (Chat ignored :
                    chats) {
                groupMessages.add(new ArrayList<>());
            }
        }
        for (int i = 0; i < groupMessages.size(); i++) {
            ArrayList<Message> ms = groupMessages.get(i);
            boolean contains = false;
            for (int j = 0; j < messages.size(); j++) {
                Message m = messages.get(j);
                if (chats.get(groupMessages.indexOf(ms)).getC_id() == m.getCId()) {
                    ms.add(m);
                    messages.remove(j);
                    j--;
                    contains = true;
                }
            }
            if (!contains || ms.size() == 0) {
                chats.remove(i);
                groupMessages.remove(i);
                i--;
            } else if (ms.size() == 1) {
                chats.get(groupMessages.indexOf(ms)).setChat_content(StringUtil.formatFileMessage(ms.get(0).getM_content()));
            } else {
                chats.get(groupMessages.indexOf(ms)).setChat_content(ms.size() + "条关于'" + searchString + "'相关记录");
            }
        }
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
        mView.initMessageList(groupMessages, chats);
    }
}
