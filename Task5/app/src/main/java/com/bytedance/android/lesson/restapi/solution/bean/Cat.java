package com.bytedance.android.lesson.restapi.solution.bean;

/**
 * @author Xavier.S
 * @date 2019.01.17 18:08
 */
public class Cat {

    private String id;
    private String url;
    private int width;
    private int height;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    // TODO-C1 (1) Implement your Cat Bean here according to the response json
}
