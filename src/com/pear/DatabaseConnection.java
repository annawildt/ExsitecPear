package com.pear;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection dbconn;
    public DatabaseConnection() {
        try {
            String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=ExsitecPear";
            dbconn = DriverManager.getConnection(dbURL, "test", "test");
            if (dbconn != null) {
                System.out.println("Successfully connected to...");
                DatabaseMetaData dm = dbconn.getMetaData();
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Driver version: " + dm.getDriverVersion());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                System.out.println("Product version: " + dm.getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
