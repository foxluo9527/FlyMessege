package com.example.flymessagedome.model;

import java.util.List;

public class PostListResult extends Base {
    private List<PostsBean> posts;

    public List<PostsBean> getPosts() {
        return posts;
    }

    public void setPosts(List<PostsBean> posts) {
        this.posts = posts;
    }

    public static class PostsBean {
        /**
         * community_post_id : 27
         * communityPostState : 1
         * postItems : [{"community_post_id":27,"community_post_item_id":10,"community_post_item_type":0,"community_post_item_url":"http://www.foxluo.cn/FlyMessage/file/postFile/20210421105722477.jpg"}]
         * zanCount : 0
         * commentCount : 0
         * shareCount : 0
         * showCount : 0
         * u_id : 34
         * createTime : 1618973833000
         * zan_state : 0
         * communityPostContent :
         */

        private int community_post_id;
        private int communityPostState;
        private int zanCount;
        private int commentCount;
        private int shareCount;
        private int showCount;
        private int u_id;
        private long createTime;
        private int zan_state;
        private String u_name;
        private String u_nick_name;
        private String u_head;
        private String communityPostContent;
        private List<PostItemsBean> postItems;

        public String getU_nick_name() {
            return u_nick_name;
        }

        public void setU_nick_name(String u_nick_name) {
            this.u_nick_name = u_nick_name;
        }

        public String getU_name() {
            return u_name;
        }

        public void setU_name(String u_name) {
            this.u_name = u_name;
        }

        public String getU_head() {
            return u_head;
        }

        public void setU_head(String u_head) {
            this.u_head = u_head;
        }

        public int getCommunity_post_id() {
            return community_post_id;
        }

        public void setCommunity_post_id(int community_post_id) {
            this.community_post_id = community_post_id;
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

        public List<PostItemsBean> getPostItems() {
            return postItems;
        }

        public void setPostItems(List<PostItemsBean> postItems) {
            this.postItems = postItems;
        }

        public static class PostItemsBean {
            /**
             * community_post_id : 27
             * community_post_item_id : 10
             * community_post_item_type : 0
             * community_post_item_url : http://www.foxluo.cn/FlyMessage/file/postFile/20210421105722477.jpg
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
