package com.example.yehray.grocerylist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import edu.gatech.seclass.GroceryListManager.GroceryList;
import edu.gatech.seclass.GroceryListManager.Item;

import java.util.ArrayList;
import java.util.Date;

public class DatabaseAccess {
    private SQLiteDatabase database;
    private DatabaseOpenHelper openHelper;
    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public void save(GroceryList groceryList) {
        try {
            ContentValues values = new ContentValues();
            values.put("date", groceryList.getTime());
            values.put("memo", groceryList.getText());

            long id = database.insert(DatabaseOpenHelper.TABLE_LIST, null, values);

            insertItems(groceryList.getItems(), groceryList.getTime());
        } catch (Exception ex) {
            String msg = ex.getMessage();
        }
    }

    public void update(GroceryList groceryList) {
        try {
            ContentValues values = new ContentValues();
            String date = Long.toString(groceryList.getTime());

            long newDate = new Date().getTime();
            groceryList.setTime(newDate);
            values.put("date", newDate);
            values.put("memo", groceryList.getText());

            database.update(DatabaseOpenHelper.TABLE_LIST, values, "date = ?", new String[]{date});

            database.delete(DatabaseOpenHelper.TABLE_ITEM, "date = ?", new String[]{date});
            insertItems(groceryList.getItems(), newDate);
        } catch (Exception ex) {
            String msg = ex.getMessage();
        }

    }

    public void insertItems(ArrayList<Item> items, Long time) {
        try {
            for (Item item : items) {
                ContentValues itemValues = new ContentValues();

                itemValues.put("date", time);
                itemValues.put("name", item.getName());
                itemValues.put("quantity", item.getQuantity());
                itemValues.put("unit", item.getUnit());
                itemValues.put("checked", item.getValue());

                long rowid = database.insert(DatabaseOpenHelper.TABLE_ITEM, null, itemValues);

                ContentValues itemMaster = new ContentValues();
                itemMaster.put("name", item.getName());
                itemMaster.put("category", item.getCategory());

                Cursor cursor = database.query(false, DatabaseOpenHelper.TABLE_ITEM_MASTER, new String[]{"name"}, "LOWER(name) = ?", new String[]{item.getName().toLowerCase()}, null, null, "name", null);
                if (cursor.getCount() < 1) {
                    long id = database.insert(DatabaseOpenHelper.TABLE_ITEM_MASTER, null, itemMaster);
                }

            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
        }
    }

    public void delete(GroceryList groceryList) {
        try {
            String date = Long.toString(groceryList.getTime());
            database.delete(DatabaseOpenHelper.TABLE_ITEM, "date = ?", new String[]{date});
            database.delete(DatabaseOpenHelper.TABLE_LIST, "date = ?", new String[]{date});
        } catch (Exception ex) {
            String msg = ex.getMessage();
        }
    }

    public ArrayList<GroceryList> getAllGroceryLists() {
        ArrayList<GroceryList> groceryLists = new ArrayList<>();

        try {

            Cursor cursor = database.rawQuery("SELECT * From grocerylist ORDER BY date DESC", null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long time = cursor.getLong(0);
                String text = cursor.getString(1);
                GroceryList thisList = new GroceryList(time, text);

                String date = Long.toString(time);

                ArrayList<Item> thisItemList = new ArrayList<Item>();
                Cursor itemCursor = database.rawQuery("SELECT l.date, l.name, l.quantity, l.checked, m.category, l.unit From listitem l inner join itemmaster m on lower(l.name) = lower(m.name) WHERE l.date = ?", new String[]{date});
                itemCursor.moveToFirst();
                while (!itemCursor.isAfterLast()) {
                    //long itemTime = itemCursor.getLong(itemCursor.getColumnIndex("date"));
                    String name = itemCursor.getString(itemCursor.getColumnIndex("name"));
                    Integer quantity = itemCursor.getInt(itemCursor.getColumnIndex("quantity"));
                    Integer checked = itemCursor.getInt(itemCursor.getColumnIndex("checked"));
                    String category = itemCursor.getString(itemCursor.getColumnIndex("category"));
                    String unit = itemCursor.getString(itemCursor.getColumnIndex("unit"));

                    thisItemList.add(new Item(name, checked, quantity, category, unit));

                    itemCursor.moveToNext();
                }

                itemCursor.close();

                thisList.setItems(thisItemList);

                groceryLists.add(thisList);

                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception ex) {
            String msg = ex.getMessage();
            String blah = msg;
        }
        return groceryLists;
    }

    public String[] getDistinctItems(String category) {
        try {
            String selection = null;
            String[] selectionArgs = null;



            if (category != null) {
                selection = "category = ?";
                selectionArgs = new String[]{category};
            }

            Cursor cursor = database.query(false, DatabaseOpenHelper.TABLE_ITEM_MASTER, new String[]{"name"}, selection, selectionArgs, null, null, "name", null);
            if (cursor.getCount() > 0) {
                String[] str = new String[cursor.getCount()];
                int i = 0;

                while (cursor.moveToNext()) {
                    str[i] = cursor.getString(cursor.getColumnIndex("name"));
                    i++;
                }
                return str;
            } else {
                return new String[]{};
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            return new String[]{};
        }
    }

    public String[] getDistinctCategories() {
        try {
            Cursor cursor = database.rawQuery("SELECT * From itemcategory ORDER BY name ASC", null);
            if (cursor.getCount() > 0) {
                String[] str = new String[cursor.getCount() + 1];
                str[0] = "Select a category...";
                int i = 1;

                while (cursor.moveToNext()) {
                    str[i] = cursor.getString(cursor.getColumnIndex("name"));
                    i++;
                }
                return str;
            } else {
                return new String[]{};
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            return new String[]{};
        }
    }

    public String[] getDistinctUnits() {
        try {
            Cursor cursor = database.rawQuery("SELECT * From unitofmeasure ORDER BY name ASC", null);
            if (cursor.getCount() > 0) {
                String[] str = new String[cursor.getCount()];
                int i = 0;

                while (cursor.moveToNext()) {
                    str[i] = cursor.getString(cursor.getColumnIndex("name"));
                    i++;
                }
                return str;
            } else {
                return new String[]{};
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            return new String[]{};
        }
    }
}
