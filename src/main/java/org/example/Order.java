package org.example;
import java.util.ArrayList;
import java.util.Date;

public class Order {

    private static int orderIndex = 0; //order number, static, incremented each time constructor is called
    private int orderNumber; //identifying number for the orders
    private String type; //type of order, can be either pick up or ship
    private ArrayList<Item> items; //list of items in the order
    private String stage; //stage order is in, can be incoming, fulfilling or completed
    private Date date; //date order was placed


    /**
     * Constructor for order
     * @param type is whether the order is pick up or ship
     * @param date is the date the order was placed
     */
    public Order(String type, Date date) {
        orderNumber = orderIndex++;
        this.type = type;
        this.date = date;
        this.stage = "incoming"; //automatically sets the status as incomming

        items = new ArrayList<Item>();


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


    /**
     * addItem adds an item to the list of items
     * @param i
     */
    public void addItem(Item i){
        items.add(i);
    }

    /**
     * This method is for when an employee starts fulfilling the order
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
        sb.append("\nItems: \n");
        for (Item i : items) {
            sb.append(i.stringForOrder());
        }
        sb.append("\nTotal cost of order: " + getTotalCost());

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
