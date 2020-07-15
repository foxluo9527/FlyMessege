package com.example.flymessagedome.model;

import java.util.List;

public class One extends Base {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * src : http://image.wufazhuce.com/FrLWJZi6cIbDVrNOQjvsz6wMW8wY
         * text : 我疯狂收集每一个快乐的瞬间，用他们回击每一个糟糕的日子。
         * day : VOL.2735
         2
         Apr 2020
         */

        private String src;
        private String text;
        private String day;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }
}
