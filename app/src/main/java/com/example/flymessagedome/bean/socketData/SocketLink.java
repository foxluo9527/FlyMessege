package com.example.flymessagedome.bean.socketData;

public class SocketLink extends BaseSocketData {

    /**
     * content : {"link":"http://www.foxluo.cn","linkType":1}
     */

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * link : http://www.foxluo.cn
         * linkType : 1
         */

        private String link;
        private int linkType;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getLinkType() {
            return linkType;
        }

        public void setLinkType(int linkType) {
            this.linkType = linkType;
        }
    }
}
