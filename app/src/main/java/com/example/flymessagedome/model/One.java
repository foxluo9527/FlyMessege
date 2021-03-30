package com.example.flymessagedome.model;

import java.util.List;

public class One extends Base {
    /**
     * {
     *   "code": 200,
     *   "msg": "success",
     *   "newslist": [
     *     {
     *       "oneid": 3140,
     *       "word": "感官上的成见，比起思想上或心理上的成见，因为更直接、原始、接近人类的根性，反而有着极其强顽的韧力。",
     *       "wordfrom": "",
     *       "imgurl": "http://image.wufazhuce.com/FsbMSJB9L5XEEQT6y3L28TtB3Mpo",
     *       "imgauthor": "",
     *       "date": "2021-03-29"
     *     }
     *   ]
     * }
     */
    private List<NewsListBean> newslist;

    public List<NewsListBean> getNewslist() {
        return newslist;
    }

    public void setNewslist(List<NewsListBean> newslist) {
        this.newslist = newslist;
    }

    public static class NewsListBean {
        /**
         * src : http://image.wufazhuce.com/FrLWJZi6cIbDVrNOQjvsz6wMW8wY
         * text : 我疯狂收集每一个快乐的瞬间，用他们回击每一个糟糕的日子。
         * day : VOL.2735
         2
         Apr 2020
         */
        private int oneid;
        private String imgurl;
        private String word;
        private String date;

        public int getOneid() {
            return oneid;
        }

        public void setOneid(int oneid) {
            this.oneid = oneid;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
