package com.zhangxin.videoapp;

import android.app.Application;

public class MyApplication extends Application {

    private String student_id="1120161967";
    private String user_name="张歆";

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
