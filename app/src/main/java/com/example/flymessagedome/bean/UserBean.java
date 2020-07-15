package com.example.flymessagedome.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserBean {
    /**
     * nowDate : 1590370083705
     * u_name : 123
     * u_sex : 男
     * u_create_time : 1589343049000
     * u_nick_name : 新用户
     * isOnline : false
     * isFriend : false
     * u_sign : 哈哈哈哈
     * u_id : 1
     * u_brithday : 930499200000
     * u_head_img : http://www.foxluo.com/FlyMessage/images/defaultHead.png
     * isFriend : false
     * u_position : 123
     * u_bg_img : http://www.foxluo.cn/FlyMessage/images/userBgImg/20200521123701296.JPG
     */
    private String u_name;
    private String u_sex;
    private long u_create_time;
    private String u_nick_name;
    private boolean isOnline;
    private String u_sign;
    @Id(autoincrement = true)
    private Long u_id;
    private long u_brithday;
    private String u_head_img;
    private boolean isFriend;
    private String u_position;
    private String u_bg_img;
    private long f_id;

    @Generated(hash = 1095546480)
    public UserBean(String u_name, String u_sex, long u_create_time, String u_nick_name,
            boolean isOnline, String u_sign, Long u_id, long u_brithday, String u_head_img,
            boolean isFriend, String u_position, String u_bg_img, long f_id) {
        this.u_name = u_name;
        this.u_sex = u_sex;
        this.u_create_time = u_create_time;
        this.u_nick_name = u_nick_name;
        this.isOnline = isOnline;
        this.u_sign = u_sign;
        this.u_id = u_id;
        this.u_brithday = u_brithday;
        this.u_head_img = u_head_img;
        this.isFriend = isFriend;
        this.u_position = u_position;
        this.u_bg_img = u_bg_img;
        this.f_id = f_id;
    }
    @Generated(hash = 1203313951)
    public UserBean() {
    }
    public String getU_name() {
        return this.u_name;
    }
    public void setU_name(String u_name) {
        this.u_name = u_name;
    }
    public String getU_sex() {
        return this.u_sex;
    }
    public void setU_sex(String u_sex) {
        this.u_sex = u_sex;
    }
    public long getU_create_time() {
        return this.u_create_time;
    }
    public void setU_create_time(long u_create_time) {
        this.u_create_time = u_create_time;
    }
    public String getU_nick_name() {
        return this.u_nick_name;
    }
    public void setU_nick_name(String u_nick_name) {
        this.u_nick_name = u_nick_name;
    }
    public boolean getIsOnline() {
        return this.isOnline;
    }
    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
    public String getU_sign() {
        return this.u_sign;
    }
    public void setU_sign(String u_sign) {
        this.u_sign = u_sign;
    }
    public Long getU_id() {
        return this.u_id;
    }
    public void setU_id(Long u_id) {
        this.u_id = u_id;
    }
    public long getU_brithday() {
        return this.u_brithday;
    }
    public void setU_brithday(long u_brithday) {
        this.u_brithday = u_brithday;
    }
    public String getU_head_img() {
        return this.u_head_img;
    }
    public void setU_head_img(String u_head_img) {
        this.u_head_img = u_head_img;
    }
    public boolean getIsFriend() {
        return this.isFriend;
    }
    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }
    public String getU_position() {
        return this.u_position;
    }
    public void setU_position(String u_position) {
        this.u_position = u_position;
    }
    public String getU_bg_img() {
        return this.u_bg_img;
    }
    public void setU_bg_img(String u_bg_img) {
        this.u_bg_img = u_bg_img;
    }
    public long getF_id() {
        return this.f_id;
    }
    public void setF_id(long f_id) {
        this.f_id = f_id;
    }
    
}