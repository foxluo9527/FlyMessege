package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.GroupBean;

public class GroupModel extends Base {

    /**
     * groupModel : {"g_create_time":1589805602000,"g_name":"测试群聊创建","g_id":5,"g_num":"f5ba3923-734b-4307-aaee-50c1c3931546","g_introduce":"第一个测试的群聊"}
     */

    private GroupBean group;

    public GroupBean getGroup() {
        return group;
    }

    public void setGroup(GroupBean group) {
        this.group = group;
    }

}
