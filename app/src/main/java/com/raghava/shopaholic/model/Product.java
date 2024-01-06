package com.raghava.shopaholic.model;

import java.math.BigDecimal;

public class Product {
    private int pId;
    private String pName;
    private BigDecimal pPrice;
    private String pDescription;
    private String pImageName;

    public Product(){
        super();
    }

    public Product(int pId, String pName, BigDecimal pPrice, String pDescription, String pImageName) {
        setpId(pId);
        setpName(pName);
        setpPrice(pPrice);
        setpDescription(pDescription);
        setpImageName(pImageName);
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getpId() {
        return pId;
    }

    public String getpName() {
        return pName;
    }

    public BigDecimal getpPrice() {
        return pPrice;
    }

    public String getpDescription() {
        return pDescription;
    }

    public String getpImageName() {
        return pImageName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public void setpPrice(BigDecimal pPrice) {
        this.pPrice = pPrice;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public void setpImageName(String pImageName) {
        this.pImageName = pImageName;
    }

}
