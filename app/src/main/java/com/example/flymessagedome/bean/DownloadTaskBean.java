package com.example.flymessagedome.bean;

public class DownloadTaskBean {
        private long downloadId;
        private long messageId;
        private String url;

    public DownloadTaskBean(long downloadId, long messageId, String url) {
        this.downloadId = downloadId;
        this.messageId = messageId;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDownloadId() {
            return downloadId;
        }

        public void setDownloadId(long downloadId) {
            this.downloadId = downloadId;
        }

        public long getMessageId() {
            return messageId;
        }

        public void setMessageId(long messageId) {
            this.messageId = messageId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DownloadTaskBean)) return false;
            DownloadTaskBean that = (DownloadTaskBean) o;
            return downloadId == that.downloadId &&
                    messageId == that.messageId;
        }

    }