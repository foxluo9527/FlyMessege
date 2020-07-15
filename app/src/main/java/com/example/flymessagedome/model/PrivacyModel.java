package com.example.flymessagedome.model;

public class PrivacyModel extends Base {
    /**
     * privacy : {"p_u_id":1,"show_online_state":0,"add_friend_answer3":"123","add_friend_question1":"我的名字?","add_friend_question2":"我们认识吗？","add_friend_type":2,"add_friend_answer1":"罗福林","add_friend_question3":"为什么加我","p_id":1,"add_friend_answer2":"123","show_u_message":1}
     */

    private PrivacyBean privacy;

    public PrivacyBean getPrivacy() {
        return privacy;
    }

    public void setPrivacy(PrivacyBean privacy) {
        this.privacy = privacy;
    }

    public static class PrivacyBean {
        /**
         * p_u_id : 1
         * show_online_state : 0
         * add_friend_answer3 : 123
         * add_friend_question1 : 我的名字?
         * add_friend_question2 : 我们认识吗？
         * add_friend_type : 2
         * add_friend_answer1 : 罗福林
         * add_friend_question3 : 为什么加我
         * p_id : 1
         * add_friend_answer2 : 123
         * show_u_message : 1
         */

        private int p_u_id;
        private int show_online_state;
        private String add_friend_answer3;
        private String add_friend_question1;
        private String add_friend_question2;
        private int add_friend_type;
        private String add_friend_answer1;
        private String add_friend_question3;
        private int p_id;
        private String add_friend_answer2;
        private int show_u_message;

        public int getP_u_id() {
            return p_u_id;
        }

        public void setP_u_id(int p_u_id) {
            this.p_u_id = p_u_id;
        }

        public int getShow_online_state() {
            return show_online_state;
        }

        public void setShow_online_state(int show_online_state) {
            this.show_online_state = show_online_state;
        }

        public String getAdd_friend_answer3() {
            return add_friend_answer3;
        }

        public void setAdd_friend_answer3(String add_friend_answer3) {
            this.add_friend_answer3 = add_friend_answer3;
        }

        public String getAdd_friend_question1() {
            return add_friend_question1;
        }

        public void setAdd_friend_question1(String add_friend_question1) {
            this.add_friend_question1 = add_friend_question1;
        }

        public String getAdd_friend_question2() {
            return add_friend_question2;
        }

        public void setAdd_friend_question2(String add_friend_question2) {
            this.add_friend_question2 = add_friend_question2;
        }

        public int getAdd_friend_type() {
            return add_friend_type;
        }

        public void setAdd_friend_type(int add_friend_type) {
            this.add_friend_type = add_friend_type;
        }

        public String getAdd_friend_answer1() {
            return add_friend_answer1;
        }

        public void setAdd_friend_answer1(String add_friend_answer1) {
            this.add_friend_answer1 = add_friend_answer1;
        }

        public String getAdd_friend_question3() {
            return add_friend_question3;
        }

        public void setAdd_friend_question3(String add_friend_question3) {
            this.add_friend_question3 = add_friend_question3;
        }

        public int getP_id() {
            return p_id;
        }

        public void setP_id(int p_id) {
            this.p_id = p_id;
        }

        public String getAdd_friend_answer2() {
            return add_friend_answer2;
        }

        public void setAdd_friend_answer2(String add_friend_answer2) {
            this.add_friend_answer2 = add_friend_answer2;
        }

        public int getShow_u_message() {
            return show_u_message;
        }

        public void setShow_u_message(int show_u_message) {
            this.show_u_message = show_u_message;
        }
    }
}
