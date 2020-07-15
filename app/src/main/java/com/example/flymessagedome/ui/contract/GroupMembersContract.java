package com.example.flymessagedome.ui.contract;

import android.content.Context;

import com.example.flymessagedome.base.BaseContract;
import com.example.flymessagedome.bean.GroupMember;
import com.example.flymessagedome.model.BlackListModel;

import java.util.ArrayList;

public interface GroupMembersContract {
    interface View extends BaseContract.BaseView{
        void initGroupMember(ArrayList<GroupMember> groupMembers,GroupMember creator);
        void initDelGroupMember(int now,int sum);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getGroupMembers(long g_id, Context context);
        void delGroupMember(ArrayList<GroupMember> groupMembers);
    }
}
