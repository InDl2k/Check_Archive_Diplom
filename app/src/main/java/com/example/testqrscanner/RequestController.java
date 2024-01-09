package com.example.testqrscanner;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class RequestController {

    private static final String url = "https://proverkacheka.com/api/v1/check/get";

    public static void getCheck(Activity main, String qrraw){
        OkHttpClient client = new OkHttpClient();
        String token = DataBaseController.getUserApiKey(main);
        RequestBody data = new FormBody.Builder()
                .add("token", token)
                .add("qrraw", qrraw)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(data)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastMessage.show("Ошибка: не удалось получить чек, проверьте подключение к интернету или пропробуйте позже");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        if (JsonParser.getStatus(json)) {
                            Shop shop = JsonParser.getShopData(json);
                            DataBaseController.addShop(main, shop);
                            shop.setId(DataBaseController.getShopByName(main, shop.getName()).getId());
                            ArrayList<Product> products = JsonParser.getProducts(json);
                            for (Product product : products) {
                                DataBaseController.addProduct(main, product);
                                product.setId(DataBaseController.getProductByName(main, product.getName()).getId());
                            }
                            DataBaseController.addCheck(main, JsonParser.getHTML(json), JsonParser.getCheckData(qrraw, json, shop, products));
                            ToastMessage.show("Данные чека обработаны");
                        } else {
                            ToastMessage.show("Ошибка: неправильные данные QR-кода или не верный API-ключ");
                        }
                    } else {
                        ToastMessage.show("Ошибка: не удалось получить чек, проверьте подключение к интернету или пропробуйте позже");
                    }
                }
                catch (IOException exc){
                    ToastMessage.show("Ошибка: не удалось обработать данные, проверьте память телефона");
                }
            }
        });



    }




}
