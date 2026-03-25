package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    @BeforeEach
    void setUp(){
        order = new Order(1,"ship","incoming",new Date(1515354694451L));
    }
    @Test
    void getOrderNumber() {
       int result = order.getOrderNumber();
        Assertions.assertEquals(1,result,"Get Order Number");
    }

    @Test
    void getType() {
        String result = order.getType();
        Assertions.assertEquals("ship",result,"Get Order Number");
    }

    @Test
    void getStage() {
        String result = order.getStage();
        Assertions.assertEquals("incoming",result,"Get Order Stage");
    }

    @Test
    void getItems() {
        ArrayList<Item> testList = new ArrayList<>();
        testList.add(new Item("Chair", 200, 5.00));
        testList.add(new Item("Table", 75, 2.50));

        order.addItem(new Item("Chair", 200, 5.00));
        order.addItem(new Item("Table", 75, 2.50));

        ArrayList<Item> result = order.getItems();

        Assertions.assertEquals(testList, result, "Get Items");

    }

    @Test
    void getDate() {

        Date result = order.getDate();
        Assertions.assertEquals(new Date(1515354694451L),result,"Get Order Date");
    }

    @Test
    void setStage() {
        order.setStage("canceled");
        String result = order.getStage();

        Assertions.assertEquals("canceled", result, "Set stage");

    }

    @Test
    void addItem() {
        order.addItem(new Item("Chair", 200, 5.00));
        order.addItem(new Item("Table", 75, 2.50));
        int result = order.getItems().size();
        Assertions.assertEquals(2, result, "Add Item");
    }

    @Test
    void startFulfilling() {
        order.startFulfilling();
        Assertions.assertEquals("in progress", order.getStage(),"Start Fulfilling Order");
    }

    @Test
    void completeOrder() {
        order.completeOrder();
        Assertions.assertEquals("completed", order.getStage(),"Complete Order");
    }

    @Test
    void cancelOrder() {
        order.cancelOrder();
        Assertions.assertEquals("canceled", order.getStage(),"Cancel order");
    }

    @Test
    void reinstateOrder() {
        order.reinstateOrder();
        Assertions.assertEquals("in progress", order.getStage(),"Reinstate Order");
    }

    @Test
    void getTotalCost() {
        order.addItem(new Item("Chair", 200, 5.00));
        order.addItem(new Item("Table", 75, 2.50));
        double result = order.getTotalCost();
        Assertions.assertEquals(1187.50, result, "Get total Cost");
    }

}