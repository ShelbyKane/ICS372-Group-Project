package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JSONHandlerTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void convertToOrder() {
    }

    @Test
    void importOrder() {


        Order orderTest = new Order(1,"ship","incoming",new Date(1515354694451L));
        orderTest.setWarehouse(-1);

        orderTest.addItem(new Item("Chair",1, 85.99));
        orderTest.addItem(new Item("Lamp",2, 32.99));
        orderTest.addItem(new Item("Rug",1, 48.95));

        Order result = JSONHandler.importOrder("src/test/resources/backups/order0.json");

        Assertions.assertEquals(orderTest,result, "Import JSON Order");
    }

    @Test
    void exportAllOrders() {
        //Orders
        Order orderTest = new Order(1,"ship","incoming",new Date(1515354694451L));
        orderTest.addItem(new Item("BROOMSTICK",1, 85.99));
        orderTest.addItem(new Item("Lamp",2, 32.99));
        orderTest.addItem(new Item("Rug",1, 48.95));
        Order orderTest2 = new Order(2,"pickup","in progress",new Date(1615354694451L));
        orderTest2.addItem(new Item("Table",1, 120.50));
        orderTest2.addItem(new Item("Chair",4, 45.00));
        orderTest2.addItem(new Item("Cushion",2, 15.75));
        Order orderTest3 = new Order(3,"ship","completed",new Date(1711111111111L));
        orderTest3.addItem(new Item("Desk",1, 199.99));
        orderTest3.addItem(new Item("Monitor",2, 149.99));
        orderTest3.addItem(new Item("Keyboard",1, 49.99));
        Order orderTest4 = new Order(4,"delivery","canceled",new Date(1650000000000L));
        orderTest4.addItem(new Item("Bed Frame",1, 299.99));
        orderTest4.addItem(new Item("Mattress",1, 399.99));
        orderTest4.addItem(new Item("JACKHAMMER",3, 19.99));
        ArrayList<Order> orderList = new ArrayList<>();
        orderList.add(orderTest);
        orderList.add(orderTest2);
        orderList.add(orderTest3);
        orderList.add(orderTest4);
        JSONHandler.exportAllOrders(orderList,"src/test/resources/previous_state.json");

    }

    @Test
    void importPreviousState() {
    }


}