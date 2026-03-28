package org.example.OldandDerivedClasses;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReloadFiles {
    public static void start_reload(OrderList orders, String path) {
        Path dir = Path.of(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                orders.import_xml_file(file.toString());
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.out.println("ReloadFiles error 1");
        }

    }
}
