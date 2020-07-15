package com.example.flymessagedome.model;

import java.util.List;

public class BlackListModel extends Base {
    private List<BlackListsBean> blackLists;

    public List<BlackListsBean> getBlackLists() {
        return blackLists;
    }

    public void setBlackLists(List<BlackListsBean> blackLists) {
        this.blackLists = blackLists;
    }

    public static class BlackListsBean {
        /**
         * object_u_id : 8
         * time : 1591370944000
         * source_u_id : 1
         * user : {"nowDate":1591761286461,"u_name":"cf4f-4fae-9a26-027e18f12289","u_sex":"男","u_create_time":1589550476000,"u_nick_name":"罗福林","u_sign":"我不对你好了","u_id":8,"u_brithday":-28800000,"u_head_img":"http://www.foxluo.cn/FlyMessage/images/userHeads/20200606162019658.jpg","u_position":"四川省 成都市 武侯区","u_bg_img":"http://www.foxluo.cn/FlyMessage/images/userBgImg/20200606180210891.jpg"}
         * bl_id : 30
         */

        private int object_u_id;
        private long time;
        private int source_u_id;
        private UserBean user;
        private int bl_id;

        public int getObject_u_id() {
            return object_u_id;
        }

        public void setObject_u_id(int object_u_id) {
            this.object_u_id = object_u_id;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getSource_u_id() {
            return source_u_id;
        }

        public void setSource_u_id(int source_u_id) {
            this.source_u_id = source_u_id;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public int getBl_id() {
            return bl_id;
        }

        public void setBl_id(int bl_id) {
            this.bl_id = bl_id;
        }

        public static class UserBean {
            /**
             * nowDate : 1591761286461
             * u_name : cf4f-4fae-9a26-027e18f12289
             * u_sex : 男
             * u_create_time : 1589550476000
             * u_nick_name : 罗福林
             * u_sign : 我不对你好了
             * u_id : 8
             * u_brithday : -28800000
             * u_head_img : http://www.foxluo.cn/FlyMessage/images/userHeads/20200606162019658.jpg
             * u_position : 四川省 成都市 武侯区
             * u_bg_img : http://www.foxluo.cn/FlyMessage/images/userBgImg/20200606180210891.jpg
             */

            private long nowDate;
            private String u_name;
            private String u_sex;
            private long u_create_time;
            private String u_nick_name;
            private String u_sign;
            private int u_id;
            private int u_brithday;
            private String u_head_img;
            private String u_position;
            private String u_bg_img;

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

            public String getU_nick_name() {
                return u_nick_name;
            }

            public void setU_nick_name(String u_nick_name) {
                this.u_nick_name = u_nick_name;
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

            public int getU_brithday() {
                return u_brithday;
            }

            public void setU_brithday(int u_brithday) {
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
    }
}
