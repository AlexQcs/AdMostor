package com.hc.admc.bean;

/**
 * Created by Alex on 2017/12/8.
 * 备注:
 */

public class ScreenItemBean {

    /**
     * id : video_3_1511748600792 // 节目唯一区块标识
     * x : 0    // 节目区块坐标
     * y : 0
     * width : 12   // 可以不用理这参数（保留参数）
     * height : 18
     * reality_width : 360.00   // 区块的实际宽 =  reality_width * 2
     * reality_height : 540.00  // 区块的实际高 =  reality_height * 2
     */

    private String id;
    private int x;
    private int y;
    private int width;
    private int height;
    private String reality_width;
    private String reality_height;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public String getReality_width() {
        return reality_width;
    }

    public void setReality_width(String reality_width) {
        this.reality_width = reality_width;
    }

    public String getReality_height() {
        return reality_height;
    }

    public void setReality_height(String reality_height) {
        this.reality_height = reality_height;
    }
}
