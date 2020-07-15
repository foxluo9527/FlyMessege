package com.example.flymessagedome.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class Message implements Parcelable {

    /**
     * m_content_type : 1
     * m_source_g_id : 5
     * m_source_id : 1
     * m_send_time : 1590142527000
     * m_content : 测试图文消息[file:a67f29986f4a44f6019a27361c073edd.mp4,link:http://www.foxluo.cn/FlyMessage/file/messageFile/20200522181527278.mp4]
     * m_type : 1
     * m_object_id : 8
     * m_id : 18
     * m_receive_state : 0
     */

    private int m_content_type;
    private int m_source_g_id;
    private int m_source_id;
    private long m_send_time;
    private String m_content;
    private int m_type;
    private int m_object_id;
    @Id(autoincrement = true)
    private Long m_id;
    private int m_receive_state;
    //判断是否为当前登录用户的消息
    private long login_u_id;
    private long cId;
    private boolean isSend;
    private long downloadId;
    //下载状态 0 ：在线文件(未下载),1:正在下载,2:下载失败,3:下载暂停,4:下载成功
    private int downloadState;

    //    Deserialization
    protected Message(Parcel in) {
        m_content_type=in.readInt();
        m_source_g_id=in.readInt();
        m_source_id=in.readInt();
        m_send_time = in.readLong();
        m_content = in.readString();
        m_type = in.readInt();
        m_object_id = in.readInt();
        m_id = in.readLong();
        m_receive_state = in.readInt();
        cId = in.readLong();
        isSend=in.readBoolean();
        downloadId=in.readLong();
        downloadState=in.readInt();
    }
    @Keep
    public Message(int m_content_type, int m_source_g_id, int m_source_id, long m_send_time, String m_content, int m_type, int m_object_id,
                   Long m_id, int m_receive_state, long cId) {
        this.m_content_type = m_content_type;
        this.m_source_g_id = m_source_g_id;
        this.m_source_id = m_source_id;
        this.m_send_time = m_send_time;
        this.m_content = m_content;
        this.m_type = m_type;
        this.m_object_id = m_object_id;
        this.m_id = m_id;
        this.m_receive_state = m_receive_state;
        this.cId = cId;
    }

    @Generated(hash = 637306882)
    public Message() {
    }

    @Generated(hash = 2032886370)
    public Message(int m_content_type, int m_source_g_id, int m_source_id, long m_send_time, String m_content, int m_type, int m_object_id,
            Long m_id, int m_receive_state, long login_u_id, long cId, boolean isSend, long downloadId, int downloadState) {
        this.m_content_type = m_content_type;
        this.m_source_g_id = m_source_g_id;
        this.m_source_id = m_source_id;
        this.m_send_time = m_send_time;
        this.m_content = m_content;
        this.m_type = m_type;
        this.m_object_id = m_object_id;
        this.m_id = m_id;
        this.m_receive_state = m_receive_state;
        this.login_u_id = login_u_id;
        this.cId = cId;
        this.isSend = isSend;
        this.downloadId = downloadId;
        this.downloadState = downloadState;
    }
    public int getM_content_type() {
        return m_content_type;
    }

    public void setM_content_type(int m_content_type) {
        this.m_content_type = m_content_type;
    }

    public int getM_source_g_id() {
        return m_source_g_id;
    }

    public void setM_source_g_id(int m_source_g_id) {
        this.m_source_g_id = m_source_g_id;
    }

    public int getM_source_id() {
        return m_source_id;
    }

    public void setM_source_id(int m_source_id) {
        this.m_source_id = m_source_id;
    }

    public long getM_send_time() {
        return m_send_time;
    }

    public void setM_send_time(long m_send_time) {
        this.m_send_time = m_send_time;
    }

    public String getM_content() {
        return m_content;
    }

    public void setM_content(String m_content) {
        this.m_content = m_content;
    }

    public int getM_type() {
        return m_type;
    }

    public void setM_type(int m_type) {
        this.m_type = m_type;
    }

    public int getM_object_id() {
        return m_object_id;
    }

    public void setM_object_id(int m_object_id) {
        this.m_object_id = m_object_id;
    }

    public Long getM_id() {
        return m_id;
    }

    public void setM_id(Long m_id) {
        this.m_id = m_id;
    }

    public int getM_receive_state() {
        return m_receive_state;
    }

    public void setM_receive_state(int m_receive_state) {
        this.m_receive_state = m_receive_state;
    }

    public long getCId() {
        return this.cId;
    }

    public void setCId(long cId) {
        this.cId = cId;
    }

    public boolean getIsSend() {
        return this.isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }


    public int getDownloadState() {
        return this.downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public long getDownloadId() {
        return this.downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public long getLogin_u_id() {
        return this.login_u_id;
    }

    public void setLogin_u_id(long login_u_id) {
        this.login_u_id = login_u_id;
    }

    //    Serialization
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        dest.writeInt(m_content_type);
        dest.writeInt(m_source_g_id);
        dest.writeInt(m_source_id);
        dest.writeLong(m_send_time);
        dest.writeString(m_content);
        dest.writeInt(m_type);
        dest.writeInt(m_object_id);
        dest.writeLong(m_id);
        dest.writeInt(m_receive_state);
        dest.writeLong(cId);
        dest.writeBoolean(isSend);
        dest.writeLong(downloadId);
        dest.writeInt(downloadState);
    }
}
