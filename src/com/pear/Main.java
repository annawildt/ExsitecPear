package com.pear;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DatabaseConnection db_conn = new DatabaseConnection();
        List<Product> productList = db_conn.getProducts();
        List<StorageUnit> storageUnitsList = db_conn.getStorageUnits();


    }
}
