package com.example.testqrscanner;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

enum UserDB{
    ID,
    LOGIN,
    PASSWORD,
    API_KEY
};

enum ShopDB{
    ID,
    NAME,
    ADDRESS,
    RETAIL_NAME
};

enum CheckDB{
    ID,
    QRRAW,
    SHOP_ID,
    PRODUCT_LIST,
    PRICE,
    DATE_TIME,
    HTML
};

enum ProductDB{
    ID,
    NAME,
    BARCODE,
    PRICE,
    LIKED,
    CATEGORY_ID
};

enum CategoryDB{
    ID,
    NAME,
    COLOR
};

public final class DataBaseController {

    public static boolean checkLogin(Activity activity, String login, String password){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS 'Users' ('userID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'login' TEXT NOT NULL, 'password' TEXT NOT NULL, 'apiKey' TEXT NOT NULL)");
            db.execSQL(String.format("INSERT OR IGNORE INTO 'Users' ('userID', 'login', 'password', 'apiKey') VALUES (%d, '%s', '%s', '%s')", 0, "guest", "", ""));
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Users WHERE login = '%s' AND password = '%s'", login, password), null)){
                if(query.moveToFirst()){
                    UserData.setUserID(query.getInt(UserDB.ID.ordinal()));
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
    }

    public static boolean addUser(Activity activity, String login, String password) {
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("INSERT OR IGNORE INTO 'Users' ('login', 'password') VALUES ('%s', '%s')", login, password));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static String getUserApiKey(Activity activity) {
        if(!DataBaseController.checkLogin(activity, "guest", "")) return "";
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Users WHERE login = 'guest'"), null)){
                if(query.moveToFirst()){
                    return query.getString(UserDB.API_KEY.ordinal());
                }
                return "";
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return "";
        }
    }

    public static boolean setUserApiKey(Activity activity, String apiKey){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("UPDATE 'Users' SET apiKey = '%s' WHERE login = 'guest'", apiKey));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static boolean checkCheck(Activity activity, String qrraw){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS 'Checks' ('checkID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "'qrraw' TEXT NOT NULL, 'shopID' INTEGER NOT NULL, 'productList' TEXT NOT NULL, " +
                    "'price' REAL NOT NULL, 'datetime' TEXT NOT NULL, 'html' TEXT NOT NULL)");
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Checks WHERE qrraw = '%s'", qrraw), null)){
                return query.moveToFirst();
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
    }

    public static boolean addCheck(Activity activity, String html, Check check) {
        if(checkCheck(activity, check.getQrraw())) return false;
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("INSERT OR IGNORE INTO 'Checks' " +
                            "('qrraw', 'shopID', 'productList', 'price', 'datetime', 'html') " +
                            "VALUES ('%s', '%d', '%s', '%s', '%s', '%s')",
                    check.getQrraw(), check.getShopID(), check.getProductsJson(), check.getPrice(), check.getDate(), html));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static ArrayList<Check> getChecks(Activity activity){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS 'Checks' ('checkID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'qrraw' TEXT NOT NULL, 'shopID' INTEGER NOT NULL, 'productList' TEXT NOT NULL, 'price' REAL NOT NULL, 'datetime' TEXT NOT NULL, 'html' TEXT NOT NULL)");
            try(Cursor query = db.rawQuery("SELECT * FROM Checks ORDER BY datetime", null)){
                ArrayList<Check> res = new ArrayList<>();
                if(query.moveToFirst()){
                    while(!query.isAfterLast()){
                        int id = query.getInt(CheckDB.ID.ordinal());
                        String qrraw = query.getString(CheckDB.QRRAW.ordinal());
                        int shopID = query.getInt(CheckDB.SHOP_ID.ordinal());
                        String dateTime = query.getString(CheckDB.DATE_TIME.ordinal());
                        float price = query.getFloat(CheckDB.PRICE.ordinal());
                        ArrayList<Product> products = JsonParser.getProductsArray(query.getString(CheckDB.PRODUCT_LIST.ordinal()));
                        for(Product product : products){
                            Product temp = DataBaseController.getProductByID(activity, product.getId());
                            product.setCategoryID(temp.getCategoryID());
                            product.setLiked(temp.getLiked());
                            product.setName(temp.getName());
                            product.setBarcode(temp.getBarcode());
                        }
                        Check temp = new Check(id, qrraw, shopID, LocalDateTime.parse(dateTime), price, products);
                        res.add(temp);
                        query.moveToNext();
                    }
                }
                return res;
            }
        }
        catch (SQLException  exc){
            ToastMessage.show(exc.getMessage());
            return new ArrayList<>();
        }
    }

    public static String getCheckHTML(Activity activity, int id){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Checks WHERE checkID = '%d'", id), null)){
                if(query.moveToFirst()){
                    return query.getString(CheckDB.HTML.ordinal());
                }
                return null;
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return null;
        }
    }

