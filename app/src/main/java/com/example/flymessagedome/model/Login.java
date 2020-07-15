package com.example.flymessagedome.model;

import com.example.flymessagedome.bean.User;

public class Login extends Base {
    public User loginUser;
    public String token;

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
