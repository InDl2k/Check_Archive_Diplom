package com.example.testqrscanner;

public class Shop {
    private int id;
    private String name;
    private String address;
    private String retailName;

    Shop(int id, String name, String address, String retailName){
        this.id = id;
        this.name = name;
        this.address = address;
        this.retailName = retailName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getRetailName() {
        return retailName;
    }

    public void setId(int id) {
        this.id = id;
    }
}
