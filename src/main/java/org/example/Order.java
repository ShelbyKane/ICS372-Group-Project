package org.example;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;

public class Order {

    private static int orderIndex = 0; //order number, static, incremented each time constructor is called
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
        if (orderNum > -1) {
            this.orderNumber = orderNum;

        // If the new Order does NOT have a given orderNumber, then check that the current orderIndex value isn't already in OrderList.
        } else {
            while (OrderList.exists(String.valueOf(orderIndex))) {
                //System.out.println("! order ID " + orderIndex + " already exists.");
                orderIndex++;
            }
            this.orderNumber = orderIndex;
        }

        this.type = type;                                                           // No default handling (XMLHandler reports an error / returns a null object)
        this.stage = stage;                                                         // XMLHandler defaults to "Incoming" if orderType not found in XML file
        this.date = date;                                                           // XMLHanlder defaults to today if no date given.
        this.warehouseID = -1;                                                      // !! Note: Need to handle files having a warehouse ID

        items = new ArrayList<Item>();
        orderIndex++;                                                               // Increment orderIndex to track total number of Orders
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
        if (stage.equalsIgnoreCase("incoming")){


            Scanner keyboard = new Scanner(System.in);
            System.out.print("Enter warehouse nubmer: ");
            String warehouse = keyboard.nextLine();

            //if the keyboard ID is not either 1, 2, or 3, ask the user to enter a valid warehouse ID
            while ( !warehouse.equals("1") || !warehouse.equals("2") || !warehouse.equals("3") ){
                System.out.print("Please enter a valid warehouse ID: ");
                warehouse = keyboard.nextLine();
            }

            warehouseID = Integer.parseInt(warehouse);

            stage = "in progress";

            System.out.println("Order status changed from incoming to in progress.");
        }

        else {
            System.out.println("You cannot start fulfilling an order that is in stage " + stage + ".");
        }

    }

    /**
     * This method is for when an employee completes an order
     * If the order is not currently in progress, it will not change the status, and will inform user
     * Sets the stage to completed
     */
    public void completeOrder() {

        if (stage.equals("in progress")){
            stage = "completed";
            System.out.println("Order status changed from in progress to completed. ");
        }

        else {
            System.out.println("You cannot complete an order that is not currently in progress. ");
        }
    }

    /**
     * This sets the stage to canceled
     */
    public void cancelOrder(){

        if (stage.equals("completed")){
            System.out.println("You cannot cancel an order that has already been completed. ");
        }
        else {
            stage = "canceled";
            System.out.println("The order has been canceled. ");
        }

    }

    public void reinstateOrder() {
        if (stage.equals("canceled")){
            Scanner keyboard = new Scanner(System.in);
            System.out.print("Enter warehouse nubmer: ");
            String warehouse = keyboard.nextLine();

            //if the warehouse ID is not either 1, 2, or 3, ask the user to enter a valid warehouse ID
            while ( !warehouse.equals("1") || !warehouse.equals("2") || !warehouse.equals("3") ){
                System.out.print("Please enter a valid warehouse ID: ");
                warehouse = keyboard.nextLine();
            }

            warehouseID = Integer.parseInt(warehouse);

            stage = "in progress";

            System.out.println("Order status changed from canceled to in progress.");
        }

        else{
            System.out.println("You cannot reinstate an order that is not currently canceled. ");
        }
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

        return totalCost;
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

    /**
     * toString returns a reasonably legible string representation of the order, displaying all info and items
     * @return the order but in readable format
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("Order number: " + this.orderNumber);
        sb.append("\nOrder type: " + this.type);
        sb.append("\nOrder stage: " + this.stage);
        sb.append("\nOrder date: " + this.date);
        sb.append("\nItems: \n");
        for (Item i : items) {
            sb.append(i.stringForOrder());
        }
        sb.append("\nTotal cost of order: " + getTotalCost());

        return String.valueOf(sb);
    }


}
