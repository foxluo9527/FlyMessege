package com.example.flymessagedome.model;

import com.example.flymessagedome.model.Base;

public class CheckBlackListModel extends Base {
    public boolean inBlacklist;

    public CheckBlackListModel() {
    }

    public CheckBlackListModel(boolean inBlacklist) {
        this.inBlacklist = inBlacklist;
    }

    public boolean isInBlacklist() {
        return inBlacklist;
    }

    public void setInBlacklist(boolean inBlacklist) {
        this.inBlacklist = inBlacklist;
    }
}
