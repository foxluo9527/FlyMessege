package com.example.flymessagedome.model;

import java.util.List;

public class HeadModel extends Base {
    private List<HeadsBean> heads;

    public List<HeadsBean> getHeads() {
        return heads;
    }

    public void setHeads(List<HeadsBean> heads) {
        this.heads = heads;
    }

    public static class HeadsBean {
        /**
         * h_id : 1
         * time : 1589472000000
         * u_id : 1
         * head_img_link : http://www.foxluo.cn/FlyMessage/images/userHeads/20200515180806874.JPG
         */

        private int h_id;
        private long time;
        private int u_id;
        private String head_img_link;

        public int getH_id() {
            return h_id;
        }

        public void setH_id(int h_id) {
            this.h_id = h_id;
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

        public String getHead_img_link() {
            return head_img_link;
        }

        public void setHead_img_link(String head_img_link) {
            this.head_img_link = head_img_link;
        }
    }
}
