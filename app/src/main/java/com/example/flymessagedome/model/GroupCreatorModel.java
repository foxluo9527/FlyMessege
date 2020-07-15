package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.GroupBean;
import com.example.flymessagedome.bean.GroupMember;

public class GroupCreatorModel extends Base {
    /**
     * group_creator : {"g_nick_name":"新用户","g_id":7,"id":10,"power":1,"u_id":1,"add_time":1593173673000}
     */

    private GroupMember group_creator;

    public GroupMember getGroup_creator() {
        return group_creator;
    }

    public void setGroup_creator(GroupMember group_creator) {
        this.group_creator = group_creator;
    }
}
