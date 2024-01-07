package com.raghava.shopaholic.model;

public class Cart {
    private String name,price,pid;

    public Cart(){}

    public Cart(String name, String price, String pid) {
        this.name = name;
        this.price = price;
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
