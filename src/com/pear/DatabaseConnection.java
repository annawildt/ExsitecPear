package com.pear;

import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    private Connection db_conn;

    /**
     * Connect to Database
     **/
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

    /**
     * Get Products, Storage Units, and Balance
     **/
    public List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT ProductID, Item, Price FROM Products";
        try (Statement stmt = db_conn.createStatement()) {
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
        String query = "SELECT * FROM Storages";
        try (Statement stmt = db_conn.createStatement()) {
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

    public List<BalanceStorage> getCurrentBalance() {
        List<BalanceStorage> storageBalance = new ArrayList<>();
        String query = "SELECT * FROM Balance " +
                "LEFT JOIN Storages ON Balance.StorageUnitID=Storages.StorageUnitID " +
                "LEFT JOIN Products ON Balance.ProductID=Products.ProductID";
        try (Statement st = db_conn.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                StorageUnit storageUnit = new StorageUnit(rs.getInt("StorageUnitID"),
                        rs.getString("City"));
                Product product = new Product(rs.getString("ProductID"),
                        rs.getString("Item"),
                        rs.getInt("price"));
                int inStock = rs.getInt("InStock");
                int reserved = rs.getInt("Reserved");
                int incoming = rs.getInt("Incoming");
                storageBalance.add(new BalanceStorage(storageUnit, product, inStock, reserved, incoming));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storageBalance;
    }

    public List<Array> getIncomingAmount() {
        List<Array> incomingAmount = new ArrayList<>();
        // Get the incoming amount per storage and item
        return incomingAmount;
    }

    public List<Order> getIncompleteOrders() {
        List<Order> incompleteOrders = new ArrayList<>();
        // Get all of the incomplete orders.
        return incompleteOrders;
    }


    /**
     * Order Creation
     **/
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

    /**
     * Order Cancellation
     **/
    public void cancelOrder(int orderID) {
        String query = "DELETE FROM Orders WHERE OrderID =" + orderID;
        try (Statement st = db_conn.createStatement()) {
            st.executeQuery(query);
            System.out.println("Deleted order with ID: " + orderID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handling Outgoing Items
     **/
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

    /**
     * Handling Incoming Stock
     **/
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
