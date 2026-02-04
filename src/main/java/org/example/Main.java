package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.Scanner;

public class Main {
    static void main(String[] args) throws IOException {

        boolean sys_active = true;  // software will not terminate on its own

        Scanner keyboard = new Scanner(System.in);  // user will interact with software using keyboard
        String temp; // store input
        int order_num; // order number

        ArrayList<Order> orders = new ArrayList<>();    // list of orders
        HashMap<String, Integer> index = new HashMap<>();   // index to keep track of orders

        System.out.println("--------------------Tracking System--------------------\n");
        System.out.println("NOTICE: Please strictly follow the instructions to ensure the software runs correctly.\nThank you.\n");

        // software will continue to run until user terminate
        while (sys_active) {
            // option menu: edit order status, display orders, import and export JSON file, and exit software
            System.out.println("--------------------Menu--------------------\n1) Show Order Detail\n2) Edit Order Status\n3) Display Orders\n4) Import JSON File\n5) Export JSON File\n0) Exit");
            System.out.print("Please enter the option number: ");
            temp = keyboard.nextLine();

            System.out.println();

            switch(temp){
                case "1":   // show order detail
                    System.out.println("--------------------Show Order Detail--------------------");
                    System.out.print("Please enter the order number: ");
                    temp = keyboard.nextLine();
                    System.out.println();

                    if (orders.isEmpty() || !index.containsKey(temp)) {
                        System.out.println("Cannot find order.");
                        System.out.println();
                        break;
                    }

                    // order number is valid, the process continue
                    order_num = index.get(temp);
                    orders.get(order_num).displayOrder();
                    System.out.println();
                    break;

                case "2":   // edit order stage/states
                    System.out.println("--------------------Edit Order Staus--------------------");
                    System.out.print("Please enter the order number: ");
                    temp = keyboard.nextLine();
                    System.out.println();

                    if (orders.isEmpty() || !index.containsKey(temp)) {
                        System.out.println("Cannot find order.");
                        System.out.println();
                        break;
                    }

                    // order number is valid, the process continue
                    order_num = index.get(temp);
                    orders.get(order_num).displayOrder();
                    System.out.println();

                    // change status to: in progress or completed
                    System.out.println("Change order status to:\na) In-Progress\nb) Completed");
                    System.out.print("Please enter option: ");
                    temp = keyboard.nextLine();

                    // user will get notification if edit is successful or not
                    if (status_change(orders.get(order_num), temp)) {
                        System.out.println("Change successfully.");
                        System.out.printf("Order number: %d status changed to %s\n", orders.get(order_num).getOrderNumber(), orders.get(order_num).getStage());
                    } else {
                        System.out.println("Change failed");
                        System.out.println();
                    }

                    break;

                case "3":   // display orders: completed orders, uncompleted orders, or all orders
                    System.out.println("--------------------Display Orders--------------------");
                    System.out.println("Select order options:\na) Completed orders\nb) Uncompleted orders\nc) All orders");
                    System.out.print("Please enter option: ");
                    temp = keyboard.nextLine();
                    System.out.println();

                    display_orders(temp, orders);   // call display_orders method

                    break;

                case "4":   // import json file
                    System.out.println("--------------------Import JSON File--------------------");
                    System.out.println("Format: double backslash (\\\\) or forward slash (/)");
                    System.out.println("Example: C:\\\\Users\\\\User... or C:/Users/User...");
                    System.out.print("Please enter the JSON file location: ");
                    temp = keyboard.nextLine();
                    System.out.println();

                    File file_location = new File(temp);

                    // user will get notification if import is successful or not
                    if (file_location.exists()) {
                        int current_idx = orders.size();
                        orders.add(JSONHandler.convertToOrder(file_location.getAbsolutePath()));
                        if (current_idx == 0 ) {
                            index.put(String.valueOf(orders.getFirst().getOrderNumber()), 0);

                        } else {
                            index.put(String.valueOf(orders.get(current_idx).getOrderNumber()), current_idx);
                        }

                        System.out.println("Import is successful!\n");

                    } else {
                        System.out.println("Cannot find file.\n");
                    }

                    break;

                case "5":   // Export (NEED THE EXPORT CLASS)
                    System.out.println("--------------------Export JSON File--------------------");

                    break;

                case "0":   // terminate program
                    System.out.println("--------------------Exit--------------------");
                    System.out.println("a) Yes\nb) No");
                    System.out.print("Are you sure you want to exit? ");
                    temp = keyboard.nextLine();
                    System.out.println();

                    if (temp.equalsIgnoreCase("a")) {
                        sys_active = false;
                    }

                    break;

                default:    // invalid input for options
                    System.out.printf("There is no option: %s\n", temp);
            }

        }

        keyboard.close();
        System.out.println("--------------------Tracking System CLOSED--------------------\n");
    }

    /**
     * A method to change the order's status/stage to: in-progress or completed.
     * @param order An Order object.
     * @param option The user-entered option to change the status.
     * @return True/false value to indicate the action is successful or failure.
     */
    public static boolean status_change(Order order, String option) {
        switch(option) {
            case "a" -> order.startFulfilling();
            case "b" -> order.completeOrder();
            default -> {return false;}
        }

        return true;
    }

    /**
     * A method to display orders: uncompleted, completed, or all.
     * @param option The user-entered option to choose what orders to display.
     * @param list The ArrayList<Order> of the stored orders.
     */
    public static void display_orders(String option, ArrayList<Order> list) {
        int count = 0;

        switch(option) {
            case "a":   // completed orders
                for (Order i : list) {
                    if (i.getStage().equalsIgnoreCase("completed")) {
                        System.out.printf("Order number: %d\tTotal cost: %.2f\n", i.getOrderNumber(), i.getTotalCost());
                        count += 1;
                    }
                }

                System.out.printf("\nTotal of completed orders: %d\n", count);
                count = 0;
                break;

            case "b":   // uncompleted orders
                for (Order i : list) {
                    if (!i.getStage().equalsIgnoreCase("completed")) {
                        System.out.printf("Order number: %d\tTotal cost: %.2f\n", i.getOrderNumber(), i.getTotalCost());
                        count += 1;
                    }
                }

                System.out.printf("\nTotal of uncompleted orders: %d\n", count);
                count = 0;
                break;

            case "c":   // all orders
                for (Order i : list) {
                    System.out.printf("Order number: %d\tTotal cost: %.2f\tOrder status: %s\n", i.getOrderNumber(), i.getTotalCost(), i.getStage());
                    count += 1;
                }

                System.out.printf("\nTotal number of orders: %d\n", count);
                count = 0;
                break;

            default:
                System.out.println("Invalid input.");
        }

        System.out.println();
    }

}
