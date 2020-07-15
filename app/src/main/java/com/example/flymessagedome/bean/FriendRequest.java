package com.example.flymessagedome.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class FriendRequest {

    /**
     * rq_id : 6
     * rq_object_u_id : 8
     * rq_source_u_id : 1
     * isAccept : false
     * rq_remarks_name : 新朋友
     * rq_content : 加个好友吧
     * rq_receive_state : 0
     * time : 1590161842517
     */
    @Id(autoincrement = true)
    private long rq_id;
    private int rq_object_u_id;
    private int rq_source_u_id;
    private boolean isAccept;
    private String rq_remarks_name;
    private String rq_content;
    private int rq_receive_state;
    private long time;

    @Generated(hash = 206428142)
    public FriendRequest(long rq_id, int rq_object_u_id, int rq_source_u_id,
            boolean isAccept, String rq_remarks_name, String rq_content,
            int rq_receive_state, long time) {
        this.rq_id = rq_id;
        this.rq_object_u_id = rq_object_u_id;
        this.rq_source_u_id = rq_source_u_id;
        this.isAccept = isAccept;
        this.rq_remarks_name = rq_remarks_name;
        this.rq_content = rq_content;
        this.rq_receive_state = rq_receive_state;
        this.time = time;
    }

    @Generated(hash = 1677678717)
    public FriendRequest() {
    }

    public long getRq_id() {
        return rq_id;
    }

    public void setRq_id(long rq_id) {
        this.rq_id = rq_id;
    }

    public int getRq_object_u_id() {
        return rq_object_u_id;
    }

    public void setRq_object_u_id(int rq_object_u_id) {
        this.rq_object_u_id = rq_object_u_id;
    }

    public int getRq_source_u_id() {
        return rq_source_u_id;
    }

    public void setRq_source_u_id(int rq_source_u_id) {
        this.rq_source_u_id = rq_source_u_id;
    }

    public boolean isIsAccept() {
        return isAccept;
    }

    public void setIsAccept(boolean isAccept) {
        this.isAccept = isAccept;
    }

    public String getRq_remarks_name() {
        return rq_remarks_name;
    }

    public void setRq_remarks_name(String rq_remarks_name) {
        this.rq_remarks_name = rq_remarks_name;
    }

    public String getRq_content() {
        return rq_content;
    }

    public void setRq_content(String rq_content) {
        this.rq_content = rq_content;
    }

    public int getRq_receive_state() {
        return rq_receive_state;
    }

    public void setRq_receive_state(int rq_receive_state) {
        this.rq_receive_state = rq_receive_state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean getIsAccept() {
        return this.isAccept;
    }
}
