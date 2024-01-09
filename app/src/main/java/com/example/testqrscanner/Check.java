package com.example.testqrscanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Check {
    private int id;
    private String qrraw;
    private int shopID;
    private LocalDateTime date;
    private float price;
    private ArrayList<Product> products;

    Check(int id, String qrraw, int shopID, LocalDateTime date, float price, ArrayList<Product> products){
        this.id = id;
        this.qrraw = qrraw;
        this.shopID = shopID;
        this.date = date;
        this.price = price;
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public float getPrice() {
        return price;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDateStringOfFormat(String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public int getShopID() {
        return shopID;
    }

    public String getQrraw() {
        return qrraw;
    }

    //TODO return json array of products = {id, price, count}
    public String getProductsJson() {
        try {
            JSONArray arr = new JSONArray();
            for (Product product : products) {
                JSONObject obj = new JSONObject();
                obj.put("id", product.getId());
                obj.put("price", product.getPrice());
                obj.put("count", product.getCount());
                arr.put(obj);
            }
            return arr.toString();
        }
        catch (JSONException exc){
            ToastMessage.show(exc.getMessage());
            return null;
        }
    }

    public ArrayList<Product> getProducts(){
        return products;
    }

}
