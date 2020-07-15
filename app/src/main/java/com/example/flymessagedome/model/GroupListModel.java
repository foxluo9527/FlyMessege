package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.GroupBean;

import java.util.List;

public class GroupListModel extends Base{
    private List<GroupBean> groups;

    public List<GroupBean> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupBean> groups) {
        this.groups = groups;
    }

}
