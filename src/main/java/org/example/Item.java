package org.example;
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

}
