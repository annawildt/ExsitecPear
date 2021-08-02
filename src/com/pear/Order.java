package com.pear;

import java.sql.Date;

public class Order {
    private int orderID;
    private String userID;
    private java.sql.Date date;
    private Product product;
    private int amount;
    private StorageUnit storageUnit;
    private String status;

    public Order(String userID, java.sql.Date date, Product product, int amount, StorageUnit storageUnit) {
        this.userID = userID;
        this.date = date;
        this.product = product;
        this.amount = amount;
        this.storageUnit = storageUnit;
    }

    public Order(int orderID, String userID, Date date, Product product, int amount, StorageUnit storageUnit) {
        this.orderID = orderID;
        this.userID = userID;
        this.date = date;
        this.product = product;
        this.amount = amount;
        this.storageUnit = storageUnit;
    }

    public Order(int orderID, String userID, Date date, Product product, int amount, StorageUnit storageUnit, String status) {
        this.orderID = orderID;
        this.userID = userID;
        this.date = date;
        this.product = product;
        this.amount = amount;
        this.storageUnit = storageUnit;
        this.status = status;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getUserID() {
        return userID;
    }

    public java.sql.Date getDate() {
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

    public String getStatus() {
        return status;
    }
}
