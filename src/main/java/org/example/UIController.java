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


    private String STATE_PATH = "src/data/states/previous_state.json";
    private String ORDER_PATH = "src/data";
    private String BACKUP_PATH = "src/backups";

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
    /*
        no longer need arg constructor since the FXMLLoader will always call the no-arg constructor

        kept it in just in case we need it

     */
    /*public UIController(ArrayList<Order> orders){
        this.orders = orders;
        orderNumbers = new ArrayList();

        for(Order o : orders){
            orderNumbers.add(o.getOrderNumber());
        }
    }
     */
    public void initialize(){
        warehouseComboBox.getItems().setAll("Warehouse 1", "Warehouse 2", "Warehouse 3");
        currWarehouse = -1;
        setListeners();
        setupCellFactory();

        orders = JSONHandler.importPreviousState(STATE_PATH);
        refreshUI();


    }

    private void updateOrders(){
        orders.sort(Comparator.comparingInt(Order::getOrderNumber));
        orderListView.getItems().setAll(FXCollections.observableArrayList(orders));
    }
    private void updateItems(){
        if(currOrder != null){

            itemLabel.setText("Order " + currOrder.getOrderNumber() + " Items");
            ArrayList<String> stringList = new ArrayList<>();
            int count = 0;
            for(Item i : currOrder.getItems()){
                StringBuilder sb = new StringBuilder("\n");
                sb.append("[Item " + (++count));
                sb.append("]\nName: " + i.getName());
                sb.append(", Quantity: " + i.getQuantity());
                sb.append(String.format(", Price: %.2f", i.getPrice()));
                stringList.add(String.valueOf(sb));
//                sb.append(i.stringForOrder());
            }
//            itemTextArea.setText(String.valueOf(sb));
            itemListView.getItems().setAll(FXCollections.observableArrayList(stringList));


//            itemTextArea.setText(currOrder.getItems().toString());
        }
    }


    public void refreshUI(){
        loadNewOrders();
        updateOrders();
        updateItems();

    }

    //LOGIC SETUP

    public boolean saveState(){
        return JSONHandler.exportAllOrders(orders, STATE_PATH);
    }

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

    //This function was partially written by ChatGPT
    private void setupCellFactory() {
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

                String type_icon = order.getType().equalsIgnoreCase("ship") ? "🚚" : "🏬";
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


    //BUTTONS
    /**
     * This takes an action event, when the Begin Processing button is hit, and pulls the order number from the textbox
     * and begins processing the order
     * If the order number isn't an integer, it'll tell the user it isn't valid
     * If the order number isn't a current order, it'll tell the user that it isn't an order that exists
     * @param e
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
     *When the cancel order button is hit, it validates that the order number is valid, then tries to cancel the order
     * if the order is canceled or completed, it will let the user know
     * otherwise, the order will be canceled
     * @param e
     */
    public void cancelOrderButton(ActionEvent e) {
        if(currOrder == null) return;
        if(currOrder.getStage().equals("in progress") || currOrder.getStage().equals("incoming")) currOrder.cancelOrder();
        refreshUI();
        saveState();

    }


    /**
     *completeOrderButton completes an order that is currently in progress.
     * @param e
     */
    public void completeOrderButton(ActionEvent e){
        if(currOrder == null) return;
        if(currOrder.getStage().equals("in progress")) currOrder.completeOrder();
        refreshUI();
        saveState();
    }


    /**
     *
     * @param e
     */
    public void reinstateOrderButton(ActionEvent e){
        if (currOrder == null) return;
        if(currOrder.getStage().equals("canceled")) currOrder.reinstateOrder();
        refreshUI();
        saveState();
    }



    public void exitButton(ActionEvent e){
        saveState();
        Platform.exit();
    }

    public void refreshButton(ActionEvent e){
        refreshUI();
    }


}

