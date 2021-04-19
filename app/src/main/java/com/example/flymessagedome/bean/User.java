package com.example.flymessagedome.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class User {
    private String u_name;
    private String u_sex;
    private long u_create_time;
    private int u_forbidden;
    private String u_nick_name;
    private String u_pass;
    private String u_sign;
    @Id(autoincrement = true)
    private long u_id;
    private String u_phone;
    private long u_brithday;
    private String u_head_img;
    private String u_position;
    private String u_bg_img;

    @Generated(hash = 812463302)
    public User(String u_name, String u_sex, long u_create_time, int u_forbidden,
            String u_nick_name, String u_pass, String u_sign, long u_id, String u_phone,
            long u_brithday, String u_head_img, String u_position, String u_bg_img) {
        this.u_name = u_name;
        this.u_sex = u_sex;
        this.u_create_time = u_create_time;
        this.u_forbidden = u_forbidden;
        this.u_nick_name = u_nick_name;
        this.u_pass = u_pass;
        this.u_sign = u_sign;
        this.u_id = u_id;
        this.u_phone = u_phone;
        this.u_brithday = u_brithday;
        this.u_head_img = u_head_img;
        this.u_position = u_position;
        this.u_bg_img = u_bg_img;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getU_sex() {
        return u_sex;
    }

    public void setU_sex(String u_sex) {
        this.u_sex = u_sex;
    }

    public long getU_create_time() {
        return u_create_time;
    }

    public void setU_create_time(long u_create_time) {
        this.u_create_time = u_create_time;
    }

    public int getU_forbidden() {
        return u_forbidden;
    }

    public void setU_forbidden(int u_forbidden) {
        this.u_forbidden = u_forbidden;
    }

    public String getU_nick_name() {
        return u_nick_name;
    }

    public void setU_nick_name(String u_nick_name) {
        this.u_nick_name = u_nick_name;
    }

    public String getU_pass() {
        return u_pass;
    }

    public void setU_pass(String u_pass) {
        this.u_pass = u_pass;
    }

    public String getU_sign() {
        return u_sign;
    }

    public void setU_sign(String u_sign) {
        this.u_sign = u_sign;
    }

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public String getU_phone() {
        return u_phone;
    }

    public void setU_phone(String u_phone) {
        this.u_phone = u_phone;
    }

    public long getU_brithday() {
        return u_brithday;
    }

    public void setU_brithday(long u_brithday) {
        this.u_brithday = u_brithday;
    }

    public String getU_head_img() {
        return u_head_img;
    }

    public void setU_head_img(String u_head_img) {
        this.u_head_img = u_head_img;
    }

    public String getU_position() {
        return u_position;
    }

    public void setU_position(String u_position) {
        this.u_position = u_position;
    }

    public String getU_bg_img() {
        return u_bg_img;
    }

    public void setU_bg_img(String u_bg_img) {
        this.u_bg_img = u_bg_img;
    }
}
