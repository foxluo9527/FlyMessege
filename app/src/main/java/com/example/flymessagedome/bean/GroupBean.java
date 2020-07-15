package com.example.flymessagedome.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupBean implements Parcelable {

    /**
     * g_create_time : 1590137700000
     * g_name : 测试群聊
     * g_id : 6
     * g_num : dce6a25a-b4af-4014-b94d-d417f88d6a0f
     * g_head_img : http://www.foxluo.cn/FlyMessage/images/defaultGroupHead.png
     * g_introduce : 群聊简介
     */

    private long g_create_time;
    private String g_name;
    @Id
    private long g_id;
    private String g_num;
    private String g_head_img;
    private String g_introduce;
    private long login_u_id;
    private boolean isCreater;
    private boolean isMember;
    //    Deserialization
    protected GroupBean(Parcel in) {
        g_create_time=in.readLong();
        g_name=in.readString();
        g_id=in.readLong();
        g_num = in.readString();
        g_head_img = in.readString();
        g_introduce = in.readString();
        login_u_id = in.readLong();
        isCreater=in.readBoolean();
        isMember=in.readBoolean();
    }
    @Generated(hash = 1719888048)
    public GroupBean(long g_create_time, String g_name, long g_id, String g_num,
            String g_head_img, String g_introduce, long login_u_id,
            boolean isCreater, boolean isMember) {
        this.g_create_time = g_create_time;
        this.g_name = g_name;
        this.g_id = g_id;
        this.g_num = g_num;
        this.g_head_img = g_head_img;
        this.g_introduce = g_introduce;
        this.login_u_id = login_u_id;
        this.isCreater = isCreater;
        this.isMember = isMember;
    }

    @Generated(hash = 405578774)
    public GroupBean() {
    }

    public long getG_create_time() {
        return g_create_time;
    }

    public void setG_create_time(long g_create_time) {
        this.g_create_time = g_create_time;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public long getG_id() {
        return g_id;
    }

    public void setG_id(long g_id) {
        this.g_id = g_id;
    }

    public String getG_num() {
        return g_num;
    }

    public void setG_num(String g_num) {
        this.g_num = g_num;
    }

    public String getG_head_img() {
        return g_head_img;
    }

    public void setG_head_img(String g_head_img) {
        this.g_head_img = g_head_img;
    }

    public String getG_introduce() {
        return g_introduce;
    }

    public void setG_introduce(String g_introduce) {
        this.g_introduce = g_introduce;
    }


    public boolean getIsCreater() {
        return this.isCreater;
    }

    public void setIsCreater(boolean isCreater) {
        this.isCreater = isCreater;
    }

    public boolean getIsMember() {
        return this.isMember;
    }

    public void setIsMember(boolean isMember) {
        this.isMember = isMember;
    }

    public long getLogin_u_id() {
        return this.login_u_id;
    }

    public void setLogin_u_id(long login_u_id) {
        this.login_u_id = login_u_id;
    }
    public static final Creator<GroupBean> CREATOR = new Creator<GroupBean>() {
        @Override
        public GroupBean createFromParcel(Parcel in) {
            return new GroupBean(in);
        }

        @Override
        public GroupBean[] newArray(int size) {
            return new GroupBean[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(g_create_time);
        dest.writeString(g_name);
        dest.writeLong(g_id);
        dest.writeString(g_num);
        dest.writeString(g_head_img);
        dest.writeString(g_introduce);
        dest.writeLong(login_u_id);
        dest.writeBoolean(isCreater);
        dest.writeBoolean(isMember);
    }
}