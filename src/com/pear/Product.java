package com.pear;

public class Product {
    private String productID;
    private String item;
    private int price;

    public Product(String productID, String description, int price) {
        this.productID = productID;
        this.item = description;
        this.price = price;
    }

    public String getProductID() {
        return productID;
    }

    public String getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }
}
