package org.example;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.util.Scanner;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        boolean sys_active = true;  // software will not terminate on its own

        Scanner keyboard = new Scanner(System.in);  // user will interact with software using keyboard
        String temp;                                // store input

        OrderList order_list = new OrderList();
        String order_id;

        // software will continue to run until user terminate
        while (sys_active) {

            System.out.println("(Programming purpose only, will not be in the final state.)\n");
            System.out.println("Enter 1: view one order\n2: edit order stage\n3: view orders");
            System.out.println("4: import json file\n5: export json file\n0: terminate software");
            temp = keyboard.nextLine();

            switch(temp){
                case "1":   // show order detail
                    System.out.println("(Programming purpose only, will not be in the final state.)\n");
                    System.out.println("View order.");
                    System.out.println("Enter \"*\" back to main menu.");
                    System.out.print("Enter order id: ");
                    temp = keyboard.nextLine();

                    if (temp.equalsIgnoreCase("*")) break;              // back to main menu
                    if (order_list.is_empty() || !order_list.exists(temp)) break;   // no order found

                    order_list.view(temp);

                    break;

                case "2":   // edit order stage/states
                    System.out.println("(Programming purpose only, will not be in the final state.)\n");
                    System.out.println("Edit order stage.");
                    System.out.println("\"*\" main menu");
                    System.out.print("Enter order id: ");
                    temp = keyboard.nextLine();

                    if (temp.equals("*")) break;                                    // back to main menu
                    if (order_list.is_empty() || !order_list.exists(temp)) break;   // no order found
                    order_id = temp;
                    order_list.view(order_id);

                    System.out.println("Enter 1: in-progress\n2: completed\n3: cancelled\n4: reinstate");
                    temp = keyboard.nextLine();
                    switch (temp) {
                        case "1" -> order_list.get_order(order_id).startFulfilling();
                        case "2" -> order_list.get_order(order_id).completeOrder();
                        case "3" -> order_list.get_order(order_id).cancelOrder();
                        case "4" -> order_list.get_order(order_id).reinstateOrder();
                        default -> System.out.println("Error");
                    }
                    break;

                case "3":   // display orders: completed orders, uncompleted orders, or all orders
                    System.out.println("(Programming purpose only, will not be in the final state.)\n");
                    System.out.println("Display orders.");
                    System.out.print("1: completed orders\n2: uncompleted orders\n3: all orders");
                    System.out.println("4: completed orders (all data)\n5: uncompleted orders (all data)\n6: all orders (all data)");
                    System.out.println("*: main menu\nelse: error and still back to main menu");
                    temp = keyboard.nextLine();

                    if (temp.equalsIgnoreCase("*")) break;              // back to main menu
                    if (order_list.is_empty() || !order_list.exists(temp)) break;   // no order found

                    temp = keyboard.nextLine();
                    switch (temp) {
                        case "1" -> order_list.view_completed_orders();
                        case "2" -> order_list.view_uncompleted_orders();
                        case "3" -> order_list.view_all();
                        case "4" -> order_list.view_completed_orders_detailed();
                        case "5" -> order_list.view_uncompleted_orders_detailed();
                        case "6" -> order_list.view_all_detailed();
                        default -> System.out.println("Error");
                    }
                    break;

                    // may be removed from main menu
                case "4":   // import json file
                    System.out.println("(Programming purpose only, will not be in the final state.)\n");
                    System.out.println("Import JSON file.");
                    System.out.println("Format: double backslash (\\\\) or forward slash (/)");
                    System.out.println("Example: C:\\\\Users\\\\User... or C:/Users/User...");
                    System.out.print("\nEnter filepath to continue, \"*\" for return main menu, else nothing happen: ");
                    temp = keyboard.nextLine();

                    if (temp.equalsIgnoreCase("*")) break;  // back to main menu

                    order_list.import_json_file(temp);
                    break;

                case "5":
                    System.out.println("(Programming purpose only, will not be in the final state.)\n");
                    System.out.println("Export all orders into one JSON file.");
                    System.out.print("Enter \"1\" to export all orders into JSON file, \"*\" for return main menu, else nothing happen: ");
                    temp = keyboard.nextLine();

                    if (temp.equalsIgnoreCase("*")) break;  // back to main menu

                    if (temp.equals("1")) {                             // export orders into a JSON file
                        if (!order_list.is_empty()) {
                            String exportPath = "orders_export.json";   //Name of file & File created in the project root directory
                            order_list.export_json_file(exportPath);
                        }
                    }
                    break;


                case "0":   // terminate program
                    System.out.println("(Programming purpose only, will not be in the final state.)\n");
                    System.out.println("Exit software.");
                    System.out.print("Enter \"1\" to terminate the program, else nothing happen: ");
                    temp = keyboard.nextLine();

                    if (temp.equals("1")) sys_active = false;   // terminate software
                    break;

                default:    // invalid input for options
                    System.out.printf("There is no option: %s\n", temp);
            }

        }

        keyboard.close();
    }

}
