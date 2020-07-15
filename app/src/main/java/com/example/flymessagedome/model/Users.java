package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.UserBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

public class Users extends Base {

    /**
     * user : {"nowDate":1590370083705,"u_name":"123","u_sex":"男","u_create_time":1589343049000,"u_nick_name":"新用户","isOnline":false,"u_sign":"哈哈哈哈","u_id":1,"u_brithday":930499200000,"u_head_img":"http://www.foxluo.com/FlyMessage/images/defaultHead.png","isFriend":false,"u_position":"123","u_bg_img":"http://www.foxluo.cn/FlyMessage/images/userBgImg/20200521123701296.JPG"}
     */

    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
