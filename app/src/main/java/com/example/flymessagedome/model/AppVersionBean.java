package com.example.flymessagedome.model;

public class AppVersionBean {
    /**
     * msg : 发现新版本
     * code : 200
     * force : 0
     * version : 1.0
     * url : 0
     * info : 第一个功能较为完善的版本
     */

    private String msg;
    private int code;
    private int force;
    private String version;
    private String url;
    private String info;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
