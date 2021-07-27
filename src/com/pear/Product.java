package com.pear;

public class Product {
    private String productID;
    private String description;
    private int price;

    public Product(String productID, String description, int price) {
        this.productID = productID;
        this.description = description;
        this.price = price;
    }

    public String getProductID() {
        return productID;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }
}
