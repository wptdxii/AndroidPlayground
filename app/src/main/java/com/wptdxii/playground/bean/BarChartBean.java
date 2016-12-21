package com.wptdxii.playground.bean;

/**
 * Created by wptdxii on 2016/11/23 0023.
 */

public class BarChartBean {
    private String category;
    private String color;
    private int amount;

    public BarChartBean(String category, String color, int amount) {
        this.category = category;
        this.color = color;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
