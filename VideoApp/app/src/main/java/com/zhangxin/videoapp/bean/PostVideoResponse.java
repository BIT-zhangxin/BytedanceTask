package com.zhangxin.videoapp.bean;

public class PostVideoResponse {

    private boolean success;
    private Video result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Video getResult() {
        return result;
    }

    public void setResult(Video result) {
        this.result = result;
    }
}
