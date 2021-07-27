package com.pear;

import java.sql.Array;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private List<Product> productList;
    private List<StorageUnit> storageUnitsList;
    private DatabaseConnection db_conn = new DatabaseConnection();

    public Menu() {
        productList = db_conn.getProducts();
        storageUnitsList = db_conn.getStorageUnits();
    }

    public boolean printMenu() {
        System.out.println("-------------------------------------");
        System.out.println("Choose an option from the menu:");
        System.out.println("1. View current storage balance.");
        System.out.println("2. Create order.");
        System.out.println("3. Send order.");
        System.out.println("4. Receive order to storage.");
        System.out.println("5. Cancel order.");
        System.out.println("0. Quit");
        return inputMenuOption();
    }

    public boolean inputMenuOption() {
        int option = 10;
        Scanner scan = new Scanner(System.in);
        boolean continueMenu = true;

        try {
            option = scan.nextInt();
        } catch (Exception e) {
            System.out.println("Please enter a number from the menu:");
            printMenu();
        }

        switch (option) {
            case 0:
                System.out.println("Exiting program...");
                continueMenu = false;
                break;
            case 1:
                printCurrentBalance();
                break;
            case 2:
                createOrder();
                break;
            case 3:
                sendOrder();
                break;
            case 4:

                break;
            case 5:
                cancelOrder();
                break;
            default:
                System.out.println("Please enter a valid menu option:");
                printMenu();
        }
        return  continueMenu;
    }

    public void printCurrentBalance() {
        //Check what we get and print per storage and item as well as total for items
        List<Array> storageBalance = db_conn.getCurrentBalance();
        for (Array balance : storageBalance) {
            System.out.println(balance);
        }
    }

    public void createOrder() {
        System.out.println("Create order");
        //Create order by letting user select which product, amount and from where
    }

    public void sendOrder() {
        System.out.println("Send order");
        // Send order by listing outgoing orders and letting user pick which one to send then send
    }

    public void receiveOrder() {
        System.out.println("Receive order.");
    }

    public void cancelOrder() {
        System.out.println("Cancel order");
    }

}

