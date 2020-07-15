package com.example.flymessagedome.ui.presenter;

import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.api.FlyMessageApi;
import com.example.flymessagedome.base.RxPresenter;
import com.example.flymessagedome.bean.Chat;
import com.example.flymessagedome.bean.ChatDao;
import com.example.flymessagedome.bean.Message;
import com.example.flymessagedome.bean.MessageDao;
import com.example.flymessagedome.model.Base;
import com.example.flymessagedome.model.MessageModel;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.UserChatActivity;
import com.example.flymessagedome.ui.contract.MessageRecordContract;
import com.example.flymessagedome.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessageRecordPresenter extends RxPresenter<MessageRecordContract.View> implements MessageRecordContract.Presenter<MessageRecordContract.View>{
    private MessageDao messageDao= FlyMessageApplication.getInstances().getDaoSession().getMessageDao();
    private ChatDao chatDao=FlyMessageApplication.getInstances().getDaoSession().getChatDao();
    private Chat userChat;
    private FlyMessageApi flyMessageApi;
    @Inject
    public MessageRecordPresenter(FlyMessageApi flyMessageApi) {
        this.flyMessageApi = flyMessageApi;
    }

    @Override
    public void getMessages(int pageSize, int pageIndex,long object_u_id) {
        userChat=chatDao.queryBuilder()
                .where(ChatDao.Properties.Source_id.eq(object_u_id))
                .where(ChatDao.Properties.Object_u_id.eq(LoginActivity.loginUser.getU_id()))
                .where(ChatDao.Properties.Chat_type.eq(0))
                .unique();
        Subscription rxSubscription = flyMessageApi.getRecordMessage(pageSize,pageIndex,object_u_id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageModel>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("获取聊天记录失败");
                    }

                    @Override

                    public void onNext(MessageModel messageModel) {
                        if (messageModel != null && mView != null && messageModel.code == Constant.SUCCESS) {
                            List<Message> messages=messageModel.getMessages();
                            for (Message message:
                                 messages) {
                                message.setLogin_u_id(LoginActivity.loginUser.getU_id());
                                message.setIsSend(true);
                                message.setCId(userChat.getC_id());
                                messageDao.insertOrReplace(message);
                            }
                            mView.initMessages((ArrayList<Message>) messages);
                            UserChatActivity.resultRefresh=true;
                        }else if (messageModel!=null){
                            mView.showError(messageModel.msg);
                        }else {
                            mView.showError("获取聊天记录失败");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void delMessage(Message message) {
        Subscription rxSubscription = flyMessageApi.delMessage(message.getM_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.showError("删除聊天记录失败");
                    }

                    @Override

                    public void onNext(Base base) {
                        if (base != null && mView != null && base.code == Constant.SUCCESS) {
                            messageDao.delete(message);
                            mView.complete();
                        }else if (base!=null){
                            mView.showError(base.msg+"M_Id:"+message.getM_id());
                        }else {
                            mView.showError("删除聊天记录失败");
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }
}
