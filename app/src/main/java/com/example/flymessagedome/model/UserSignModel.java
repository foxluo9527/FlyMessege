package com.example.flymessagedome.model;

import java.util.List;

public class UserSignModel extends Base {

    private List<SignBean> sign;

    public List<SignBean> getSign() {
        return sign;
    }

    public void setSign(List<SignBean> sign) {
        this.sign = sign;
    }

    public static class SignBean {
        /**
         * s_content : 1234
         * s_id : 2
         * time : 1589558274000
         * u_id : 1
         */

        private String s_content;
        private int s_id;
        private long time;
        private int u_id;

        public String getS_content() {
            return s_content;
        }

        public void setS_content(String s_content) {
            this.s_content = s_content;
        }

        public int getS_id() {
            return s_id;
        }

        public void setS_id(int s_id) {
            this.s_id = s_id;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getU_id() {
            return u_id;
        }

        public void setU_id(int u_id) {
            this.u_id = u_id;
        }
    }
}
