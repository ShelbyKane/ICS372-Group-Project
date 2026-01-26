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
        this.stage = "incoming";

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

        //Display order number, date and stage
        System.out.println("Order number: " + orderNumber +
                            "\nOrder date: " + date +
                            "\nOrder stage: " + stage +
                            "\nItems:"
                            );
        //iterate through ArrayList items to print out the information about each item
        for (Item i : items) {
            System.out.println("Item name: " + i.getName() +
                                "\nItem quantity: " + i.getQuantity() +
                                "\nItem price: " + i.getPrice());

        }
        // total cost of the order
        System.out.println("\nTotal cost of order: " + getTotalCost());
    }


}
