package com.pear;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        Menu menu = new Menu();
        menu.printCurrentBalance();
        boolean runApp;
        do {
            runApp = menu.printMenu();
        } while (runApp);
    }
}
