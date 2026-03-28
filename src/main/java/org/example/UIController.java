package org.example;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;

public class UIController{


    private String STATE_PATH = "orders_export.json";
    private String ORDER_PATH = "downloadedOrders";
    private String BACKUP_PATH = "backupOrders";

    private ArrayList<Order> orders;
    private ArrayList<Integer> orderNumbers;
    private Order currOrder;
    private int currWarehouse;
    private int inputOrderNumber;


   @FXML
   private Label headerLabel;
   @FXML
   private Label itemLabel;
   @FXML
   private Label notifLabel;
   @FXML
   private TextArea itemTextArea;
   @FXML
    private TextField orderNumberField;
   @FXML
   private ListView<Order> orderListView;
   @FXML
   private ListView<String> itemListView;
   @FXML
    private Button displayOrderButton;
   @FXML
   private ComboBox<String> warehouseComboBox;

   //BUTTONS
   @FXML
    private Button beginOrderButton;
   @FXML
   private Button refreshButton;
   @FXML
    private Button completeOrderButton;
   @FXML
    private Button cancelOrderButton;
   @FXML
    private Button reinstateOrderButton;
   @FXML
    private Button exitButton;



    public UIController(){ // creates controller instance
    }

    public void initialize(){
        warehouseComboBox.getItems().setAll("Warehouse 1", "Warehouse 2", "Warehouse 3");
        currWarehouse = -1;
        setListeners();
        setupOrderCells();

        orders = JSONHandler.importPreviousState(STATE_PATH);
        refreshUI();


    }



    //LOGIC SETUP

    /**
     * Saves the state of the program to a JSON file at STATE_PATH
     * @return
     */
    public boolean saveState(){return JSONHandler.exportAllOrders(orders, STATE_PATH);}

    /**
     * Loads all the new orders into the current orders List from ORDER_PATH
     */
    private void loadNewOrders(){
        File folder = new File(ORDER_PATH);
        File[] files = folder.listFiles();

        if(files == null) return;
        for(File file: files) {
            if(!file.isFile()) continue;
            String name = file.getName().toLowerCase();

            if(name.endsWith(".json")) {
                orders.add(JSONHandler.importOrder(file.getPath()));
                moveFile(file.getPath(),BACKUP_PATH);
            } else if(name.endsWith(".xml")) {
                try{
                    orders.add(XMLHandler.convertXMLToOrder(file.getAbsolutePath()));
                    moveFile(file.getAbsolutePath(), BACKUP_PATH);
                } catch(Exception e){
                    continue;
                }

            }
        }
    }

    /**
     * Moves file from one location to another folder
     * @param originPath File to be moved
     * @param folderDir Destination Folder
     */
    private void moveFile(String originPath, String folderDir){
        Path source = Paths.get(originPath);
        Path targetDir = Paths.get(folderDir);
        Path target = targetDir.resolve((source.getFileName()));
        try{
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Problem backing up file");
        }
    }

    //UI CONTROLS

    /**
     * Updates the Order listView
     */
    private void updateOrders(){
        try{
            orders.sort(Comparator.comparingInt(Order::getOrderNumber));
            orderListView.getItems().setAll(FXCollections.observableArrayList(orders));
        } catch (NullPointerException e){
        }

    }

    /**
     * Updates the Item listView
     */
    private void updateItems(){
        if(currOrder != null){

            itemLabel.setText("Order " + currOrder.getOrderNumber() + " Items");
            ArrayList<String> stringList = new ArrayList<>();
            int count = 0;
            for(Item i : currOrder.getItems()) {
                StringBuilder sb = new StringBuilder("\n");
                sb.append("[Item " + (++count));
                sb.append("]\nName: " + i.getName());
                sb.append(", Quantity: " + i.getQuantity());
                sb.append(String.format(", Price: %.2f", i.getPrice()));
                stringList.add(String.valueOf(sb));
            }
            itemListView.getItems().setAll(FXCollections.observableArrayList(stringList));

        }
    }

