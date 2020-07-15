package com.example.flymessagedome.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class FriendsBean {
        /**
         * friendUser : {"nowDate":1590128013556,"u_name":"123","u_sex":"男","u_create_time":1589343049000,"u_nick_name":"新用户","u_sign":"哈哈哈哈","u_id":1,"u_brithday":-28800000,"u_head_img":"http://www.foxluo.cn/FlyMessage/images/userHeads/20200521122847534.JPG","u_position":"123","u_bg_img":"http://www.foxluo.cn/FlyMessage/images/userBgImg/20200521123701296.JPG"}
         * f_remarks_name : 新用户
         * f_source_u_id : 8
         * isOnline : false
         * f_id : 7
         * time : 1590126983000
         * f_object_u_id : 1
         */

        private String f_remarks_name;
        private long f_source_u_id;
        private boolean isOnline;
        @Id
        private long f_id;
        private long time;
        private long f_object_u_id;
        @ToOne(joinProperty = "f_object_u_id")
        private UserBean friendUser;
        /** Used to resolve relations */
        @Generated(hash = 2040040024)
        private transient DaoSession daoSession;
        /** Used for active entity operations. */
        @Generated(hash = 244630814)
        private transient FriendsBeanDao myDao;
        @Keep 
        public FriendsBean(String f_remarks_name, long f_source_u_id, boolean isOnline, long f_id, long time, int f_object_u_id) {
            this.f_remarks_name = f_remarks_name;
            this.f_source_u_id = f_source_u_id;
            this.isOnline = isOnline;
            this.f_id = f_id;
            this.time = time;
            this.f_object_u_id = f_object_u_id;
        }

        @Generated(hash = 432200542)
        public FriendsBean() {
        }

        @Generated(hash = 242529456)
        public FriendsBean(String f_remarks_name, long f_source_u_id, boolean isOnline, long f_id, long time, long f_object_u_id) {
            this.f_remarks_name = f_remarks_name;
            this.f_source_u_id = f_source_u_id;
            this.isOnline = isOnline;
            this.f_id = f_id;
            this.time = time;
            this.f_object_u_id = f_object_u_id;
        }

        @Generated(hash = 585400318)
        private transient Long friendUser__resolvedKey;
        public String getF_remarks_name() {
            return f_remarks_name;
        }

        public void setF_remarks_name(String f_remarks_name) {
            this.f_remarks_name = f_remarks_name;
        }

        public long getF_source_u_id() {
            return f_source_u_id;
        }

        public void setF_source_u_id(long f_source_u_id) {
            this.f_source_u_id = f_source_u_id;
        }

        public boolean isIsOnline() {
            return isOnline;
        }

        public void setIsOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

        public Long getF_id() {
            return f_id;
        }

        public void setF_id(Long f_id) {
            this.f_id = f_id;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getF_object_u_id() {
            return f_object_u_id;
        }

        public void setF_object_u_id(long f_object_u_id) {
            this.f_object_u_id = f_object_u_id;
        }

        public boolean getIsOnline() {
            return this.isOnline;
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

        public void setF_id(long f_id) {
            this.f_id = f_id;
        }

        /** To-one relationship, resolved on first access. */
        @Generated(hash = 657446973)
        public UserBean getFriendUser() {
            long __key = this.f_object_u_id;
            if (friendUser__resolvedKey == null || !friendUser__resolvedKey.equals(__key)) {
                final DaoSession daoSession = this.daoSession;
                if (daoSession == null) {
                    throw new DaoException("Entity is detached from DAO context");
                }
                UserBeanDao targetDao = daoSession.getUserBeanDao();
                UserBean friendUserNew = targetDao.load(__key);
                synchronized (this) {
                    friendUser = friendUserNew;
                    friendUser__resolvedKey = __key;
                }
            }
            return friendUser;
        }

        /** called by internal mechanisms, do not call yourself. */
        @Generated(hash = 1910313425)
        public void setFriendUser(@NotNull UserBean friendUser) {
            if (friendUser == null) {
                throw new DaoException("To-one property 'f_object_u_id' has not-null constraint; cannot set to-one to null");
            }
            synchronized (this) {
                this.friendUser = friendUser;
                f_object_u_id = friendUser.getU_id();
                friendUser__resolvedKey = f_object_u_id;
            }
        }

        /** called by internal mechanisms, do not call yourself. */
        @Generated(hash = 1836544691)
        public void __setDaoSession(DaoSession daoSession) {
            this.daoSession = daoSession;
            myDao = daoSession != null ? daoSession.getFriendsBeanDao() : null;
        }

      
    }