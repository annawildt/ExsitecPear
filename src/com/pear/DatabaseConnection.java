package com.pear;

import java.sql.*;
import java.util.*;
import java.util.Date;

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
                System.out.println("----------------------------------------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Get Products, Storage Units, Balance, and Order list **/
    public List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT ProductID, Item, Price FROM Products";
        try (Statement st = db_conn.createStatement()) {
            ResultSet rs = st.executeQuery(query);
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

    public List<Order> getIncompleteOrders() {
        List<Order> incompleteOrders = new ArrayList<>();
        String query = "SELECT * FROM Orders " +
                "LEFT JOIN Storages ON Orders.StorageID = Storages.StorageUnitID " +
                "LEFT JOIN Products ON Orders.ProductID = Products.ProductID " +
                "LEFT JOIN OrderStatus ON Orders.StatusID = OrderStatus.StatusID " +
                "WHERE Orders.StatusID != 1 AND Orders.StatusID != 3";
        try (Statement st = db_conn.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                incompleteOrders.add(new Order(
                        rs.getInt("OrderID"),
                        rs.getString("UserID"),
                        rs.getDate("Date"),
                        (new Product(
                                rs.getString("ProductID"),
                                rs.getString("Item"),
                                rs.getInt("Price"))),
                        rs.getInt("Amount"),
                        (new StorageUnit(
                                rs.getInt("StorageUnitID"),
                                rs.getString("City"))),
                        rs.getString("StatusName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incompleteOrders;
    }

    /** Order Creation **/
    public void createNewOrder(int amount, Product product, StorageUnit storageUnit) {
        String userID = "Placeholder";
        java.sql.Timestamp sqlDate = new java.sql.Timestamp(new Date().getTime());
        String query;
        int reserved = 2;
        int incoming = 4;

        if (amount > 0) {
            query = "INSERT INTO Orders (UserID, Date, ProductID, StorageID, Amount, StatusID) "
                    + "VALUES ('" + userID + "', '" + sqlDate + "', "
                    + "'" + product.getProductID() + "', " + storageUnit.getStorageUnitID() + ", "
                    + amount + ", " + incoming + ");";
        } else {
            query = "INSERT INTO Orders (UserID, Date, ProductID, StorageID, Amount, StatusID) "
                    + "VALUES ('" + userID + "', '" + sqlDate + "', "
                    + "'" + product.getProductID() + "', " + storageUnit.getStorageUnitID() + ", "
                    + amount + ", " + reserved + ");";
        }
        try (Statement statement = db_conn.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateBalanceNewOrder(amount, product, storageUnit);
    }

    public void updateBalanceNewOrder(int amount, Product product, StorageUnit storageUnit) {
        String query;
        List<BalanceStorage> storageBalance = getCurrentBalance();
        int storageUnitID = storageUnit.getStorageUnitID();
        String productID = product.getProductID();
        int currentReserved = 0;
        int currentIncoming = 0;
        for (BalanceStorage balance : storageBalance) {
            if (balance.getStorageUnit().getStorageUnitID() == storageUnitID
                    && balance.getProduct().getProductID().equals(productID)) {
                currentReserved = balance.getReserved();
                currentIncoming = balance.getIncoming();
                break;
            }
        }
        if (amount < 0) {
            query = "UPDATE Balance" +
                    " SET Reserved = " + (currentReserved + amount)
                    + " WHERE StorageUnitID = " + storageUnitID
                    + " AND ProductID = '" + productID + "'";
        } else {
            query = "UPDATE Balance " +
                    " SET Incoming = " + (currentIncoming + amount)
                    + " WHERE StorageUnitID = " + storageUnitID
                    + " AND ProductID = '" + productID + "'";
        }
        try (Statement st = db_conn.createStatement()) {
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("------------------------------");
    }

    /**  Order Cancellation **/
    public void cancelOrder(Order cancelOrder) {
        List<BalanceStorage> storageBalance = getCurrentBalance();
        int storageUnitID = cancelOrder.getStorageUnit().getStorageUnitID();
        String productID = cancelOrder.getProduct().getProductID();
        int currentReserved = 0;
        int currentIncoming = 0;
        for (BalanceStorage balance : storageBalance) {
            if (balance.getStorageUnit().getStorageUnitID() == storageUnitID
                    && balance.getProduct().getProductID().equals(productID)) {
                currentReserved = balance.getReserved();
                currentIncoming = balance.getIncoming();
                break;
            }
        }

        String cancelQuery = "UPDATE Orders " +
                "SET StatusID = 3 " +
                "WHERE OrderID =" + cancelOrder.getOrderID();
        String updateQuery;
        if (cancelOrder.getAmount() > 0) {
            updateQuery = "UPDATE Balance" +
                    " SET Incoming = " + (currentIncoming - cancelOrder.getAmount())
                    + " WHERE StorageUnitID = " + storageUnitID
                    + " AND ProductID = '" + productID + "'";
        } else {
            updateQuery = "UPDATE Balance" +
                    " SET Reserved = " + (currentReserved - cancelOrder.getAmount())
                    + " WHERE StorageUnitID = " + storageUnitID
                    + " AND ProductID = '" + productID + "'";
        }
        try (Statement st = db_conn.createStatement()) {
            st.execute(cancelQuery);
            System.out.println("Cancelled order with ID: " + cancelOrder.getOrderID());
            st.execute(updateQuery);
            System.out.println("Updated Balance.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Handling Orders **/
    public void orderHandlingUpdates(Order order) {
        List<BalanceStorage> currentBalance = getCurrentBalance();
        int inStock = 0;
        int reserved = 0;
        int incoming = 0;
        for (BalanceStorage balance : currentBalance) {
            if (balance.getProduct().getProductID().equals(order.getProduct().getProductID())
                    && balance.getStorageUnit().getStorageUnitID() == order.getStorageUnit().getStorageUnitID()) {
                inStock = balance.getInStock();
                reserved = balance.getReserved();
                incoming = balance.getIncoming();
            }
        }

        String updateOrder = "UPDATE Orders " +
                "SET StatusID = 1 " +
                "WHERE OrderID = " + order.getOrderID();
        String updateInStock = "UPDATE Balance" +
                " SET InStock = " + (inStock + order.getAmount()) +
                " WHERE ProductID = '" + order.getProduct().getProductID() +
                "' AND StorageUnitID = " + order.getStorageUnit().getStorageUnitID();

        try (Statement st = db_conn.createStatement()){
            st.execute(updateOrder);
            System.out.println("Updated order " + order.getOrderID());

            st.execute(updateInStock);
            System.out.println("Updated InStock for " + order.getStorageUnit().getCity()
            + ". Current amount: " + (inStock + order.getAmount()));

            if (order.getAmount() < 0) {
                String updateBalanceReserved = "UPDATE Balance " +
                        "SET Reserved = " + (reserved - order.getAmount()) +
                        " WHERE ProductID = '" + order.getProduct().getProductID() +
                        "' AND StorageUnitID = " + order.getStorageUnit().getStorageUnitID();
                st.execute(updateBalanceReserved);
                System.out.println("Updated Reserved for " + order.getStorageUnit().getCity()
                        + ". Current Reserved: " + "");
            } else {
                String updateBalanceIncoming = "UPDATE Balance " +
                        "SET Incoming = " + (incoming - order.getAmount()) +
                        " WHERE ProductID = '" + order.getProduct().getProductID() +
                        "' AND StorageUnitID = " + order.getStorageUnit().getStorageUnitID();
                st.execute(updateBalanceIncoming);
                System.out.println("Updated Incoming for " + order.getStorageUnit().getCity()
                        + ". Current Incoming: " + "");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws SQLException {
        db_conn.close();
    }
}
