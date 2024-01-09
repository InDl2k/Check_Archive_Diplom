package com.example.testqrscanner;

public final class UserData {
    private static int userID;
    private static String apiKey;

    public static void setUserID(int id) {
        userID = id;
    }

    public static void setApiKey(String apiKey) {
        UserData.apiKey = apiKey;
    }

    public static int getUserID() {
        return userID;
    }

    public static String getApiKey() {
        return apiKey;
    }
}