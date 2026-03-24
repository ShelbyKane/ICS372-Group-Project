package org.example;
import java.io.File;
public class AutoImporter {
public static void read_folder(String folderName, OrderList order_list) {
    File importFolder = new File(folderName);      // identify folder
    File[] importFiles = importFolder.listFiles(); // get all da files

    if (importFiles != null) {            // check folder existance and is not empty
        for (File file : importFiles) {  // loop thru each file in folder
            System.out.println("Checking file: " + file.getName());

            if (file.getName().endsWith(".json")) {  // import JSON files
                System.out.println("Importing JSON file");
                order_list.import_json_file(file.getPath());
            }

            else if (file.getName().endsWith(".xml")) {  // import XML files
                System.out.println("Importing XML file");
                order_list.import_xml_file(file.getPath());
            }
        }
    }
}
}