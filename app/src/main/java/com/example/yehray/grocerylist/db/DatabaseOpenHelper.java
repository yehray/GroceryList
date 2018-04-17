package com.example.yehray.grocerylist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Hashtable;
import java.util.Set;

/**
 * Created by MXM4893 on 10/16/2016.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {


    public static final String DATABASE = "grocery.db";
    public static final String TABLE_LIST = "grocerylist";
    public static final String TABLE_ITEM = "listitem";
    public static final String TABLE_CATEGORY = "itemcategory";
    public static final String TABLE_ITEM_MASTER = "itemmaster";
    public static final String TABLE_UNITS = "unitofmeasure";
    public static final int VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE grocerylist(date INTEGER PRIMARY KEY, memo TEXT);");
            db.execSQL("CREATE TABLE listitem(date INTEGER, name TEXT, quantity INTEGER, checked INTEGER, unit TEXT, PRIMARY KEY(date, name))");
            db.execSQL("CREATE TABLE itemcategory(name TEXT PRIMARY KEY)");
            db.execSQL("CREATE TABLE itemmaster(name TEXT PRIMARY KEY, category TEXT)");
            db.execSQL("CREATE TABLE unitofmeasure(name TEXT PRIMARY KEY)");

            String[] categories = new String[20];
            categories[0] = "Produce";
            categories[1] = "Meat/Poultry";
            categories[2] = "Bakery";
            categories[3] = "Deli";
            categories[4] = "Frozen Food";
            categories[5] = "Dairy";
            categories[6] = "Seafood";
            categories[7] = "Canned Goods";
            categories[8] = "Dry Packaged Goods";
            categories[9] = "Spices";
            categories[10] = "Condiments";
            categories[11] = "Baking Supplies";
            categories[12] = "Snacks";
            categories[13] = "Drinks";
            categories[14] = "Baby";
            categories[15] = "Cereal/Breakfast";
            categories[16] = "Cleaning Products";
            categories[17] = "Health & Beauty";
            categories[18] = "Pet Supplies";
            categories[19] = "Paper & Plastic";

            for (String category: categories) {
                ContentValues content = new ContentValues();
                content.put("name", category);
                db.insert(DatabaseOpenHelper.TABLE_CATEGORY, null, content);
            }

            String[] units = new String[3];
            units[0] = "EACH";
            units[1] = "DOZEN";
            units[2] = "POUNDS";

            for (String unit : units) {
                ContentValues content= new ContentValues();
                content.put("name", unit);
                db.insert(DatabaseOpenHelper.TABLE_UNITS, null, content);
            }

            Hashtable<String, String> hm = new Hashtable<String, String>();
            //add key-value pair to Hashtable
            hm.put("Carrots", "Produce");
            hm.put("Lettuce", "Produce");
            hm.put("Cabbage","Produce");
            hm.put("Ground Beef","Meat/Poultry");
            hm.put("Chicken", "Meat/Poultry");
            hm.put("Turkey", "Meat/Poultry");
            hm.put("Cake", "Bakery");
            hm.put("Pies", "Bakery");
            hm.put("Breads", "Bakery");
            hm.put("Sliced Turkey","Deli");
            hm.put("Sliced Ham","Deli");
            hm.put("Sliced Chicken", "Deli");
            hm.put("Tyson Chicken", "Frozen Food");
            hm.put("Frozen Peas","Frozen Food");
            hm.put("Garlic Bread (frozen)","Frozen Food");
            hm.put("Eggs", "Dairy");
            hm.put("Milk", "Dairy");
            hm.put("Cheese","Dairy");
            hm.put("Tuna","Seafood");
            hm.put("Bass", "Seafood");
            hm.put("Salmon","Seafood");
            hm.put("Peas","Canned Goods");
            hm.put("Green Beans", "Canned Goods");
            hm.put("Black Beans","Canned Goods");
            hm.put("Freeze Dried Meat","Dry Packaged Goods");
            hm.put("Sampler Freeze Dry", "Dry Packaged Goods");
            hm.put("Freeze Dried Veggies","Dry Packaged Goods");
            hm.put("Salt","Spices");
            hm.put("Pepper", "Spices");
            hm.put("Cumin","Spices");
            hm.put("Ketchup","Condiments");
            hm.put("Mustard", "Condiments");
            hm.put("Ranch","Condiments");
            hm.put("Flour","Baking Supplies");
            hm.put("Sugar", "Baking Supplies");
            hm.put("Brown Sugar","Baking Supplies");
            hm.put("Oreos","Snacks");
            hm.put("Oatmeal Cream Pies", "Snacks");
            hm.put("Zebra Cakes","Snacks");
            hm.put("Coke","Drinks");
            hm.put("Pepsi", "Drinks");
            hm.put("Bottled Water","Drinks");
            hm.put("Diapers","Baby");
            hm.put("Wipes","Baby");
            hm.put("Diaper Cream", "Baby");
            hm.put("Fruit Loops","Cereal/Breakfast");
            hm.put("Raisin Bran","Cereal/Breakfast");
            hm.put("Rice Crispies", "Cereal/Breakfast");
            hm.put("Windex","Cleaning Products");
            hm.put("Draino","Cleaning Products");
            hm.put("Bleach","Cleaning Products");
            hm.put("Shampoo","Health & Beauty");
            hm.put("Conditioner","Health & Beauty");
            hm.put("Deodorant","Health & Beauty");
            hm.put("Litter","Pet Supplies");
            hm.put("Dog Food","Pet Supplies");
            hm.put("Lint Brush","Pet Supplies");
            hm.put("Paper Plates","Paper & Plastic");
            hm.put("Trashbags","Paper & Plastic");
            hm.put("Sandwich Bags","Paper & Plastic");



            Set<String> keys = hm.keySet();
            for(String key: keys){
                ContentValues content = new ContentValues();
                content.put("name", key);
                content.put("category", hm.get(key));
                db.insert(DatabaseOpenHelper.TABLE_ITEM_MASTER, null, content);
            }


        }
        catch(Exception ex) {
            String msg = ex.getMessage();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
