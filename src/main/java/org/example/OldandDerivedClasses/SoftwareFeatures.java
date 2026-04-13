package org.example.OldandDerivedClasses;

import org.example.Order;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SoftwareFeatures {
    private FileLocator backups_dir = new FileLocator();
    private OrderList order_list = new OrderList();
    private Timer auto_import_timer;
    private int reload_order_size;

    public SoftwareFeatures() {
        ReloadFiles.start_reload(order_list, backups_dir.get_dir());
        reload_order_size = order_list.get_size();
        AutoImporter.load_imported_files();// load previously imported files to avoid re-importing them
        start_auto_import();
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
        save_backup(order_list.get_order(order_number));
        return true;
    }

    public boolean edit_stage_completed(String order_number) throws ParserConfigurationException, TransformerException {
        if (!order_list.get_order(order_number).getStage().equalsIgnoreCase("in progress")) {
            return false;
        }
        order_list.get_order(order_number).completeOrder();
        save_backup(order_list.get_order(order_number));
        return true;
    }

    public boolean edit_stage_canceled(String order_number) throws ParserConfigurationException, TransformerException {
        if (!order_list.get_order(order_number).getStage().equalsIgnoreCase("incoming")) {
            return false;
        }
        order_list.get_order(order_number).cancelOrder();
        save_backup(order_list.get_order(order_number));
        return true;
    }

    public boolean edit_stage_reinstate(String order_number) throws ParserConfigurationException, TransformerException {
        if (!order_list.get_order(order_number).getStage().equalsIgnoreCase("canceled")) {
            return false;
        }
        order_list.get_order(order_number).reinstateOrder();
        save_backup(order_list.get_order(order_number));
        return true;
    }

    public String get_uncompleted_orders_info() {
        StringBuilder prints = new StringBuilder();
        ArrayList<Order> uncompleted_orders = order_list.get_uncompleted_orders();
        for (Order order : uncompleted_orders) {
            prints.append(String.format("%-20d%-20s%.2f\n", order.getOrderNumber(), order.getStage(), order.getTotalCost()));
        }
        return prints.toString();
    }

    public String get_all_orders_info() {
        StringBuilder prints = new StringBuilder();
        ArrayList<Order> uncompleted_orders = order_list.get_all_orders();
        for (Order order : uncompleted_orders) {
            prints.append(order.toLongString());
            prints.append("\n");
        }
        return prints.toString();
    }

    public ArrayList<Order> get_uncompleted_orders() {
        return order_list.get_uncompleted_orders();
    }

    public boolean import_file(String path) throws ParserConfigurationException, SAXException, TransformerException {
        if (!order_list.import_json_or_xml(path)) return false;
        save_backup(order_list.get_last_order());
        return true;
    }

    public void export_json_file() {
        order_list.export_json_file("orders_export.json");
    }

    //start the auto-import timer that checks downloadedOrders folder every 3 seconds
    public void start_auto_import() {
        if (auto_import_timer != null) {
            auto_import_timer.cancel(); //cancel existing timerr
        }
        
        auto_import_timer = new Timer();
        auto_import_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AutoImporter.read_folder("downloadedOrders", order_list);
                try {
                    if (reload_order_size != order_list.get_size()) save_orders_at_idx_n();
                } catch (ParserConfigurationException | TransformerException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 3000); //start immediately, repeat every 3 secondss

        File importFolder = new File("downloadedOrders");  // created File object. identify folder where files posted
        File[] importFiles = importFolder.listFiles();              // get all files inside that folder

        File testFile = new File("downloadedOrders/test.xml");
        AutoImporter.copy_to_backup(testFile);
    }

    //stop the auto-import timer
    public void stop_auto_import() {
        if (auto_import_timer != null) {
            auto_import_timer.cancel();
            auto_import_timer.purge();
            auto_import_timer = null;
        }
    }

    public void save_orders_at_idx_n() throws ParserConfigurationException, TransformerException {
        for (int i = reload_order_size; i < order_list.get_size(); i++) {
            save_backup(order_list.get_order_by_idx(i));
        }
        reload_order_size = order_list.get_size();
    }

    public void save_backup(Order order) throws ParserConfigurationException, TransformerException {
        backups_dir.save_backup(order);
    }
}
