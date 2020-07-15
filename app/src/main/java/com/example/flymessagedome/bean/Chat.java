package com.example.flymessagedome.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Chat {
    @Id(autoincrement = true)
    private Long c_id;
    //最近一条收到的消息时间
    private Date time;

    //头像（群聊以群聊头像）
    private String chat_head;
    private String chat_name;
    private String chat_content;
    private int chat_m_count;
    //是否提醒(红色未读，消息通知)
    private boolean chat_show_remind;
    //是否顶置
    private boolean chat_up;
    //是否再次展示
    private boolean chat_reshow;
    //接收者id
    private long object_u_id;
    //发送者用户id
    private long source_id;
    //发送者群聊id
    private long source_g_id;
    //聊天类型 0:用户，1:群聊
    private int chat_type;
    //聊天背景
    private String bgImg;
    private boolean inEntering;
    private boolean lastSendFailed;
    @ToMany(referencedJoinProperty = "cId")
    private List<Message> messages;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1596497024)
    private transient ChatDao myDao;
    @Keep
    public Chat(Long c_id, Date time, String chat_head, String chat_name, String chat_content,
            int chat_m_count, boolean chat_show_remind, boolean chat_up, boolean chat_reshow,
            long object_u_id, long source_id, long source_g_id, int chat_type) {
        this.c_id = c_id;
        this.time = time;
        this.chat_head = chat_head;
        this.chat_name = chat_name;
        this.chat_content = chat_content;
        this.chat_m_count = chat_m_count;
        this.chat_show_remind = chat_show_remind;
        this.chat_up = chat_up;
        this.chat_reshow = chat_reshow;
        this.object_u_id = object_u_id;
        this.source_id = source_id;
        this.source_g_id = source_g_id;
        this.chat_type = chat_type;
    }
    @Generated(hash = 519536279)
    public Chat() {
    }
    @Generated(hash = 1894034801)
    public Chat(Long c_id, Date time, String chat_head, String chat_name, String chat_content,
            int chat_m_count, boolean chat_show_remind, boolean chat_up, boolean chat_reshow,
            long object_u_id, long source_id, long source_g_id, int chat_type, String bgImg,
            boolean inEntering, boolean lastSendFailed) {
        this.c_id = c_id;
        this.time = time;
        this.chat_head = chat_head;
        this.chat_name = chat_name;
        this.chat_content = chat_content;
        this.chat_m_count = chat_m_count;
        this.chat_show_remind = chat_show_remind;
        this.chat_up = chat_up;
        this.chat_reshow = chat_reshow;
        this.object_u_id = object_u_id;
        this.source_id = source_id;
        this.source_g_id = source_g_id;
        this.chat_type = chat_type;
        this.bgImg = bgImg;
        this.inEntering = inEntering;
        this.lastSendFailed = lastSendFailed;
    }
    public Long getC_id() {
        return this.c_id;
    }
    public void setC_id(Long c_id) {
        this.c_id = c_id;
    }
    public Date getTime() {
        return this.time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public String getChat_head() {
        return this.chat_head;
    }
    public void setChat_head(String chat_head) {
        this.chat_head = chat_head;
    }
    public String getChat_name() {
        return this.chat_name;
    }
    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }
    public String getChat_content() {
        return this.chat_content;
    }
    public void setChat_content(String chat_content) {
        this.chat_content = chat_content;
    }
    public int getChat_m_count() {
        return this.chat_m_count;
    }
    public void setChat_m_count(int chat_m_count) {
        this.chat_m_count = chat_m_count;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1338118832)
    public List<Message> getMessages() {
        if (messages == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MessageDao targetDao = daoSession.getMessageDao();
            List<Message> messagesNew = targetDao._queryChat_Messages(c_id);
            synchronized (this) {
                if (messages == null) {
                    messages = messagesNew;
                }
            }
        }
        return messages;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1942469556)
    public synchronized void resetMessages() {
        messages = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    public boolean getChat_show_remind() {
        return this.chat_show_remind;
    }
    public void setChat_show_remind(boolean chat_show_remind) {
        this.chat_show_remind = chat_show_remind;
    }
    public boolean getChat_up() {
        return this.chat_up;
    }
    public void setChat_up(boolean chat_up) {
        this.chat_up = chat_up;
    }
    public boolean getChat_reshow() {
        return this.chat_reshow;
    }
    public void setChat_reshow(boolean chat_reshow) {
        this.chat_reshow = chat_reshow;
    }
    public long getObject_u_id() {
        return this.object_u_id;
    }
    public void setObject_u_id(long object_u_id) {
        this.object_u_id = object_u_id;
    }
    public long getSource_id() {
        return this.source_id;
    }
    public void setSource_id(long source_id) {
        this.source_id = source_id;
    }
    public long getSource_g_id() {
        return this.source_g_id;
    }
    public void setSource_g_id(long source_g_id) {
        this.source_g_id = source_g_id;
    }
    public int getChat_type() {
        return this.chat_type;
    }
    public void setChat_type(int chat_type) {
        this.chat_type = chat_type;
    }
    public boolean getInEntering() {
        return this.inEntering;
    }
    public void setInEntering(boolean inEntering) {
        this.inEntering = inEntering;
    }
    public boolean getLastSendFailed() {
        return this.lastSendFailed;
    }
    public void setLastSendFailed(boolean lastSendFailed) {
        this.lastSendFailed = lastSendFailed;
    }
    public String getBgImg() {
        return this.bgImg;
    }
    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1004576325)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChatDao() : null;
    }

}