    /**
     * Sets up listeners for both the Warehouse combo box and the Order
     */
    private void setListeners(){
        orderListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldOrder, selectedOrder) -> {
                    if (selectedOrder != null) {
                        currOrder = selectedOrder;
                        updateItems();
                    }
                });

        warehouseComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    switch (newValue) {
                        case "Warehouse Selection" -> notifLabel.setText("Please select a warehouse");
                        case "Warehouse 1" -> currWarehouse = 1;
                        case "Warehouse 2" -> currWarehouse = 2;
                        case "Warehouse 3" -> currWarehouse = 3;
                    }
                    notifLabel.setText("");
                    refreshUI();
                }
        );
    }

    /**
     * Sets up the Order listView cells to have certain format dependent on their properties
     * This was partially written with ChatGPT
     */
    private void setupOrderCells() {
        orderListView.setCellFactory(list -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);

                if (empty || order == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    return;
                }

                String type_icon = (order.getType().equalsIgnoreCase("ship")
                        || order.getType().equalsIgnoreCase("delivery")) ? "🚚" : "🏬";
                String stage_icon = "";
                String baseStyle = "-fx-text-fill: black;";
                switch (order.getStage().toLowerCase()) {
                    case "completed":
                        baseStyle +=  "-fx-background-color: lightgreen;";
                        stage_icon = "(✅)";
                        break;
                    case "in progress":
                        baseStyle += ("-fx-background-color: lightblue;");

                        stage_icon = "(🛒)";
                        break;
                    case "incoming":
                        baseStyle += ("-fx-background-color: lightblue;");
                        stage_icon = "(📨)";
                        break;
                    case "canceled":
                        baseStyle += ("-fx-background-color: lightcoral;");
                        stage_icon = "(❌)";
                        break;
                    default:
                        baseStyle += "";
                }

                StringBuilder sb = new StringBuilder();

                sb.append(type_icon);
                sb.append("| Order #" + order.getOrderNumber());
                sb.append(stage_icon + " | 📅 " + order.getDate());
                if (order.getWarehouse() >= 1) sb.append(" | 🏠 " + order.getWarehouse());
                sb.append(" | 💲 " + order.getTotalCost());

                setText(String.valueOf(sb));

                if(isSelected()){
                    setStyle(baseStyle + "-fx-border-color: black; -fx-border-width: 2;");
                } else {
                    setStyle(baseStyle);
                }

            }
        });
    }

    /**
     * Refreshes the listViews on the UI to be updated new orders or items
     */
    public void refreshUI(){
        loadNewOrders();
        updateOrders();
        updateItems();

    }

    //BUTTONS

    /**
     *Sets the selected Order's stage to complete
     *Selected Order comes from listView
     */
    public void beginProcessingOrderButton(ActionEvent e) {
        if (currOrder == null) return;
        if(!currOrder.getStage().equals("incoming")){
            notifLabel.setText("Order Must be incoming");
            return;
        }
        if (currWarehouse < 1){
            notifLabel.setText("Please select warehouse");
            return;
        }

        currOrder.startFulfilling();
        currOrder.setWarehouse(currWarehouse);


        refreshUI();
        saveState();

    }

    /**
     *Sets the selected Order's stage to complete
     *Selected Order comes from listView
     */
    public void cancelOrderButton(ActionEvent e) {
        if(currOrder == null) return;
        if(currOrder.getStage().equals("in progress") || currOrder.getStage().equals("incoming")) currOrder.cancelOrder();
        refreshUI();
        saveState();

    }

    /**
     *Sets the selected Order's stage to complete
     *Selected Order comes from listView
     */
    public void completeOrderButton(ActionEvent e){
        if(currOrder == null) return;
        if(currOrder.getStage().equals("in progress")) currOrder.completeOrder();
        refreshUI();
        saveState();
    }

    /**
     *Sets the selected Order's stage from canceled to incoming
     *Selected Order comes from listView
     */
    public void reinstateOrderButton(ActionEvent e){
        if (currOrder == null) return;
        if(currOrder.getStage().equals("canceled")) currOrder.reinstateOrder();
        refreshUI();
        saveState();
    }

    /**
     *Saves the current state of the program and then closes the program
     *
     */
    public void exitButton(ActionEvent e){
        saveState();
        Platform.exit();
    }

    /**
     * Refreshes the UI listViews
     */
    public void refreshButton(ActionEvent e){refreshUI();}

}

