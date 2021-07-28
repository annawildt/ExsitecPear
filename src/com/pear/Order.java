package com.pear;

import java.sql.Timestamp;

public class Order {
    private int orderID;
    private String userID;
    private java.sql.Timestamp date;
    private Product product;
    private int amount;
    private StorageUnit storageUnit;

    public Order(int orderID, String userID, Timestamp date, Product product, int amount, StorageUnit storageUnit) {
        this.orderID = orderID;
        this.userID = userID;
        this.date = date;
        this.product = product;
        this.amount = amount;
        this.storageUnit = storageUnit;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getUserID() {
        return userID;
    }

    public Timestamp getDate() {
        return date;
    }

    public Product getProduct() {
        return product;
    }

    public int getAmount() {
        return amount;
    }

    public StorageUnit getStorageUnit() {
        return storageUnit;
    }
}
