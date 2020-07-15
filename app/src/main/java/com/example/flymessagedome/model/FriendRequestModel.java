package com.example.flymessagedome.model;

import java.util.List;

public class FriendRequestModel extends Base {

    private List<FriendRequestsBean> friendRequests;

    public List<FriendRequestsBean> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<FriendRequestsBean> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public static class FriendRequestsBean {
        /**
         * rq_id : 12
         * rq_object_u_id : 1
         * rq_source_u_id : 8
         * rqUser : {"nowDate":1591403041219,"u_name":"cf4f-4fae-9a26-027e18f12289","u_sex":"男","u_create_time":1589550476000,"u_nick_name":"l","isOnline":false,"u_sign":"我就不信","u_id":8,"u_head_img":"http://www.foxluo.cn/FlyMessage/images/defaultHead.png","isFriend":true,"f_id":0,"u_bg_img":"http://www.foxluo.cn/FlyMessage/bg/defaultBg.png"}
         * isAccept : true
         * rq_remarks_name : 新朋友
         * rq_content : 加个好友吧
         * rq_receive_state : 1
         * time : 1590664148000
         */

        private int rq_id;
        private int rq_object_u_id;
        private int rq_source_u_id;
        private RqUserBean rqUser;
        private boolean isAccept;
        private String rq_remarks_name;
        private String rq_content;
        private int rq_receive_state;
        private long time;

        public int getRq_id() {
            return rq_id;
        }

        public void setRq_id(int rq_id) {
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

        public RqUserBean getRqUser() {
            return rqUser;
        }

        public void setRqUser(RqUserBean rqUser) {
            this.rqUser = rqUser;
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

        public static class RqUserBean {
            /**
             * nowDate : 1591403041219
             * u_name : cf4f-4fae-9a26-027e18f12289
             * u_sex : 男
             * u_create_time : 1589550476000
             * u_nick_name : l
             * isOnline : false
             * u_sign : 我就不信
             * u_id : 8
             * u_head_img : http://www.foxluo.cn/FlyMessage/images/defaultHead.png
             * isFriend : true
             * f_id : 0
             * u_bg_img : http://www.foxluo.cn/FlyMessage/bg/defaultBg.png
             */

            private long nowDate;
            private String u_name;
            private String u_sex;
            private long u_create_time;
            private String u_nick_name;
            private boolean isOnline;
            private String u_sign;
            private int u_id;
            private String u_head_img;
            private boolean isFriend;
            private int f_id;
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

            public int getF_id() {
                return f_id;
            }

            public void setF_id(int f_id) {
                this.f_id = f_id;
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
