package com.pear;

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
        System.out.println("Choose an option from the menu:");
        System.out.println("1. View current storage balance.");
        System.out.println("2. Create order.");
        System.out.println("3. Send order.");
        System.out.println("4. Receive order to storage.");
        System.out.println("5. Cancel order.");
        System.out.println("0. Quit");
        System.out.println("-------------------------------------");
        return inputMenuOption();
    }

    public boolean inputMenuOption() {
        boolean continueMenu = true;
        int option = enterUserInput();
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
                receiveOrder();
                break;
            case 5:
                cancelOrder();
                break;
            default:
                System.out.println("Please enter a valid menu option.");
                inputMenuOption();
        }
        return continueMenu;
    }

    public void printCurrentBalance() {
        List<BalanceStorage> storageBalance = db_conn.getCurrentBalance();
        for (BalanceStorage baSt : storageBalance) {
            System.out.println("StorageUnit: " + baSt.getStorageUnit().getCity());
            System.out.println("Product: " + baSt.getProduct().getItem());
            System.out.println("Available amount: " + (baSt.getInStock() - baSt.getReserved()));
            System.out.println("Pending delivery: " + baSt.getIncoming());
            System.out.println("------------------------------------------------");
        }
    }

    public void createOrder() {
        //Create order by letting user select which product, amount and from where
        System.out.println("Create order");
    }

    public void sendOrder() {
        // Send order by listing outgoing orders and letting user pick which one to send then send
        System.out.println("Send order");
    }

    public void receiveOrder() {
        // Receive order by listing incoming orders and letting user pick which one to get and add
        System.out.println("Receive order.");
    }

    public void cancelOrder() {
        List<Order> incompleteOrders = db_conn.getIncompleteOrders();

        System.out.println("Orders not yet processed:");
        printOrders(incompleteOrders);

        System.out.println("Enter the order ID of the order you wish to cancel or 0 to stop cancellation:");
        int orderID = enterUserInput();

        if (orderID != 0) {
            while (!checkCancelOrder(orderID, incompleteOrders)) {
                System.out.println("Order not found. Please enter a valid Order ID or 0 to quit:");
                orderID = enterUserInput();
                if (orderID == 0) {
                    break;
                } else if (checkCancelOrder(orderID, incompleteOrders)) {
                    db_conn.cancelOrder(orderID);
                }
            }
        } else {
            System.out.println("Going back to menu...");
        }
    }

    public int enterUserInput() {
        Scanner userInput = new Scanner(System.in);
        int userOption = 0;
        try {
            userOption = userInput.nextInt();
        } catch (Exception e) {
            System.out.println("Please enter a valid number.");
            enterUserInput();
        }
        return userOption;
    }

    public boolean checkCancelOrder(int orderID, List<Order> incompleteOrders) {
        for (Order order : incompleteOrders) {
            if (order.getOrderID() == orderID) {
                return true;
            }
        }
        return false;
    }

    public void printOrders(List<Order> orders) {
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getOrderID());
            System.out.println("Order Date: " + order.getDate());
            System.out.println("User ID: " + order.getUserID());
            System.out.println("Product: " + order.getProduct());
            System.out.println("Amount: " + order.getAmount());
            System.out.println("Storage Unit: " + order.getStorageUnit());
            System.out.println("----------------------------------------");
        }
    }

}

