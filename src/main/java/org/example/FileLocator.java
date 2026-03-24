package org.example;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;

public class FileLocator {
    private final String backup_dir = System.getProperty("user.dir") + "\\src\\backups";

    /**
     * Create the filename and its file location. Can be used to get path
     * or simply creating the path for a specific order.
     * @param id    Order number (order id)
     * @return      A file location of this order.
     */
    public String get_path(int id) {
        String filename = String.format("\\order_%d.xml", id);

        return backup_dir + filename;
    }

    /**
     * Create its file location using given filename (in specific format).
     * @param filename  Filename, eg order_1.xml
     * @return          A file location of this order.
     */
    public String get_path(String filename) {
        return backup_dir + File.separator + filename;
    }

    public String get_dir() {
        return dir;
    }

    public void save_backup(Order order) throws ParserConfigurationException, TransformerException {
        get_path(order.getOrderNumber());
        XMLHandler.exportXMLOrder(order, path);
    }
}
