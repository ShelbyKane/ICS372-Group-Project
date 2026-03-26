package org.example;

import java.util.Objects;

//**
public class Item {
    private String name;
    private int quantity;
    private double price;

    public Item(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * getters and setters
     *
     * please note that the setters for price and
     * name might not be needed later on
     **/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return quantity * price;
    }
    @Override
    public String toString() {

        return "Item{" + "name=" + name + ", quantity=" + quantity + ", price=" + price + '}';
    }

    /**
     * Uses a stringbuilder object to create a string representation of the order
     * specifically for the to string method in the order class
     * @return a string representation of the item specifically formatted for displaying the entire order
     */
    public String stringForOrder(){
        StringBuilder sb = new StringBuilder();
        sb.append("\tName: "); //add a tab so it looks clean
        sb.append(this.name);
        sb.append("\n\t"); //adding a new line then a tab so it fits neatly within the order
        sb.append(this.quantity);
        sb.append("\n\t");
        sb.append(String.format("%.2f", this.price));

        return String.valueOf(sb);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Item)) return false;

        Item otherItem = (Item) o;

        return otherItem.quantity == quantity
                && otherItem.price == price
                && Objects.equals(otherItem.name, name);
    }
    @Override
    public int hashCode(){
        return Objects.hash(quantity,price,name);
    }

}
