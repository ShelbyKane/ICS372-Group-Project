package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        Order orderTest = new Order(0,"ship","incoming",new Date(1515354694451L));
        orderTest.addItem(new Item("Chair",1, 85.99));
        orderTest.addItem(new Item("Lamp",2, 32.99));
        orderTest.addItem(new Item("Rug",1, 48.95));

        Order result = JSONHandler.importOrder("src/test/resources/backups/order0.json");

        Assertions.assertEquals(orderTest,result, "Import JSON Order");
    }

    @Test
    void importPreviousState() {
    }

    @Test
    void exportAllOrders() {
    }
}