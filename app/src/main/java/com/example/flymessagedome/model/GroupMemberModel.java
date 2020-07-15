package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.GroupMember;

public class GroupMemberModel extends Base{

    /**
     * group_member : {"g_nick_name":"123","g_id":6,"id":8,"power":0,"u_id":8,"add_time":1590152504000}
     */

    private GroupMember group_member;

    public GroupMember getGroup_member() {
        return group_member;
    }

    public void setGroup_member(GroupMember group_member) {
        this.group_member = group_member;
    }

}
