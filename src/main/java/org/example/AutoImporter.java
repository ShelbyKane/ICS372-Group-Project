package org.example;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AutoImporter {
    public static java.util.ArrayList<String> imported_files = new java.util.ArrayList<>();  // store names of imported files to avoid duplicates

    public static void copy_to_backup(File file) {
        try {
            File backupDir = new File("backupOrders");
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }
            File newFile = new File(backupDir, file.getName());
            Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied to backup: " + file.getName());
        }
        catch (Exception e) {
            System.out.println("Backup copy failed");
        }
    }

    public static void read_folder(String folderName, OrderList order_list) {
    File importFolder = new File(folderName);
        if (!importFolder.exists() || importFolder.listFiles() ==null){
            return;
        }

    File[] importFiles = importFolder.listFiles(); // get all da files

    if (importFiles != null) { // check folder existance and is not empty
        for (File file : importFiles) {  // loop thru each file in folder
            if (imported_files.contains(file.getPath())) {  // check folder existance and is not empty
                continue;
            }
            imported_files.add(file.getPath());  // add file to imported list
            System.out.println("Checking file: " + file.getName());

            if (file.getName().endsWith(".json")) {  // import JSON files
                System.out.println("Importing JSON file");
                order_list.import_json_file(file.getPath());
                copy_to_backup(file);
            }

            else if (file.getName().endsWith(".xml")) {  // import XML files
                System.out.println("Importing XML file");
                order_list.import_xml_file(file.getPath());
                copy_to_backup(file);
            }
        }
    }
}
}