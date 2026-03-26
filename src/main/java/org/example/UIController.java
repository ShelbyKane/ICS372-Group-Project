package org.example;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class UIController{

    private ArrayList<Order> orders;
    private ArrayList<Integer> orderNumbers;
    private Order currOrder;
    private int currWarehouse;
    private int inputOrderNumber;


   @FXML
   private Label headerLabel;
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

        orders = JSONHandler.importPreviousState("src/data/previous_state.json");
        updateOrders();

    }

    private void updateOrders(){
        orderListView.getItems().setAll(FXCollections.observableArrayList(orders));
    }
    private void updateItems(){
        if(currOrder != null){

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
                }
        );
    }

    public void refreshUI(){
        updateOrders();
//        updateItems();
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

    }


    /**
     *completeOrderButton completes an order that is currently in progress.
     * @param e
     */
    public void completeOrderButton(ActionEvent e){
        if(currOrder == null) return;
        if(currOrder.getStage().equals("in progress")) currOrder.completeOrder();
        refreshUI();
    }


    /**
     *
     * @param e
     */
    public void reinstateOrderButton(ActionEvent e){
        if (currOrder == null) return;
        if(currOrder.getStage().equals("canceled")) currOrder.reinstateOrder();
        refreshUI();
    }



    public void exitButton(ActionEvent e){
        Platform.exit();
    }


    public void changeWarehouseComboBox(ActionEvent e){

    }
}

