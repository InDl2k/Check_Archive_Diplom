package com.example.testqrscanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

public final class JsonParser {

    public static ArrayList<Product> getProducts(String json) {
        ArrayList<Product> res = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(new JSONObject(new JSONObject(new JSONObject(json).getString("data")).getString("json")).getString("items"));
            for (int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String barcode = "";
                if(jsonArray.getJSONObject(i).has("productCodeNew")) {
                    Iterator<String> keys = jsonArray.getJSONObject(i).getJSONObject("productCodeNew").keys();
                    barcode = new JSONObject(new JSONObject(jsonArray.getJSONObject(i).getString("productCodeNew")).getString(keys.next())).getString("rawProductCode");
                }
                float price = (float) (jsonArray.getJSONObject(i).getDouble("price") / 100.0);
                float count = (float) (jsonArray.getJSONObject(i).getDouble("quantity"));
                res.add(new Product(0, name, barcode, price, 0, 0, count));
            }
            return res;
        }
        catch (JSONException exc){
            ToastMessage.show(exc.getMessage());
            return new ArrayList<>();
        }
    }

    public static String getHTML(String json)  {
        try {
            String header = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "    <head>\n" +
                    "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=2, user-scalable=yes\">\n" +
                    "        <link rel=\"stylesheet\" href=\"style.css\" />\n" +
                    "    </head>\n" +
                    "<body>\n";
            String footer = "\n</body>" + "\n</html>";
            String res = header +
                    new JSONObject(new JSONObject(json).getString("data")).getString("html")
                            .replace("/qrcode/generate?text=", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=") +
                    footer;
            return res;
        }
        catch (JSONException exc){
            ToastMessage.show(exc.getMessage());
            return "";
        }
    }

    public static Check getCheckData(String qrraw, String data, Shop shop, ArrayList<Product> products) {
        try {
            JSONObject json = new JSONObject(new JSONObject(new JSONObject(data).getString("data")).getString("json"));
            String dateTime = json.getString("dateTime");
            float totalPrice = (float) (json.getDouble("totalSum") / 100.0f);
            return new Check(0, qrraw, shop.getId(), LocalDateTime.parse(dateTime), totalPrice, products);
        }
        catch (JSONException exc){
            ToastMessage.show(exc.getMessage());
            return null;
        }
    }

    public static ArrayList<Product> getProductsArray(String json) {
        try {
            ArrayList<Product> products = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); ++i){
                products.add(new Product(jsonArray.getJSONObject(i).getInt("id"), null, null, (float) jsonArray.getJSONObject(i).getDouble("price"), 0, 0, (float) jsonArray.getJSONObject(i).getDouble("count")));
            }
            return products;
        }
        catch (JSONException exc){
            ToastMessage.show(exc.getMessage());
            return new ArrayList<>();
        }
    }

    public static Shop getShopData(String json) {
        try {
            JSONObject js = new JSONObject(new JSONObject(new JSONObject(json).getString("data")).getString("json"));
            String shopName = js.getString("user");
            String shopAddress = "";
            if(js.has("retailPlaceAddress")) shopAddress = js.getString("retailPlaceAddress");
            else shopAddress = new JSONObject(js.getString("metadata")).getString("address");
            String retailName = js.getString("retailPlace");
            return new Shop(0, shopName, shopAddress, retailName);
        }
        catch (JSONException exc){
            ToastMessage.show(exc.getMessage());
            return null;
        }
    }

    public static boolean getStatus(String json) {
        try {
            return new JSONObject(json).getString("code").equals("1");
        }
        catch (JSONException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
    }


}
