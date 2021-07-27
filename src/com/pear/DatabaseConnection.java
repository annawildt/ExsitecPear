package com.pear;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private Connection db_conn;

    /** Connect to Database **/
    public DatabaseConnection() {
        try {
            String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=ExsitecPear";
            db_conn = DriverManager.getConnection(dbURL, "test", "test");
            if (db_conn != null) {
                System.out.println("Successfully connected to...");
                DatabaseMetaData dm = db_conn.getMetaData();
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Driver version: " + dm.getDriverVersion());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                System.out.println("Product version: " + dm.getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Get Products, Storage Units, and Balance **/
    public List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT ProductID, Item, Price FROM Products";
        try (Statement stmt = db_conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String productID = rs.getString("ProductID");
                String description = rs.getString("Item");
                int price = rs.getInt("Price");
                productList.add(new Product(productID, description, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return productList;
    }

    public List<StorageUnit> getStorageUnits() {
        List<StorageUnit> storageUnitList = new ArrayList<>();
        String query = "SELECT StorageUnitID, City FROM Storages";
        try (Statement stmt = db_conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int storageUnitID = rs.getInt("StorageUnitID");
                String city = rs.getString("City");
                storageUnitList.add(new StorageUnit(storageUnitID, city));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storageUnitList;
    }

    public List<Array> getCurrentBalance() {
        List<Array> storageBalance = new ArrayList<>();
        // Get the current storage balance by calculating InStock vs Reserved
        return storageBalance;
    }

    public List<Array> getIncomingAmount() {
        List<Array> incomingAmount = new ArrayList<>();
        // Get the incoming amount per storage and item
        return incomingAmount;
    }


    /** Order Creation **/
    public boolean createNewOrder(int amount, Product product, StorageUnit storageUnit) {
        if (checkStorageItems(amount, product, storageUnit)) {
            System.out.println("Creating order");
        } else {
            return false;
        }

        return true;
    }

    public boolean checkStorageItems(int amount, Product product, StorageUnit storageUnit) {
        // Check if the amount of product is available in storage
        return true;
    }

    /** Handling Outgoing Items **/
    public List<Order> getReservedOrders() {
        List<Order> reservedOrders = new ArrayList<>();
        // Get the orders with negative amount which have status Reserved
        return reservedOrders;
    }
    public boolean takeOutReservedOrder() {
        /* Select order among status Reserved and take it out of storage,
        marking the order as done and remove the amount from reserved and
        remove the same amount from Balance InStock*/
        return true;
    }

    /** Handling Incoming Stock **/
    public List<Order> getIncomingOrders() {
        List<Order> incomingOrders = new ArrayList<>();
        // Get the orders with positive amount which have status Incoming
        return incomingOrders;
    }

    public boolean receiveIncomingOrder() {
        /* Select order among status Incoming and put into storage,
        marking the order as done and remove the amount from incoming and
        add the same amount from Balance InStock*/
        return true;
    }
}
