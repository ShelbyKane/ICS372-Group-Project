package org.example;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.ArrayList;

public class AutoImporter {
    public static ArrayList<String> imported_files = new ArrayList<>();  // store names of imported files to avoid duplicates
    private static final String IMPORTED_FILES_LOG = "imported_files.log";

    // Load the list of previously imported files from persistent storage
    public static void load_imported_files() {
        try {
            File logFile = new File(IMPORTED_FILES_LOG);
            if (logFile.exists()) {
                imported_files = new ArrayList<>(Files.readAllLines(logFile.toPath()));
            }
        } catch (IOException e) {
            System.out.println("Could not load imported files log");
        }
    }

    // Save the list of imported files to persistent storage
    public static void save_imported_files() {
        try {
            Files.write(new File(IMPORTED_FILES_LOG).toPath(), imported_files);
        } catch (IOException e) {
            System.out.println("Could not save imported files log");
        }
    }

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
        if (!importFolder.exists() || importFolder.listFiles() == null) {
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
                    System.out.println("---");
                }

                else if (file.getName().endsWith(".xml")) {  // import XML files
                    System.out.println("Importing XML file");
                    order_list.import_xml_file(file.getPath());
                    copy_to_backup(file);
                    System.out.println("---");
                }
                
                save_imported_files();  // Save the list after each import
            }
        }
    }
}
