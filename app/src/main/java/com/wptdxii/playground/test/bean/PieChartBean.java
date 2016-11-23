package com.wptdxii.playground.test.bean;

/**
 * Created by wptdxii on 2016/11/23 0023.
 */

public class PieChartBean {

    private String category;
    private String color;
    private int count;

    public PieChartBean(String category, String color, int count) {
        this.category = category;
        this.color = color;
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
