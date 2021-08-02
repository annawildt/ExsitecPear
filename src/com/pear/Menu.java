package com.pear;

import java.sql.SQLException;
import java.util.ArrayList;
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

    /** Menu handling **/
    public boolean printMenu() throws SQLException {
        System.out.println("Choose an option from the menu:");
        System.out.println("1. View current storage balance.");
        System.out.println("2. Create order.");
        System.out.println("3. Send order from storage.");
        System.out.println("4. Receive order to storage.");
        System.out.println("5. Cancel order.");
        System.out.println("0. Quit");
        System.out.println("-------------------------------------");
        return inputMenuOption();
    }

    public boolean inputMenuOption() throws SQLException{
        boolean continueMenu = true;
        int option = userInputInteger();
        switch (option) {
            case 0:
                System.out.println("Exiting program...");
                continueMenu = false;
                db_conn.closeConnection();
                break;
            case 1:
                printCurrentBalance();
                break;
            case 2:
                createOrder();
                break;
            case 3:
                System.out.println("Send an order which is marked as Reserved from the storage.");
                handleOrder(true);
                break;
            case 4:
                System.out.println("Receive an incoming order to the storage.");
                handleOrder(false);
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

    /** View balance **/
    public void printCurrentBalance() {
        List<BalanceStorage> storageBalance = db_conn.getCurrentBalance();
        for (BalanceStorage baSt : storageBalance) {
            System.out.println("StorageUnit: " + baSt.getStorageUnit().getCity());
            System.out.println("Product: " + baSt.getProduct().getItem());
            System.out.println("Pending delivery: " + baSt.getIncoming());
            System.out.println("Reserved for outgoing: " + baSt.getReserved());
            System.out.println("Available for order: " + (baSt.getInStock() + baSt.getReserved()));
            System.out.println("------------------------------------------------");
        }
    }

    /** Order creation **/
    public void createOrder() {
        int productIndex = chooseItemOrder();
        if (productIndex > 0) {
            Product productToOrder = productList.get(productIndex - 1);
            System.out.println(productToOrder.getItem() + " chosen.");
            int storageUnit = chooseStorageOrder();
            if (storageUnit > 0) {
                boolean addToStorage = chooseOrderType();
                int amount = selectAmount();
                addOrder(productToOrder, (storageUnit - 1), addToStorage, amount);
            }
        }
    }

    public int chooseItemOrder() {
        for (int i = 0; i < productList.size(); i++) {
            System.out.println((i + 1) + ". " + productList.get(i).getItem()
                    + " - " + productList.get(i).getPrice() + " SEK");
        }
        System.out.println("Enter the number of the item you wish to order or 0 to exit.");
        int userOption = userInputInteger();
        if (userOption > 0) {
            while (userOption > productList.size()) {
                System.out.println("Item not found. Please enter a valid item number:");
                userOption = userInputInteger();
            }
        } else {
            System.out.println("Exiting to menu.");
            return 0;
        }

        return userOption;
    }

    public boolean chooseOrderType() {
        System.out.println("Choose order type.");
        System.out.println("1. Order TO storage.");
        System.out.println("2. Order FROM storage.");
        int orderType = userInputInteger();
        if (!(orderType ==  1 || orderType == 2))
            chooseOrderType();
        return orderType == 1;
    }

    public int chooseStorageOrder() {
        for (int i = 0; i < storageUnitsList.size(); i++) {
            System.out.println((i + 1) + ". " + storageUnitsList.get(i).getCity());
        }
        System.out.println("Enter the number of the storage you wish to order from or 0 to exit.");
        int userOption = userInputInteger();
        if (userOption > 0) {
            while (userOption > storageUnitsList.size()) {
                System.out.println("Storage not found. Please enter a valid item number:");
                userOption = userInputInteger();
            }
        } else {
            System.out.println("Exiting to menu.");
            return 0;
        }
        return userOption;
    }

    public int selectAmount() {
        System.out.println("Please enter the amount of items you wish to order:");
        int amount = userInputInteger();
        while (amount < 0) {
            System.out.println("Invalid amount.");
            amount = userInputInteger();
        }
        return amount;
    }

    public void addOrder(Product product, int storageOption, boolean addToStorage, int amount) {
        List<BalanceStorage> currentBalance = db_conn.getCurrentBalance();
        int storageAmount = 0;
        for (BalanceStorage baSt : currentBalance) {
            if (product.getItem().equals(baSt.getProduct().getItem())
                    && storageUnitsList.get(storageOption).getCity().equals(baSt.getStorageUnit().getCity())) {
                storageAmount = (baSt.getInStock() - baSt.getReserved());
                break;
            }
        }
        if (addToStorage) {
            System.out.println("Creating order: " + product.getItem() + " - Amount: " +
                    amount + " to storage " + storageUnitsList.get(storageOption).getCity());
            db_conn.createNewOrder(amount, product, storageUnitsList.get(storageOption));
        } else {
            if ((storageAmount - amount) >= 0) {
                System.out.println("Creating order: " + product.getItem() + " - Amount: "
                        + (amount * -1) + " from storage " + storageUnitsList.get(storageOption).getCity());
                db_conn.createNewOrder((amount * -1), product, storageUnitsList.get(storageOption));
            } else {
                System.out.println("Not enough items in storage.");
                System.out.println(storageUnitsList.get(storageOption).getCity() + " has "
                        + storageAmount + " items in stock.");
            }
        }
    }

    /** Handling orders **/
    public void handleOrder(boolean sendOrder) {
        List<Order> orderList = null;
        while (orderList == null) {
            if (sendOrder) {
                orderList = getReservedOrders();
            } else {
                orderList = getIncomingOrders();
            }
            Order order = getOrderFromIDInput(orderList);
            if (order != null) {
                db_conn.orderHandlingUpdates(order);
            } else {
                System.out.println("Returning to menu...");
            }
        }
        System.out.println("-------------------------------------");
    }

    public List<Order> getReservedOrders() {
        List<Order> reservedOrders = new ArrayList<>();
        for (Order order : db_conn.getIncompleteOrders()) {
            if (order.getStatus().equals("Reserved")) {
                reservedOrders.add(order);
            }
        }
        return reservedOrders;
    }

    public List<Order> getIncomingOrders() {
        List<Order> incomingOrders = new ArrayList<>();
        for (Order order : db_conn.getIncompleteOrders()) {
            if (order.getStatus().equals("Incoming")) {
                incomingOrders.add(order);
            }
        }
        return incomingOrders;
    }

    /** Cancel order **/
    public void cancelOrder() {
        List<Order> incompleteOrders = db_conn.getIncompleteOrders();

        System.out.println("Orders not yet processed:");
        printOrders(incompleteOrders);

        if (!(incompleteOrders.isEmpty())) {
            System.out.println("Enter the order ID of the order you wish to cancel or 0 to stop cancellation:");
            int orderID = userInputInteger();

            if (orderID != 0) {
                while (!checkCancelOrder(orderID, incompleteOrders)) {
                    System.out.println("Order not found. Please enter a valid Order ID or 0 to quit:");
                    orderID = userInputInteger();
                    if (orderID == 0) {
                        break;
                    } else if (checkCancelOrder(orderID, incompleteOrders)) {
                        for (Order order : incompleteOrders)
                            if (order.getOrderID() == orderID)
                                db_conn.cancelOrder(order);
                    }
                }
            } else {
                System.out.println("Returning to menu...");
                System.out.println("-------------------------------------");
            }
        } else {
            System.out.println("Returning to menu...");
            System.out.println("-------------------------------------");
        }
    }

    public boolean checkCancelOrder(int orderID, List<Order> incompleteOrders) {
        for (Order order : incompleteOrders) {
            if (order.getOrderID() == orderID) {
                return true;
            }
        }
        return false;
    }

    /** General use **/
    public void printOrders(List<Order> orders) {
        if (!(orders.isEmpty())) {
            for (Order order : orders) {
                System.out.println("Order ID: " + order.getOrderID());
                System.out.println("Order Date: " + order.getDate());
                System.out.println("User ID: " + order.getUserID());
                System.out.println("Product: " + order.getProduct().getItem());
                System.out.println("Amount: " + order.getAmount());
                System.out.println("Storage Unit: " + order.getStorageUnit().getCity());
                System.out.println("----------------------------------------");
            }
        } else {
            System.out.println("No orders found.");
        }
    }

    public int userInputInteger() {
        Scanner userInput = new Scanner(System.in);
        int inputNumber = 0;
        try {
            inputNumber = userInput.nextInt();
        } catch (Exception e) {
            System.out.println("Please enter a valid number.");
            userInputInteger();
        }
        return inputNumber;
    }

    public Order getOrderFromIDInput(List<Order> orderList) {
        printOrders(orderList);
        if (!(orderList.isEmpty())) {
            System.out.println("Enter the orderID of the order you choose or 0 to exit.");

            int orderID = userInputInteger();
            while (orderID != 0) {
                for (Order order : orderList) {
                    if (order.getOrderID() == orderID) {
                        return order;
                    }
                }
                System.out.println("Invalid orderID. Enter a valid ID or 0 to exit.");
                orderID = userInputInteger();
            }
        }
        return null;
    }
}

