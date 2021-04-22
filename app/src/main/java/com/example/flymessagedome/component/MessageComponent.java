package com.example.flymessagedome.component;

import androidx.fragment.app.FragmentActivity;

import com.example.flymessagedome.service.MessageService;
import com.example.flymessagedome.ui.activity.AddPostActivity;
import com.example.flymessagedome.ui.activity.AddSignActivity;
import com.example.flymessagedome.ui.activity.BlackListActivity;
import com.example.flymessagedome.ui.activity.ContractAddActivity;
import com.example.flymessagedome.ui.activity.CreateGroupActivity;
import com.example.flymessagedome.ui.activity.EditGroupMsgActivity;
import com.example.flymessagedome.ui.activity.EditPostActivity;
import com.example.flymessagedome.ui.activity.EditUserMsgActivity;
import com.example.flymessagedome.ui.activity.FriendRequestActivity;
import com.example.flymessagedome.ui.activity.GroupChatActivity;
import com.example.flymessagedome.ui.activity.GroupsActivity;
import com.example.flymessagedome.ui.activity.LoginActivity;
import com.example.flymessagedome.ui.activity.LoginUserMsgActivity;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.ui.activity.MessageRecordActivity;
import com.example.flymessagedome.ui.activity.SearchActivity;
import com.example.flymessagedome.ui.activity.SearchFriendActivity;
import com.example.flymessagedome.ui.activity.SearchGroupResultActivity;
import com.example.flymessagedome.ui.activity.SearchRecordActivity;
import com.example.flymessagedome.ui.activity.SearchResultActivity;
import com.example.flymessagedome.ui.activity.ShowGroupMembersActivity;
import com.example.flymessagedome.ui.activity.ShowHistorySignActivity;
import com.example.flymessagedome.ui.activity.ShowLoginHeadActivity;
import com.example.flymessagedome.ui.activity.ShowPostActivity;
import com.example.flymessagedome.ui.activity.UserChatActivity;
import com.example.flymessagedome.ui.activity.UserCommunityActivity;
import com.example.flymessagedome.ui.activity.WebActivity;
import com.example.flymessagedome.ui.activity.WelcomeActivity;
import com.example.flymessagedome.ui.fragment.CommunityFragment;
import com.example.flymessagedome.ui.fragment.FriendFragment;
import com.example.flymessagedome.ui.fragment.MessageFragment;
import com.example.flymessagedome.ui.fragment.MineFragment;

import dagger.Component;

@Component(dependencies = AppComponent.class)
public interface MessageComponent {
    LoginActivity inject(LoginActivity loginActivity);
    WelcomeActivity inject(WelcomeActivity welcomeActivity);
    MessageService inject(MessageService messageService);
    MainActivity inject(MainActivity mainActivity);
    MessageFragment inject(MessageFragment messageFragment);
    UserChatActivity inject(UserChatActivity userChatActivity);
    GroupChatActivity inject(GroupChatActivity groupChatActivity);
    FriendFragment inject(FriendFragment friendFragment);
    CommunityFragment inject(CommunityFragment communityFragment);
    SearchResultActivity inject(SearchResultActivity searchResultActivity);
    FriendRequestActivity inject(FriendRequestActivity friendRequestActivity);
    EditUserMsgActivity inject(EditUserMsgActivity editUserMsgActivity);
    BlackListActivity inject(BlackListActivity blackListActivity);
    LoginUserMsgActivity inject(LoginUserMsgActivity loginUserMsgActivity);
    AddSignActivity inject(AddSignActivity addSignActivity);
    ShowHistorySignActivity inject(ShowHistorySignActivity showHistorySignActivity);
    ShowLoginHeadActivity inject(ShowLoginHeadActivity showLoginHeadActivity);
    MessageRecordActivity inject(MessageRecordActivity messageRecordActivity);
    SearchActivity inject(SearchActivity searchActivity);
    SearchFriendActivity inject(SearchFriendActivity searchFriendActivity);
    SearchRecordActivity inject(SearchRecordActivity searchRecordActivity);
    GroupsActivity inject(GroupsActivity groupsActivity);
    CreateGroupActivity inject(CreateGroupActivity createGroupActivity);
    SearchGroupResultActivity inject(SearchGroupResultActivity searchGroupResultActivity);
    EditGroupMsgActivity inject(EditGroupMsgActivity editGroupMsgActivity);
    ShowGroupMembersActivity inject(ShowGroupMembersActivity showGroupMembersActivity);
    ContractAddActivity inject(ContractAddActivity contractAddActivity);
    AddPostActivity inject(AddPostActivity addPostActivity);
    EditPostActivity inject(EditPostActivity editPostActivity);
    ShowPostActivity inject(ShowPostActivity showPostActivity);
    UserCommunityActivity inject(UserCommunityActivity userCommunityActivity);
}
