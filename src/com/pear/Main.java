package com.pear;

public class Main {

    public static void main(String[] args) {
        Menu menu = new Menu();
        boolean runApp;
        do {
            runApp = menu.printMenu();
        } while (runApp);
    }
}
