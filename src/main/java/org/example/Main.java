package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        boolean sys_active = true;  // software will not terminate on its own

        Scanner keyboard = new Scanner(System.in);  // user will interact with software using keyboard
        String temp; // store input
        int order_num; // order number

        ArrayList<Order> orders = new ArrayList<>();    // list of orders
        HashMap<String, Integer> index = new HashMap<>();   // index to keep track of orders

        System.out.println("*********************************** Tracking System ***********************************\n");
        System.out.println("NOTICE: Please strictly follow the instructions to ensure the software runs correctly.\nThank you.\n");

        // software will continue to run until user terminate
        while (sys_active) {
            // option menu: edit order status, display orders, import and export JSON file, and exit software
            System.out.println("======================= Main Menu =======================\n1) Show Order Detail\n2) Edit Order Status\n3) Display Orders\n4) Import JSON File\n5) Export JSON File\n0) Exit");
            System.out.print("\nPlease select an option: ");
            temp = keyboard.nextLine();

            switch(temp){
                case "1":   // show order detail
                    System.out.println("------------------- Show Order Detail -------------------");
                    System.out.println("Enter q to return to main menu.\n");
                    System.out.print("Please enter the order number: ");
                    temp = keyboard.nextLine();
                    System.out.println("---------------------------------------------------------");

                    if (temp.equalsIgnoreCase("q")) {
                        System.out.println("Return to main menu.");
                        break;

                    } else if (orders.isEmpty() || !index.containsKey(temp)) {
                        System.out.println("Cannot find the order.");
                        break;
                    }

                    // order number is valid, the process continue
                    order_num = index.get(temp);
                    orders.get(order_num).displayOrder();
                    break;

                case "2":   // edit order stage/states
                    System.out.println("-------------------- Edit Order Staus --------------------");
                    System.out.println("Enter q to return to main menu.\n");
                    System.out.print("Please enter the order number: ");
                    temp = keyboard.nextLine();
                    System.out.println("---------------------------------------------------------");

                    if (temp.equalsIgnoreCase("q")) {
                        System.out.println("Return to main menu.");
                        break;

                    } else if (orders.isEmpty() || !index.containsKey(temp)) {
                        System.out.println("Cannot find the order.");
                        break;
                    }

                    // order number is valid, the process continue
                    order_num = index.get(temp);
                    orders.get(order_num).displayOrder();
                    System.out.println("---------------------------------------------------------");

                    // change status to: in progress or completed
                    System.out.println("Enter q to return to main menu.\n");
                    System.out.println("Change order status to:\na) In-Progress\nb) Completed");
                    System.out.print("\nPlease select an option: ");
                    temp = keyboard.nextLine();
                    System.out.println("---------------------------------------------------------");

                    if (temp.equalsIgnoreCase("q")) {
                        System.out.println("Return to main menu.");
                        break;

                    }

                    if (temp.equalsIgnoreCase("b") && orders.get(order_num).getStage().equalsIgnoreCase("incoming")) {
                        System.out.println("You are attempting to skip an order stage: incoming -> completed\n(skipping \"in-progress\")");
                        System.out.println("a) Yes\nb) No");
                        System.out.print("\nDo you want to proceed? ");
                        temp = keyboard.nextLine();
                        System.out.println("---------------------------------------------------------");

                        if (temp.equalsIgnoreCase("a")) {
                            temp = "b";

                        } else {
                            System.out.println("Cancelled change.");
                            break;
                        }

                    }

                    if (status_change(orders.get(order_num), temp)) {
                        System.out.println("Change successfully.");
                        System.out.printf("Order number: %d\nNEW status: %s\n", orders.get(order_num).getOrderNumber(), orders.get(order_num).getStage());

                    } else {
                        System.out.println("Change failed");
                    }

                    break;

                case "3":   // display orders: completed orders, uncompleted orders, or all orders
                    System.out.println("-------------------- Display Orders --------------------");
                    System.out.println("Enter q to return to main menu.\n");
                    System.out.println("Orders in options a, b, c display order's order id, total price, status/stage");
                    System.out.println("Orders in options d, e, f display order's order id, type, status/stage, date, item(s), total price\n");
                    System.out.println("Order display options:\na) Completed orders\nb) Uncompleted orders\nc) All orders");
                    System.out.println("d) Completed orders (detail)\ne) Uncompleted orders (detail)\nf) All orders (detail)");
                    System.out.print("\nPlease select an option: ");
                    temp = keyboard.nextLine();
                    System.out.println("---------------------------------------------------------");

                    if (temp.equalsIgnoreCase("q")) {
                        System.out.println("Return to main menu.");
                        break;
                    }

                    display_orders(temp, orders);   // call display_orders method
                    break;

                case "4":   // import json file
                    System.out.println("-------------------- Import JSON File --------------------");
                    System.out.println("Enter q to return to main menu.\n");
                    System.out.println("Format: double backslash (\\\\) or forward slash (/)");
                    System.out.println("Example: C:\\\\Users\\\\User... or C:/Users/User...");
                    System.out.print("\nPlease enter the JSON file location: ");
                    temp = keyboard.nextLine();
                    System.out.println("---------------------------------------------------------");

                    if (temp.equalsIgnoreCase("q")) {
                        System.out.println("Return to main menu.");
                        break;
                    }

                    File file_location = new File(temp);

                    try (FileInputStream valid = new FileInputStream(file_location)) {
                        Order new_order = JSONHandler.convertToOrder((file_location).getAbsolutePath());

                        if (new_order != null) {
                            int current_idx = orders.size();

                            orders.add(new_order);
                            if (current_idx == 0 ) {
                                index.put(String.valueOf(orders.getFirst().getOrderNumber()), 0);

                            } else {
                                index.put(String.valueOf(orders.get(current_idx).getOrderNumber()), current_idx);
                            }

                            System.out.println("Import is successful!");

                        } else {
                            System.out.println("Import failed.");
                        }


                    } catch (FileNotFoundException ex) {
                        System.out.println("Cannot find the file.");
                    }

                    break;

                case "5":
                    System.out.println("-------------------- Export JSON File -------------------");
                    System.out.println("There is " + orders.size() + " order(s) present.");
                    System.out.print("a) Yes\nb) No (main menu)\n\nDo you wish to continue: ");

                    temp = keyboard.nextLine(); // ask user for input

                    if (temp.equalsIgnoreCase("b")) {  //if user wants to quit, go back to main menu
                        System.out.println("Return to main menu.");
                        break;
                    } else if (temp.equalsIgnoreCase("a")) {
                        if (orders.isEmpty()) {
                            System.out.println("There are no orders to export.");
                            break; //return to main menu
                        }

                        String exportPath = "orders_export.json"; //Name of file & File created in the project root directory
                        JSONHandler.exportAllOrders(orders, exportPath);//Call the exportAllOrders method to write all orders to JSON

                        //Export successful! User will get notification and the file will be created in the project root directory
                        System.out.println("Exported " + orders.size() + " orders to " + exportPath);
                    } else {
                        System.out.println("There is no option: " + temp);
                    }

                    break;


                case "0":   // terminate program
                    System.out.println("------------------------- Exit -------------------------");
                    System.out.println("a) Yes\nb) No");
                    System.out.print("\nAre you sure you want to exit? ");
                    temp = keyboard.nextLine();

                    if (temp.equalsIgnoreCase("a")) {
                        sys_active = false;

                    } else {
                        if (!temp.equalsIgnoreCase("b")) {
                            System.out.println("---------------------------------------------------------");
                            System.out.println("There is no option: " + temp);
                        }
                        System.out.println("---------------------------------------------------------");
                        System.out.println("Return to main menu.");
                    }

                    break;

                default:    // invalid input for options
                    System.out.printf("There is no option: %s\n", temp);
            }

        }

        keyboard.close();
        System.out.println("******************************* Tracking System CLOSED *******************************\n");
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
        String keyword;

        if (list.isEmpty()) {
            System.out.println("There are no orders in the software.");
            return;
        }

        switch(option) {
            case "a":   // completed orders
                for (Order i : list) {
                    if (i.getStage().equalsIgnoreCase("completed")) {
                        System.out.printf("Order number: %d\tTotal cost: %.2f\tOrder status: %s\n", i.getOrderNumber(), i.getTotalCost(), i.getStage());
                        count += 1;
                    }
                }

                keyword = "completed";
                break;

            case "b":   // uncompleted orders
                for (Order i : list) {
                    if (!i.getStage().equalsIgnoreCase("completed")) {
                        System.out.printf("Order number: %d\tTotal cost: %.2f\tOrder status: %s\n", i.getOrderNumber(), i.getTotalCost(), i.getStage());
                        count += 1;
                    }
                }

                keyword = "uncompleted";
                break;

            case "c":   // all orders
                for (Order i : list) {
                    System.out.printf("Order number: %d\tTotal cost: %.2f\tOrder status: %s\n", i.getOrderNumber(), i.getTotalCost(), i.getStage());
                    count += 1;
                }

                keyword = "all";
                break;

            case "d":   // completed orders DETAIL
                for (Order i : list) {
                    if (i.getStage().equalsIgnoreCase("completed")) {
                        i.displayOrder();
                        count += 1;
                        System.out.print("---------------------------------------------------------");
                    }
                }

                keyword = "completed";
                break;

            case "e":   // uncompleted orders DETAIL
                for (Order i : list) {
                    if (!i.getStage().equalsIgnoreCase("completed")) {
                        i.displayOrder();
                        count += 1;
                        System.out.print("---------------------------------------------------------");
                    }
                }

                keyword = "uncompleted";
                break;

            case "f":   // all orders DETAIL
                for (Order i : list) {
                    i.displayOrder();
                    count += 1;
                    System.out.print("---------------------------------------------------------");
                }

                keyword = "all";
                break;

            default:
                System.out.printf("There is no option: %s\n", option);
                return;
        }

        if (count != 0) System.out.println();
        System.out.printf("The total number of %s orders is %d.\n", keyword, count);
    }

}
