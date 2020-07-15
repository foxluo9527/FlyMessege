package com.example.flymessagedome.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupMember {

    /**
     * g_nick_name : 新用户
     * g_id : 5
     * id : 5
     * power : 1
     * u_id : 1
     * add_time : 1589805602000
     */

    private String g_nick_name;
    private long g_id;
    @Id
    private long id;
    private int power;
    private long u_id;
    private long add_time;
    private boolean isCreator;
    @Generated(hash = 1609406508)
    public GroupMember(String g_nick_name, long g_id, long id, int power, long u_id,
            long add_time, boolean isCreator) {
        this.g_nick_name = g_nick_name;
        this.g_id = g_id;
        this.id = id;
        this.power = power;
        this.u_id = u_id;
        this.add_time = add_time;
        this.isCreator = isCreator;
    }

    @Generated(hash = 1668463032)
    public GroupMember() {
    }

    public String getG_nick_name() {
        return g_nick_name;
    }

    public void setG_nick_name(String g_nick_name) {
        this.g_nick_name = g_nick_name;
    }

    public long getG_id() {
        return g_id;
    }

    public void setG_id(long g_id) {
        this.g_id = g_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public boolean getIsCreator() {
        return this.isCreator;
    }

    public void setIsCreator(boolean isCreator) {
        this.isCreator = isCreator;
    }
}
