package org.example;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class UIController{

    private ArrayList<Order> orders;
    private ArrayList<Integer> orderNumbers;

   @FXML
   private Label headerLabel;
   @FXML
   private TextArea orderDisplayTextArea;
   @FXML
    private TextField orderNumberField;
   @FXML
    private Button displayOrderButton;
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

    private int inputOrderNumber;


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

    public void setOrders(ArrayList<Order> orders){

        this.orders = orders;
        orderNumbers = new ArrayList<>();

        for(Order o : orders){
            orderNumbers.add(o.getOrderNumber());
        }
    }

    /**
     * This method gets the order number from the text box in the UI
     * @return true if the order number was successful, false if the order number was not successful
     */
    public boolean getOrderNumberFromUI() {
        //Try block makes sure that the user did enter an integer. If not, it says invalid order number
        try {
            inputOrderNumber = Integer.parseInt(orderNumberField.getText());
            if(orderNumbers.contains(inputOrderNumber)){
                return true;
            }
            else {
                headerLabel.setText("Invalid Order Number");
                return false;
            }

        }
        catch (NumberFormatException exception) {
            headerLabel.setText("Invalid Order Number");
        }
        catch (Exception ex) {
            headerLabel.setText("There was some error");
        }
        return false;
    }


    /**
     * This takes an action event, when the Begin Processing button is hit, and pulls the order number from the textbox
     * and begins processing the order
     * If the order number isn't an integer, it'll tell the user it isn't valid
     * If the order number isn't a current order, it'll tell the user that it isn't an order that exists
     * @param e
     */
    public void beginProcessingOrderButton(ActionEvent e) {

        if (getOrderNumberFromUI()) {
            if(orderNumbers.contains(inputOrderNumber)){
                //grab the order from the arraylist that matches, and change the status to in progress
                for (Order o : orders) {
                    if (o.getOrderNumber() == inputOrderNumber) {
                        if (o.getStage().equals("incoming")){
                            o.startFulfilling();
                            headerLabel.setText(o.getOrderNumber() + " stage changed to in progress.");
                            return;
                        }
                        else {
                            headerLabel.setText("You cannot start processing an order that has already been started.");
                        }
                    }
                }
            }
            else {
                headerLabel.setText("That order number, while definitely an integer, does not exist.");
            }
        }

    }


    /**
     *When the cancel order button is hit, it validates that the order number is valid, then tries to cancel the order
     * if the order is canceled or completed, it will let the user know
     * otherwise, the order will be canceled
     * @param e
     */
    public void cancelOrderButton(ActionEvent e) {
        if (getOrderNumberFromUI()) {
            //loop through orders to make sure we're editing the right one
            for(Order o : orders) {
                //if the order number is the input order number
                if (o.getOrderNumber() == inputOrderNumber) {
                    if (o.getStage().equals("cancelled")){
                        headerLabel.setText(o.getOrderNumber() + " has already been cancelled.");
                    }
                    else if (o.getStage().equals("completed")){
                        headerLabel.setText(o.getOrderNumber() + " you can't cancel an order that has already been completed.");
                    }
                    else {
                        o.cancelOrder();
                        headerLabel.setText(o.getOrderNumber() + " has been cancelled.");
                    }
                }
            }
        }

    }


    /**
     *completeOrderButton completes an order that is currently in progress.
     * @param e
     */
    public void completeOrderButton(ActionEvent e){
        if (getOrderNumberFromUI()) {
            for(Order o : orders) {
                if (o.getOrderNumber() == inputOrderNumber) {
                    if (o.getStage().equals("completed")){
                        headerLabel.setText(o.getOrderNumber() + " has already been completed.");
                    }
                    else if (o.getStage().equals("canceled")){
                        headerLabel.setText(o.getOrderNumber() + " has been cancelled. You cannot complete a canceled order.");
                    }
                    else if (o.getStage().equals("incoming")){
                        headerLabel.setText(o.getOrderNumber() + " is currently incoming. You must begin processing an order before it can be completed.");
                    }
                    else {
                        o.completeOrder();
                    }
                }
            }
        }
    }


    /**
     *
     * @param e
     */
    public void reinstateOrderButton(ActionEvent e){
        if (getOrderNumberFromUI()) {
            for(Order o : orders) {
                if (o.getOrderNumber() == inputOrderNumber) {
                    if (o.getStage().equals("canceled")){
                        o.reinstateOrder();
                        headerLabel.setText(o.getOrderNumber() + " has been reinstated.");
                    }
                    else {
                        headerLabel.setText("You cannot reinstate an order that isn't currently canceled.");
                    }
                }
            }
        }
    }

    /**
     * Displays the order in the UI
     * @param e
     */
    public void displayOrderButton(ActionEvent e){
        if (getOrderNumberFromUI()) {
            for(Order o : orders) {
                if (o.getOrderNumber() == inputOrderNumber) {
                    orderDisplayTextArea.setText(o.toString());
                }
            }
        }
    }

    public void exitButton(ActionEvent e){
        Platform.exit();
    }
}