    public static void dropDB(Activity activity){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("DROP TABLE IF EXISTS Checks");
            db.execSQL("DROP TABLE IF EXISTS Products");
            db.execSQL("DROP TABLE IF EXISTS Shops");
            db.execSQL("DROP TABLE IF EXISTS Categories");
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
        }
    }

    public static boolean checkProduct(Activity activity, Product product){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS 'Products' ('productID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'name' TEXT NOT NULL, 'barcode' TEXT NOT NULL, 'lastPrice' REAL NOT NULL, 'liked' INTEGER NOT NULL, 'category_id' INTEGER NOT NULL)");
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Products WHERE name = '%s'", product.getName()), null)){
                return query.moveToFirst();
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
    }

    public static boolean addProduct(Activity activity, Product product){
        if(checkProduct(activity, product)) return false;
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("INSERT OR IGNORE INTO 'Products' ('name', 'barcode', 'lastPrice', 'liked', 'category_id') VALUES ('%s', '%s', %f, 0, 0)", product.getName(), product.getBarcode(), product.getPrice()));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static ArrayList<Product> getProducts(Activity activity){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS 'Products' ('productID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'name' TEXT NOT NULL, 'barcode' TEXT NOT NULL, 'lastPrice' REAL NOT NULL, 'liked' INTEGER NOT NULL, 'category_id' INTEGER NOT NULL)");
            try(Cursor query = db.rawQuery("SELECT * FROM Products", null)){
                ArrayList<Product> res = new ArrayList<>();
                if(query.moveToFirst()){
                    while(!query.isAfterLast()){
                        int id = query.getInt(ProductDB.ID.ordinal());
                        String name = query.getString(ProductDB.NAME.ordinal());
                        String barcode = query.getString(ProductDB.BARCODE.ordinal());
                        float price = query.getFloat(ProductDB.PRICE.ordinal());
                        int liked = query.getInt(ProductDB.LIKED.ordinal());
                        int categoryID = query.getInt(ProductDB.CATEGORY_ID.ordinal());
                        Product temp = new Product(id, name, barcode, price, liked, categoryID);
                        res.add(temp);
                        query.moveToNext();
                    }
                }
                return res;
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return new ArrayList<>();
        }
    }

    public static Product getProductByID(Activity activity, int id) {
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Products WHERE productID = '%d'", id), null)){
                if(query.moveToFirst()){
                    return new Product(query.getInt(ProductDB.ID.ordinal()), query.getString(ProductDB.NAME.ordinal()), query.getString(ProductDB.BARCODE.ordinal()), query.getFloat(ProductDB.PRICE.ordinal()), query.getInt(ProductDB.LIKED.ordinal()), query.getInt(ProductDB.CATEGORY_ID.ordinal()));
                }
                return null;
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return null;
        }
    }

    public static Product getProductByName(Activity activity, String name) {
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Products WHERE name = '%s'", name), null)){
                if(query.moveToFirst()){
                    return new Product(query.getInt(ProductDB.ID.ordinal()), query.getString(ProductDB.NAME.ordinal()), query.getString(ProductDB.BARCODE.ordinal()), query.getFloat(ProductDB.PRICE.ordinal()), query.getInt(ProductDB.LIKED.ordinal()), query.getInt(ProductDB.CATEGORY_ID.ordinal()));
                }
                return null;
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return null;
        }
    }

    public static boolean setProductLikeStatus(Activity activity, int status, int id){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("UPDATE 'Products' SET liked = %d WHERE productID = %d", status, id));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static boolean checkShop(Activity activity, Shop shop){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS 'Shops' ('shopID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'name' TEXT NOT NULL, 'address' TEXT NOT NULL, 'retailName' TEXT NOT NULL)");
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Shops WHERE name = '%s' AND address = '%s'", shop.getName(), shop.getAddress()), null)){
                return query.moveToFirst();
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
    }

    public static boolean addShop(Activity activity, Shop shop){
        if(checkShop(activity, shop)) return false;
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("INSERT OR IGNORE INTO 'Shops' ('name', 'address', 'retailName') VALUES ('%s', '%s', '%s')", shop.getName(), shop.getAddress(), shop.getRetailName()));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static Shop getShopByName(Activity activity, String name){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Shops WHERE name = '%s'", name), null)){
                if(query.moveToFirst()){
                    return new Shop(query.getInt(ShopDB.ID.ordinal()), query.getString(ShopDB.NAME.ordinal()), query.getString(ShopDB.ADDRESS.ordinal()), query.getString(ShopDB.RETAIL_NAME.ordinal()));
                }
                return null;
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return null;
        }
    }

    public static Shop getShopByID(Activity activity, int id){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Shops WHERE shopID = '%d'", id), null)){
                if(query.moveToFirst()){
                    return new Shop(query.getInt(ShopDB.ID.ordinal()), query.getString(ShopDB.NAME.ordinal()), query.getString(ShopDB.ADDRESS.ordinal()), query.getString(ShopDB.RETAIL_NAME.ordinal()));
                }
                return null;
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return null;
        }
    }

    public static boolean checkCategory(Activity activity, String name){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS 'Categories' ('categoryID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'name' TEXT NOT NULL, 'color' INTEGER NOT NULL)");
            db.execSQL(String.format("INSERT OR IGNORE INTO 'Categories' ('categoryID', 'name', 'color') VALUES (%d, '%s', %d)", 0, "Нет категории", Color.GRAY));
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Categories WHERE name = '%s'", name), null)){
                return query.moveToFirst();
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
    }

    public static boolean addCategory(Activity activity, Category category){
        if(checkCategory(activity, category.getName())) return false;
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("INSERT OR IGNORE INTO 'Categories' ('name', 'color') VALUES ('%s', %d)", category.getName(), category.getColor()));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static boolean setProductCategory(Activity activity, Category category, int productID){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("UPDATE 'Products' SET category_id = %d WHERE productID = %d", category.getId(), productID));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static boolean setDefaultAllProductsThatCategory(Activity activity, Category category){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("UPDATE 'Products' SET category_id = %d WHERE category_id = %d", 0,  category.getId()));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }

    public static Category getCategoryByID(Activity activity, int id){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Categories WHERE categoryID = '%d'", id), null)){
                if(query.moveToFirst()){
                    return new Category(query.getInt(CategoryDB.ID.ordinal()), query.getString(CategoryDB.NAME.ordinal()), query.getInt(CategoryDB.COLOR.ordinal()));
                }
                return new Category(0, "", Color.GRAY);
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return new Category(0, "", Color.GRAY);
        }
    }

    public static Category getCategoryByName(Activity activity, String name){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            try(Cursor query = db.rawQuery(String.format("SELECT * FROM Categories WHERE name = '%s'", name), null)){
                if(query.moveToFirst()){
                    return new Category(query.getInt(CategoryDB.ID.ordinal()), query.getString(CategoryDB.NAME.ordinal()), query.getInt(CategoryDB.COLOR.ordinal()));
                }
                return new Category(0, "", Color.GRAY);
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return new Category(0, "", Color.GRAY);
        }
    }

    public static List<Category> getCategories(Activity activity){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL("CREATE TABLE IF NOT EXISTS 'Categories' ('categoryID' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'name' TEXT NOT NULL, 'color' INTEGER NOT NULL)");
            db.execSQL(String.format("INSERT OR IGNORE INTO 'Categories' ('categoryID', 'name', 'color') VALUES (%d, '%s', %d)", 0, "Нет категории", Color.GRAY));
            try(Cursor query = db.rawQuery("SELECT * FROM Categories", null)){
                List<Category> res = new ArrayList<>();
                if(query.moveToFirst()){
                    while(!query.isAfterLast()){
                        int id = query.getInt(CategoryDB.ID.ordinal());
                        String name = query.getString(CategoryDB.NAME.ordinal());
                        int color = query.getInt(CategoryDB.COLOR.ordinal());
                        Category temp = new Category(id, name, color);
                        res.add(temp);
                        query.moveToNext();
                    }
                }
                return res;
            }
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return new ArrayList<>();
        }
    }

    public static boolean eraseCategory(Activity activity, Category category){
        try(SQLiteDatabase db = activity.getApplicationContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null)){
            db.execSQL(String.format("DELETE FROM 'Categories' WHERE categoryID = %d", category.getId()));
        }
        catch (SQLException exc){
            ToastMessage.show(exc.getMessage());
            return false;
        }
        return true;
    }



}

