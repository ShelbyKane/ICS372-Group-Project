package org.example;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;

public class SoftwareFeatures {
    private FileLocator backups_dir = new FileLocator();
    private OrderList order_list = new OrderList();

    public SoftwareFeatures() {
        ReloadFiles.start_reload(order_list, backups_dir.get_dir());
    }

    public boolean zero_orders() {
        return order_list.is_empty();
    }

    public boolean valid_order(String order_number) {
        return OrderList.exists(order_number);
    }

    public String view_an_order(String order_number) {
        return order_list.get_order(order_number).toString();
    }

    public boolean edit_stage_fulfilling(String order_number) throws ParserConfigurationException, TransformerException {
        if (!order_list.get_order(order_number).getStage().equalsIgnoreCase("incoming")) {
            return false;
        }
        order_list.get_order(order_number).startFulfilling();
        backups_dir.save_backup(order_list.get_order(order_number));
        return true;
    }

    public boolean edit_stage_completed(String order_number) throws ParserConfigurationException, TransformerException {
        if (!order_list.get_order(order_number).getStage().equalsIgnoreCase("in progress")) {
            return false;
        }
        order_list.get_order(order_number).completeOrder();
        backups_dir.save_backup(order_list.get_order(order_number));
        return true;
    }

    public boolean edit_stage_canceled(String order_number) throws ParserConfigurationException, TransformerException {
        if (!order_list.get_order(order_number).getStage().equalsIgnoreCase("incoming")) {
            return false;
        }
        order_list.get_order(order_number).cancelOrder();
        backups_dir.save_backup(order_list.get_order(order_number));
        return true;
    }

    public boolean edit_stage_reinstate(String order_number) throws ParserConfigurationException, TransformerException {
        if (!order_list.get_order(order_number).getStage().equalsIgnoreCase("canceled")) {
            return false;
        }
        order_list.get_order(order_number).reinstateOrder();
        backups_dir.save_backup(order_list.get_order(order_number));
        return true;
    }

    public String get_uncompleted_orders_info() {
        String prints = "";
        ArrayList<Order> uncompleted_orders = order_list.get_uncompleted_orders();
        for (Order order : uncompleted_orders) {
            prints = prints + String.format("%d\t\t\t\t\t%s\t\t\t\t\t%.2f\n", order.getOrderNumber(), order.getStage(), order.getTotalCost());
        }
        return prints;
    }

    public ArrayList<Order> get_uncompleted_orders() {
        return order_list.get_uncompleted_orders();
    }

    public boolean import_file(String path) throws ParserConfigurationException, SAXException, TransformerException {
        if (!order_list.import_json_or_xml(path)) return false;
        backups_dir.save_backup(order_list.get_last_order());
        return true;
    }

    public void export_json_file() {
        order_list.export_json_file("orders_export.json");
    }
}
