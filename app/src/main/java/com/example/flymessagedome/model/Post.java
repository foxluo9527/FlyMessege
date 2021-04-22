package com.example.flymessagedome.model;

import java.util.List;

public class Post extends Base{

    /**
     * post : {"u_name":"f1c5-4d5c-b27e-70ea802ae3ca","comments":[{"create_time":1618998321000,"hot_value":1,"replies":[{"reply_u_name":"新用户","create_time":1618998471000,"send_u_head":"http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg","send_u_name":"新用户","community_post_comment_id":1,"community_post_comment_reply_id":1,"send_u_id":34,"reply_content":"我也踩一踩","reply_u_id":0}],"send_u_head":"http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg","send_u_name":"新用户","zan_num":0,"community_post_comment_id":1,"community_post_id":30,"send_u_id":0,"state":1,"community_post_comment_content":"踩一踩"}],"community_post_id":30,"u_nick_name":"新用户","communityPostState":1,"postItems":[{"community_post_id":30,"community_post_item_id":13,"community_post_item_type":0,"community_post_item_url":"http://www.foxluo.cn/FlyMessage/file/postFile/20210421165926748.jpg"},{"community_post_id":30,"community_post_item_id":14,"community_post_item_type":0,"community_post_item_url":"http://www.foxluo.cn/FlyMessage/file/postFile/20210421165927724.JPG"},{"community_post_id":30,"community_post_item_id":15,"community_post_item_type":0,"community_post_item_url":"http://www.foxluo.cn/FlyMessage/file/postFile/20210421165928426.jpg"}],"zanCount":0,"commentCount":1,"shareCount":0,"showCount":3,"u_id":34,"zans":[],"createTime":1618995566000,"u_head":"http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg","zan_state":0,"communityPostContent":"三张图片"}
     */

    private PostBean post;

    public PostBean getPost() {
        return post;
    }

    public void setPost(PostBean post) {
        this.post = post;
    }

    public static class PostBean {
        /**
         * u_name : f1c5-4d5c-b27e-70ea802ae3ca
         * comments : [{"create_time":1618998321000,"hot_value":1,"replies":[{"reply_u_name":"新用户","create_time":1618998471000,"send_u_head":"http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg","send_u_name":"新用户","community_post_comment_id":1,"community_post_comment_reply_id":1,"send_u_id":34,"reply_content":"我也踩一踩","reply_u_id":0}],"send_u_head":"http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg","send_u_name":"新用户","zan_num":0,"community_post_comment_id":1,"community_post_id":30,"send_u_id":0,"state":1,"community_post_comment_content":"踩一踩"}]
         * community_post_id : 30
         * u_nick_name : 新用户
         * communityPostState : 1
         * postItems : [{"community_post_id":30,"community_post_item_id":13,"community_post_item_type":0,"community_post_item_url":"http://www.foxluo.cn/FlyMessage/file/postFile/20210421165926748.jpg"},{"community_post_id":30,"community_post_item_id":14,"community_post_item_type":0,"community_post_item_url":"http://www.foxluo.cn/FlyMessage/file/postFile/20210421165927724.JPG"},{"community_post_id":30,"community_post_item_id":15,"community_post_item_type":0,"community_post_item_url":"http://www.foxluo.cn/FlyMessage/file/postFile/20210421165928426.jpg"}]
         * zanCount : 0
         * commentCount : 1
         * shareCount : 0
         * showCount : 3
         * u_id : 34
         * zans : []
         * createTime : 1618995566000
         * u_head : http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg
         * zan_state : 0
         * communityPostContent : 三张图片
         */

        private String u_name;
        private int community_post_id;
        private String u_nick_name;
        private int communityPostState;
        private int zanCount;
        private int commentCount;
        private int shareCount;
        private int showCount;
        private int u_id;
        private long createTime;
        private String u_head;
        private int zan_state;
        private String communityPostContent;
        private List<CommentsBean> comments;
        private List<PostItemsBean> postItems;
        private List<ZanBean> zans;

        public String getU_name() {
            return u_name;
        }

        public void setU_name(String u_name) {
            this.u_name = u_name;
        }

        public int getCommunity_post_id() {
            return community_post_id;
        }

        public void setCommunity_post_id(int community_post_id) {
            this.community_post_id = community_post_id;
        }

        public String getU_nick_name() {
            return u_nick_name;
        }

