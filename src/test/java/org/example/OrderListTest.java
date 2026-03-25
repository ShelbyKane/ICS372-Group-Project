package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;

import static org.junit.jupiter.api.Assertions.*;

class OrderListTest {

    @BeforeEach
    void setUp(){
        OrderList orderList = new OrderList("src/test/resources/backups");
    }
    @Test
    void exists() {
    }

    @Test
    void is_empty() {
    }

    @Test
    void order_idx() {
    }

    @Test
    void get_size() {
    }

    @Test
    void get_order() {
    }

    @Test
    void get_last_order() {
    }

    @Test
    void view() {
    }

    @Test
    void edit_stage_completed() {
    }

    @Test
    void edit_stage_processing() {
    }

    @Test
    void edit_stage_canceled() {
    }

    @Test
    void edit_stage_reinstate() {
    }

    @Test
    void view_all() {
    }

    @Test
    void view_completed_orders() {
    }

    @Test
    void view_uncompleted_orders() {
    }

    @Test
    void view_all_detailed() {
    }

    @Test
    void view_completed_orders_detailed() {
    }

    @Test
    void view_uncompleted_orders_detailed() {
    }

    @Test
    void import_json_or_xml() {
    }

    @Test
    void import_xml_file() {
    }

    @Test
    void import_json_file() {
    }

    @Test
    void export_json_file() {
    }
}