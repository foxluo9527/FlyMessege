package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.GroupMember;

import java.util.List;

public class GroupMembersModel extends Base {
    private List<GroupMember> group_members;

    public List<GroupMember> getGroup_members() {
        return group_members;
    }

    public void setGroup_members(List<GroupMember> group_members) {
        this.group_members = group_members;
    }
}
