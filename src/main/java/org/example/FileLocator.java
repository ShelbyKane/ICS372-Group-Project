package org.example;

import java.io.File;

public class FileLocator {
    private String backup_dir = System.getProperty("user.dir");
    private String filename = "order";
    private String path = backup_dir + File.separator + filename;

    public String store_backup(int id) {
        filename = String.format("\\order_%d.xml", id);

        return backup_dir + File.separator + "\\src\\backups" + filename;
    }
}
