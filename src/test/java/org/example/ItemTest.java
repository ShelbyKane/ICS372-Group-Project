package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item item;
    @BeforeEach
    void setUp(){
        item = new Item("Chair", 200, 2.50);
    }
    @Test
    void getName() {
        String result = item.getName();
        Assertions.assertEquals("Chair", result, "Item Name");
    }

    @Test
    void setName() {
        item.setName("Table");
        String reslut = item.getName();
        Assertions.assertEquals("Table", reslut, "Item Set Name");
    }

    @Test
    void getQuantity() {
        int result = item.getQuantity();
        Assertions.assertEquals(200, result, "Item Quantity");
    }

    @Test
    void setQuantity() {
        item.setQuantity(300);
        int result = item.getQuantity();
        Assertions.assertEquals(300, result, "Item Set Quantity");
    }

    @Test
    void getPrice() {
        double result = item.getPrice();
        Assertions.assertEquals(2.50, result, "Item Price");
    }

    @Test
    void setPrice() {
        item.setPrice(5.00);
        double result = item.getPrice();
        Assertions.assertEquals(5.00, result, "Set Item Price");

    }

    @Test
    void getTotal() {
        double result = item.getTotal();
        Assertions.assertEquals(500.00, result, "Get total");
    }

}