        public void setU_nick_name(String u_nick_name) {
            this.u_nick_name = u_nick_name;
        }

        public int getCommunityPostState() {
            return communityPostState;
        }

        public void setCommunityPostState(int communityPostState) {
            this.communityPostState = communityPostState;
        }

        public int getZanCount() {
            return zanCount;
        }

        public void setZanCount(int zanCount) {
            this.zanCount = zanCount;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public int getShareCount() {
            return shareCount;
        }

        public void setShareCount(int shareCount) {
            this.shareCount = shareCount;
        }

        public int getShowCount() {
            return showCount;
        }

        public void setShowCount(int showCount) {
            this.showCount = showCount;
        }

        public int getU_id() {
            return u_id;
        }

        public void setU_id(int u_id) {
            this.u_id = u_id;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getU_head() {
            return u_head;
        }

        public void setU_head(String u_head) {
            this.u_head = u_head;
        }

        public int getZan_state() {
            return zan_state;
        }

        public void setZan_state(int zan_state) {
            this.zan_state = zan_state;
        }

        public String getCommunityPostContent() {
            return communityPostContent;
        }

        public void setCommunityPostContent(String communityPostContent) {
            this.communityPostContent = communityPostContent;
        }

        public List<CommentsBean> getComments() {
            return comments;
        }

        public void setComments(List<CommentsBean> comments) {
            this.comments = comments;
        }

        public List<PostItemsBean> getPostItems() {
            return postItems;
        }

        public void setPostItems(List<PostItemsBean> postItems) {
            this.postItems = postItems;
        }

        public List<ZanBean> getZans() {
            return zans;
        }

        public void setZans(List<ZanBean> zans) {
            this.zans = zans;
        }
        public static class ZanBean{
            private int user_id;
            private long create_time;

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public long getCreate_time() {
                return create_time;
            }

            public void setCreate_time(long create_time) {
                this.create_time = create_time;
            }
        }
        public static class CommentsBean {
            /**
             * create_time : 1618998321000
             * hot_value : 1
             * replies : [{"reply_u_name":"新用户","create_time":1618998471000,"send_u_head":"http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg","send_u_name":"新用户","community_post_comment_id":1,"community_post_comment_reply_id":1,"send_u_id":34,"reply_content":"我也踩一踩","reply_u_id":0}]
             * send_u_head : http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg
             * send_u_name : 新用户
             * zan_num : 0
             * community_post_comment_id : 1
             * community_post_id : 30
             * send_u_id : 0
             * state : 1
             * community_post_comment_content : 踩一踩
             */

            private long create_time;
            private int hot_value;
            private String send_u_head;
            private String send_u_name;
            private String send_u_nick_name;
            private int zan_state;
            private int zan_num;
            private int community_post_comment_id;
            private int community_post_id;
            private int send_u_id;
            private int state;
            private String community_post_comment_content;
            private List<RepliesBean> replies;

            public int getZan_state() {
                return zan_state;
            }

            public void setZan_state(int zan_state) {
                this.zan_state = zan_state;
            }

            public String getSend_u_nick_name() {
                return send_u_nick_name;
            }

            public void setSend_u_nick_name(String send_u_nick_name) {
                this.send_u_nick_name = send_u_nick_name;
            }

            public long getCreate_time() {
                return create_time;
            }

            public void setCreate_time(long create_time) {
                this.create_time = create_time;
            }

            public int getHot_value() {
                return hot_value;
            }

            public void setHot_value(int hot_value) {
                this.hot_value = hot_value;
            }

            public String getSend_u_head() {
                return send_u_head;
            }

            public void setSend_u_head(String send_u_head) {
                this.send_u_head = send_u_head;
            }

            public String getSend_u_name() {
                return send_u_name;
            }

            public void setSend_u_name(String send_u_name) {
                this.send_u_name = send_u_name;
            }

            public int getZan_num() {
                return zan_num;
            }

            public void setZan_num(int zan_num) {
                this.zan_num = zan_num;
            }

            public int getCommunity_post_comment_id() {
                return community_post_comment_id;
            }

            public void setCommunity_post_comment_id(int community_post_comment_id) {
                this.community_post_comment_id = community_post_comment_id;
            }

            public int getCommunity_post_id() {
                return community_post_id;
            }

            public void setCommunity_post_id(int community_post_id) {
                this.community_post_id = community_post_id;
            }

            public int getSend_u_id() {
                return send_u_id;
            }

            public void setSend_u_id(int send_u_id) {
                this.send_u_id = send_u_id;
            }

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public String getCommunity_post_comment_content() {
                return community_post_comment_content;
            }

            public void setCommunity_post_comment_content(String community_post_comment_content) {
                this.community_post_comment_content = community_post_comment_content;
            }

            public List<RepliesBean> getReplies() {
                return replies;
            }

            public void setReplies(List<RepliesBean> replies) {
                this.replies = replies;
            }

            public static class RepliesBean {
                /**
                 * reply_u_name : 新用户
                 * create_time : 1618998471000
                 * send_u_head : http://www.foxluo.cn/FlyMessage/images/userHeads/20210419162603132.jpg
                 * send_u_name : 新用户
                 * community_post_comment_id : 1
                 * community_post_comment_reply_id : 1
                 * send_u_id : 34
                 * reply_content : 我也踩一踩
                 * reply_u_id : 0
                 */

                private String reply_u_name;
                private long create_time;
                private String send_u_head;
                private String send_u_name;
                private String send_u_nick_name;
                private int community_post_comment_id;
                private int community_post_comment_reply_id;
                private int send_u_id;
                private String reply_content;
                private int reply_u_id;

                public String getSend_u_nick_name() {
                    return send_u_nick_name;
                }

                public void setSend_u_nick_name(String send_u_nick_name) {
                    this.send_u_nick_name = send_u_nick_name;
                }

                public String getReply_u_name() {
                    return reply_u_name;
                }

                public void setReply_u_name(String reply_u_name) {
                    this.reply_u_name = reply_u_name;
                }

                public long getCreate_time() {
                    return create_time;
                }

                public void setCreate_time(long create_time) {
                    this.create_time = create_time;
                }

                public String getSend_u_head() {
                    return send_u_head;
                }

                public void setSend_u_head(String send_u_head) {
                    this.send_u_head = send_u_head;
                }

                public String getSend_u_name() {
                    return send_u_name;
                }

                public void setSend_u_name(String send_u_name) {
                    this.send_u_name = send_u_name;
                }

                public int getCommunity_post_comment_id() {
                    return community_post_comment_id;
                }

                public void setCommunity_post_comment_id(int community_post_comment_id) {
                    this.community_post_comment_id = community_post_comment_id;
                }

                public int getCommunity_post_comment_reply_id() {
                    return community_post_comment_reply_id;
                }

                public void setCommunity_post_comment_reply_id(int community_post_comment_reply_id) {
                    this.community_post_comment_reply_id = community_post_comment_reply_id;
                }

                public int getSend_u_id() {
                    return send_u_id;
                }

                public void setSend_u_id(int send_u_id) {
                    this.send_u_id = send_u_id;
                }

                public String getReply_content() {
                    return reply_content;
                }

                public void setReply_content(String reply_content) {
                    this.reply_content = reply_content;
                }

                public int getReply_u_id() {
                    return reply_u_id;
                }

                public void setReply_u_id(int reply_u_id) {
                    this.reply_u_id = reply_u_id;
                }
            }
        }

        public static class PostItemsBean {
            /**
             * community_post_id : 30
             * community_post_item_id : 13
             * community_post_item_type : 0
             * community_post_item_url : http://www.foxluo.cn/FlyMessage/file/postFile/20210421165926748.jpg
             */

            private int community_post_id;
            private int community_post_item_id;
            private int community_post_item_type;
            private String community_post_item_url;

            public int getCommunity_post_id() {
                return community_post_id;
            }

            public void setCommunity_post_id(int community_post_id) {
                this.community_post_id = community_post_id;
            }

            public int getCommunity_post_item_id() {
                return community_post_item_id;
            }

            public void setCommunity_post_item_id(int community_post_item_id) {
                this.community_post_item_id = community_post_item_id;
            }

            public int getCommunity_post_item_type() {
                return community_post_item_type;
            }

            public void setCommunity_post_item_type(int community_post_item_type) {
                this.community_post_item_type = community_post_item_type;
            }

            public String getCommunity_post_item_url() {
                return community_post_item_url;
            }

            public void setCommunity_post_item_url(String community_post_item_url) {
                this.community_post_item_url = community_post_item_url;
            }
        }
    }
}
