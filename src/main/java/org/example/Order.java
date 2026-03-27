package org.example;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;

public class Order {

    private static int orderIndex = 1; //order number, static, incremented each time constructor is called
    private int orderNumber; //identifying number for the orders
    private String type; //type of order, can be either pick up or ship or direct delivery
    private ArrayList<Item> items; //list of items in the order
    private String stage; //stage order is in, can be incoming, fulfilling or completed
    private Date date; //date order was placed

    //Added for project 2/////////////////////
    private int warehouseID;
    private String source;


    /**
     * Constructor for order
     * @param type is whether the order is pick up or ship
     * @param date is the date the order was placed
     */
    public Order(String type, Date date) {

        // If current orderIndex value is already found in OrderList, increment orderIndex +1.
        while (OrderList.exists(String.valueOf(orderIndex))) {
            //System.out.println("! order ID " + orderIndex + " already exists.");
            orderIndex++;
        }
        orderNumber = orderIndex;
        this.type = type;
        this.date = date;
        this.stage = "incoming"; //automatically sets the status as incoming
        this.warehouseID = -1;

        items = new ArrayList<Item>();
        orderIndex++;
    }

    // Izzy - Draft. Adding an Order constructor with more detail (need this to track changes to orders over time, and to account for XML files having specified order IDs)
    // NOTE: Writing this w/ XMLHandler in mind. Need to ensure this works with JSONHandler
    public Order(int orderNum, String type, String stage, Date date) {

        // If orderNum > -1, then use orderNumber provided by import file. Increment orderIndex to keep track of total # of orders.
        // NOTE: XMLHandler checks if orderNum already exists in OrderList (returns an error message / null object).
        if (orderNum > 0) {
            this.orderNumber = orderNum;
            if (orderNum > orderIndex) orderIndex = orderNum + 1;

        // If the new Order does NOT have a given orderNumber, then check that the current orderIndex value isn't already in OrderList.
        } else {

            this.orderNumber = orderIndex++;

        }

        this.type = type;                                                           // No default handling (XMLHandler reports an error / returns a null object)
        this.stage = stage;                                                         // XMLHandler defaults to "Incoming" if orderType not found in XML file
        this.date = date;                                                           // XMLHanlder defaults to today if no date given.
        this.warehouseID = -1;                                                      // !! Note: Need to handle files having a warehouse ID

        items = new ArrayList<Item>();
                                                             // Increment orderIndex to track total number of Orders
    }

    //Getters ///////////////////////////////////////////////////////////
    public int getOrderNumber() {
        return orderNumber;
    }

    public String getType() {
        return type;
    }

    public String getStage() {
        return stage;
    }

    public ArrayList<Item> getItems(){
        return items;
    }

    public Date getDate(){
        return date;
    }

    public void setStage(String s) {
        this.stage = s;
    }

    public void setWarehouse(int id){
        this.warehouseID = id;
    }

    public int getWarehouse() {return this.warehouseID;}
    /**
     * addItem adds an item to the list of items
     * @param i
     */
    public void addItem(Item i){
        items.add(i);
    }

    /**
     * This method is for when an employee starts fulfilling the order
     * It will ask the user to enter the warehouse ID for the warehouse that will be fulfilling the order
     * If an invalid warehouseID is entered, it will keep asking them to reenter until a valid warehouse ID is entered
     * Sets the stage to in progress
     */
    public void startFulfilling() {
       stage = "in progress";

    }

    /**
     * This method is for when an employee completes an order
     * Sets the stage to completed
     */
    public void completeOrder() {
        stage = "completed";
    }

    /**
     * This sets the stage to canceled
     */
    public void cancelOrder(){
        stage = "canceled";
        warehouseID = -1;
    }

    public void reinstateOrder() {
            stage = "incoming";
    }

    /**
     * This method calculates the total cost of all items in the order
     * @return the total cost of the order
     */
    public double getTotalCost() {
        double totalCost = 0;
        for (Item i : items){
            totalCost+= i.getTotal();
        }

        return Math.round(totalCost * 100.00) / 100.00;
    }

    /**
     * displayOrder prints the order to output
     */
    public void displayOrder() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("Order number: " + this.orderNumber);
        sb.append("\nOrder type: " + this.type);
        sb.append("\nOrder stage: " + this.stage);
        sb.append("\nOrder date: " + this.date);
        sb.append("\nItems: ");
        for (Item i : items) {
            sb.append("\n" + i.stringForOrder());
        }
        sb.append(String.format("\nTotal cost of order: %.2f", getTotalCost()));

        System.out.println(String.valueOf(sb));
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("\n");
        sb.append("Order Number: " + this.orderNumber);
        sb.append(" | Type: " + this.type);
        sb.append(" | Date: " + this.date);
        sb.append("\nStage: " + this.stage);
        sb.append(String.format(" | Total Price: %.2f", this.getTotalCost()));
        if(!(this.stage.equals("incoming")) && this.warehouseID > 0) sb.append(" | Warehouse: " + this.warehouseID);

        return String.valueOf(sb);
    }
    /**
     * toString returns a reasonably legible string representation of the order, displaying all info and items
     * @return the order but in readable format
     */

    public String toLongString() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("Order number: " + this.orderNumber);
        sb.append("\nOrder type: " + this.type);
        sb.append("\nOrder stage: " + this.stage);
        sb.append("\nOrder date: " + this.date);
        sb.append("\nItems: \n");
        for (Item i : items) {
            sb.append(i.stringForOrder());
        }
        sb.append(String.format("\nTotal cost of order: %.2f", getTotalCost()));

        return String.valueOf(sb);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Order)) return false;

        Order otherOrder = (Order) o;
//        return otherOrder.getOrderNumber() == this.orderNumber
//                && otherOrder.getType().equals(this.type)
//                && otherOrder.getDate().equals(this.date)
//                && otherOrder.getStage().equals(this.stage)
//                && otherOrder.getTotalCost() == this.getTotalCost();
        return otherOrder.orderNumber == this.orderNumber
                && Objects.equals(otherOrder.type, type)
                && Objects.equals(otherOrder.date, date)
                && Objects.equals(otherOrder.stage, stage)
                && otherOrder.getTotalCost() == this.getTotalCost();
    }
    @Override
    public int hashCode(){
        return Objects.hash(orderNumber,type,date,stage,this.getTotalCost());
    }
}
