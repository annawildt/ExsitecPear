package com.pear;

import java.sql.Date;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        Menu menu = new Menu();
        menu.printCurrentBalance();
        boolean runApp;
        do {
            runApp = menu.printMenu();
        } while (runApp);

//        DatabaseConnection db = new DatabaseConnection();
//        db.orderHandlingUpdates(new Order(5,
//                "Placeholder",
//                Date.valueOf("2021-07-31"),
//                (new Product("P002", "jPlatta", 5700)),
//                50,
//                (new StorageUnit(2, "Norrköping"))));
//
//        db.closeConnection();
    }
}
