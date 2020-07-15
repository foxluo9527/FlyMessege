package com.example.flymessagedome.model;

import java.util.List;

public class SearchUserModel extends Base {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * nowDate : 1590025073781
         * u_name : cf4f-4fae-9a26-027e18f12289
         * u_create_time : 1589550476000
         * u_nick_name : 123
         * isOnline : false
         * u_sign : 哈哈哈哈
         * u_id : 8
         * u_head_img : http://www.foxluo.com/FlyMessage/head/defaultHead.png
         * isFriend : false
         * u_bg_img : http://www.foxluo.com/FlyMessage/bg/defaultBg.png
         */

        private long nowDate;
        private String u_name;
        private long u_create_time;
        private String u_nick_name;
        private boolean isOnline;
        private String u_sign;
        private int u_id;
        private String u_head_img;
        private boolean isFriend;
        private String u_bg_img;
        private String u_position;
        private String u_sex;

        public boolean isOnline() {
            return isOnline;
        }

        public void setOnline(boolean online) {
            isOnline = online;
        }

        public boolean isFriend() {
            return isFriend;
        }

        public void setFriend(boolean friend) {
            isFriend = friend;
        }

        public String getU_position() {
            return u_position;
        }

        public void setU_position(String u_position) {
            this.u_position = u_position;
        }

        public String getU_sex() {
            return u_sex;
        }

        public void setU_sex(String u_sex) {
            this.u_sex = u_sex;
        }

        public long getNowDate() {
            return nowDate;
        }

        public void setNowDate(long nowDate) {
            this.nowDate = nowDate;
        }

        public String getU_name() {
            return u_name;
        }

        public void setU_name(String u_name) {
            this.u_name = u_name;
        }

        public long getU_create_time() {
            return u_create_time;
        }

        public void setU_create_time(long u_create_time) {
            this.u_create_time = u_create_time;
        }

        public String getU_nick_name() {
            return u_nick_name;
        }

        public void setU_nick_name(String u_nick_name) {
            this.u_nick_name = u_nick_name;
        }

        public boolean isIsOnline() {
            return isOnline;
        }

        public void setIsOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

        public String getU_sign() {
            return u_sign;
        }

        public void setU_sign(String u_sign) {
            this.u_sign = u_sign;
        }

        public int getU_id() {
            return u_id;
        }

        public void setU_id(int u_id) {
            this.u_id = u_id;
        }

        public String getU_head_img() {
            return u_head_img;
        }

        public void setU_head_img(String u_head_img) {
            this.u_head_img = u_head_img;
        }

        public boolean isIsFriend() {
            return isFriend;
        }

        public void setIsFriend(boolean isFriend) {
            this.isFriend = isFriend;
        }

        public String getU_bg_img() {
            return u_bg_img;
        }

        public void setU_bg_img(String u_bg_img) {
            this.u_bg_img = u_bg_img;
        }
    }
}
