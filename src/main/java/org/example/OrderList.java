package org.example;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderList {
    private ArrayList<Order> orders = new ArrayList<>();    // list of orders
    private static HashMap<String, Integer> index = new HashMap<>();   // index to keep track of orders

    public OrderList(ArrayList<Order> orders){
        this.orders = orders;
    }

    public static boolean exists(String order_id) {
        return index.containsKey(order_id);
    }

    public boolean is_empty() {
        return orders.isEmpty();
    }

    public int order_idx(String order_id) {
        return index.get(order_id);
    }

    public int get_size() {
        return orders.size();
    }


    public Order get_order(String order_id) {
        return orders.get(index.get(order_id));
    }

    public Order get_last_order() {
        return orders.getLast();
    }

    public void view(String orderNumber) {      // please use exists to check first, prevent error
        orders.get(index.get(orderNumber)).displayOrder();
    }

    public void edit_stage_completed(String orderNumber) {
        orders.get(index.get(orderNumber)).completeOrder();
    }

    public void edit_stage_processing(String orderNumber) {
        orders.get(index.get(orderNumber)).startFulfilling();
    }

    public void edit_stage_canceled(String orderNumber) {
        orders.get(index.get(orderNumber)).cancelOrder();
    }

    public void edit_stage_reinstate(String orderNumber) {
        orders.get(index.get(orderNumber)).reinstateOrder();
    }

    public void view_all() {           // use is_empty first, prevent error
        for (Order order : orders) {
            System.out.printf("Order number: %d\tTotal cost: %.2f\tOrder status: %s\n", order.getOrderNumber(), order.getTotalCost(), order.getStage());
        }
        System.out.println("Total orders: " + orders.size());
    }
    public String getSimpleString(){
        String fin = "";
        for (Order order : orders) {
            fin = String.format(fin + "Order number: %d\tTotal cost: %.2f\tOrder status: %s\n", order.getOrderNumber(), order.getTotalCost(), order.getStage());
        }
        fin = String.format(fin + "Total orders: " + orders.size());
        return fin;
    }

    public void view_completed_orders() {
        int count = 0;
        for (Order order : orders) {
            if (order.getStage().equalsIgnoreCase("completed")) {
                System.out.printf("Order number: %d\tTotal cost: %.2f\tOrder status: %s\n", order.getOrderNumber(), order.getTotalCost(), order.getStage());
                count += 1;
            }
        }
        System.out.println("Total orders: " + count);
    }

    public void view_uncompleted_orders() {
        int count = 0;
        for (Order order : orders) {
            if (!order.getStage().equalsIgnoreCase("completed")) {
                System.out.printf("Order number: %d\tTotal cost: %.2f\tOrder status: %s\n", order.getOrderNumber(), order.getTotalCost(), order.getStage());
                count += 1;
            }
        }
        System.out.println("Total orders: " + count);
    }

    public void view_all_detailed() {
        for (Order order : orders) {
            order.displayOrder();
        }
        System.out.println("Total orders: " + orders.size());
    }

    public void view_completed_orders_detailed() {
        int count = 0;
        for (Order order : orders) {
            if (order.getStage().equalsIgnoreCase("completed")) {
                order.displayOrder();
                count += 1;
            }
        }
        System.out.println("Total orders: " + count);
    }

    public void view_uncompleted_orders_detailed() {
        int count = 0;
        for (Order order : orders) {
            if (!order.getStage().equalsIgnoreCase("completed")) {
                order.displayOrder();
                count += 1;
            }
        }
        System.out.println("Total orders: " + count);
    }

    public void import_xml_file(String fileName) {
        File file_location = new File(fileName);

        try (FileInputStream valid = new FileInputStream(file_location)) {
            Order new_order = XMLHandler.convertXMLToOrder((file_location).getAbsolutePath());

            if (new_order != null) {
                int current_idx = orders.size();

                orders.add(new_order);
                if (current_idx == 0 ) {
                    index.put(String.valueOf(orders.getFirst().getOrderNumber()), 0);

                } else {
                    index.put(String.valueOf(orders.get(current_idx).getOrderNumber()), current_idx);
                }
            }


        } catch (ParserConfigurationException | IOException | SAXException ex) {
            System.out.println("OrderList error 1");
        }
    }

    public void import_json_file(String fileName) {
        File file_location = new File(fileName);

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
            }

        } catch (IOException ex) {
            System.out.println("OrderList error 2");
        }
    }

    public void export_json_file(String path) {
        JSONHandler.exportAllOrders(orders, path);//Call the exportAllOrders method to write all orders to JSON;
    }

    public ArrayList<Order> get_uncompleted_orders() {
        ArrayList<Order> uncompleted_orders = new ArrayList<>();
        for (Order order : orders) {
            if (!order.getStage().equalsIgnoreCase("completed")) {
                uncompleted_orders.add(order);
            }
        }

        return uncompleted_orders;
    }

    public boolean import_json_or_xml(String path) throws ParserConfigurationException, SAXException {
        File file = new File(path);

        try (FileInputStream valid = new FileInputStream(file)) {
            if (file.getAbsolutePath().endsWith(".json")) {
                import_json_file(path);
            } else if (file.getAbsolutePath().endsWith(".xml")) {
                import_xml_file(path);
            } else {
                return false;
            }

        } catch (IOException ex) {
            return false;
        }

        return true;
    }
}
