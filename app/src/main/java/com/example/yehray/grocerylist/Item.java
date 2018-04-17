package com.example.yehray.grocerylist;

import java.io.Serializable;

public class Item implements Serializable {
    String name;
    int value;
    int quantity;
    String category;
    String unit;

    public Item(String name, int value, int quantity, String category, String unit) {
        this.name = name;
        this.value = value;
        this.quantity = quantity;
        this.category = category;
        this.unit = unit;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setName(String name) { this.name = name;}

    public void setValue(int value) {this.value = value;}

    public String getCategory() {return this.category; }

    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return this.unit;}

    public void setUnit(String unit) {this.unit = unit;}
}
