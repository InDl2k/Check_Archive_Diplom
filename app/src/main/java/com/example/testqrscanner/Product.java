package com.example.testqrscanner;

public class Product {
    private int id;
    private String name;
    private String barcode;
    private float price;
    private float count;
    private int liked;
    private int categoryID;

    Product(int id, String name, String barcode, float price, int liked, int categoryID){
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.liked = liked;
        this.categoryID = categoryID;
    }

    Product(int id, String name, String barcode, float price, int liked, int categoryID, float count){
        this(id, name, barcode, price, liked, categoryID);
        this.count = count;
    }


    public int getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCount() {
        return count;
    }

    public int getLiked() {
        return liked;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }
}
