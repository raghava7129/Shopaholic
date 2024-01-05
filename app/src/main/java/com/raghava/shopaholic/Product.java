package com.raghava.shopaholic;

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

    public void setpId(int pId) {
        this.pId = pId;
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

    public Product(int pId, String pName, String pDescription, String pImageName){
        setpId(pId);
        setpName(pName);
        setpDescription(pDescription);
        setpImageName(pImageName);
    }

}
