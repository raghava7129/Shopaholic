package com.raghava.shopaholic.model;

public class Orders {

    private String name, city, address, phone, date, totalAmount;
    public Orders(){}
    public Orders(String name, String city, String address, String phone, String date, String totalAmount){
        this.name=name;
        this.city=city;
        this.address=address;
        this.phone=phone;
        this.date=date;
        this.totalAmount=totalAmount;
    }
    public String getDate() {
        return date;
    }
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

}
