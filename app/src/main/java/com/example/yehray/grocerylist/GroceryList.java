package package com.example.yehray.grocerylist;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class GroceryList implements Serializable {
    private Date date;
    private String text;
    private ArrayList<Item> items;
    private boolean fullDisplayed;
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy 'at' hh:mm aaa");

    public GroceryList() {
        this.date = new Date();
        items = new ArrayList<Item>();
    }
    public GroceryList(String text) {
        this.date = new Date();
        this.text = text;
        items = new ArrayList<Item>();
    }

    public GroceryList(Long time, String text)
    {
        this.date = new Date(time);
        this.text = text;
        items = new ArrayList<Item>();
    }

    public String getDate() {
        return dateFormat.format(date);
    }

    public Long getTime() {
        return date.getTime();
    }

    public void setTime(Long time) {
        this.date = new Date(time);
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Item> getItems() { return this.items; }

    public void setItems(ArrayList<Item> items) { this.items = items; }

    public String getText() {
        return this.text;
    }

    public String getShortText() {
        String temp = text.replaceAll("\n", " ");
        if (temp.length() > 25) {
            return temp.substring(0,25) + "...";

        }
        else {
            return temp;
        }
    }

    public void setFullDisplayed(boolean fullDisplayed) {
        this.fullDisplayed = fullDisplayed;
    }

    public boolean isFullDisplayed() {
        return this.fullDisplayed;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
