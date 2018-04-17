package com.example.yehray.grocerylist;

import android.util.Log;


public class databaseQuery {
    private String[] strs = new String[2];

    public databaseQuery(){
        strs[0] = "http://omscs6300groupgrocerylist.appspot.com/";
    }

    public String searchProduct(String productName){
        strs[1]  = "SELECT * FROM itemlist1 where item_name like '" + productName + "%'";
        Log.e("myapp", strs[1]);

        try{
            String rs = new connectDB().execute(strs).get();
            Log.e("myapp", rs);
            return rs;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String displayType(){
        strs[1]  = "SELECT DISTINCT item_category FROM itemlist1";
        Log.e("myapp", strs[1]);

        try{
            String rs = new connectDB().execute(strs).get();
            Log.e("myapp", rs);
            return rs;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String displayItemByType(String type){
        strs[1]  = "SELECT * FROM itemlist1 where item_category = '" + type + "'";
        Log.e("myapp", strs[1]);

        try{
            String rs = new connectDB().execute(strs).get();
            Log.e("myapp", rs);
            return rs;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String insertData(String item_name, String item_category){
        strs[1]  = "INSERT INTO itemlist1 (item_category, item_name) VALUES ('" + item_category + "','" + item_name + "')";
        Log.e("myapp", strs[1]);

        try{
            String rs = new connectDB().execute(strs).get();
            Log.e("myapp", rs);
            return rs;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
