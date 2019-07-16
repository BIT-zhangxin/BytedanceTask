package com.bytedance.android.lesson.restapi.solution.bean;

/**
 * @author Xavier.S
 * @date 2019.01.18 17:53
 */
public class PostVideoResponse {

    private boolean success;

    private Feed result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Feed getResult() {
        return result;
    }

    public void setResult(Feed result) {
        this.result = result;
    }

    // TODO-C2 (3) Implement your PostVideoResponse Bean here according to the response json
}
