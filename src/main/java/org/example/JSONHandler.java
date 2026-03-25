package org.example;
import java.io.FileWriter;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JSONHandler {

    /** This method converts a JSON file to an Order object based off of a given directory for the file.
     *  The method will add the type and date of an order and then all the Item objects.
     *
     * @param fileLocation String of directory for file to be parsed
     * @return Order object created from JSON file
     * | null if the file isn't parsed properly
     */
    //This method becomes redundant if using importOrder
    public static Order convertToOrder(String fileLocation){

        try (FileReader reader = new FileReader(fileLocation)){

            JSONParser parser = new JSONParser();
            JSONObject fullFileObj = (JSONObject) parser.parse(reader);
            JSONObject orderObj = (JSONObject) fullFileObj.get("order");


            //Get object versions of order details
            Object typeObj = orderObj.get("type");
            Object dateObj = orderObj.get("order_date");
            Object itemsObj = orderObj.get("items");

            //Check Types and verify details are valid
            String type = ((typeObj instanceof String)) ? (String) typeObj : "Not Available";
//            type = (type.equals("ship") || type.equals("pickup")) ? type : "Not Available";

            Date date = ((dateObj instanceof Number)) ? new Date(((Number)dateObj).longValue()) : new Date();

            Order order = new Order(type, date);        //Begin Order creation

            //Get items, convert and place into order object before returning
            JSONArray items = (itemsObj instanceof JSONArray) ? (JSONArray) itemsObj : new JSONArray();
            for(Item i : convertItems(items)){
                order.addItem(i);
            }

            return order;

        } catch(IOException | ParseException | NullPointerException e){
            System.out.println("Parse Exception: There was an error reading the JSON file. " +
                    "please confirm order is in correct format and try again");

        }

        return null;
    }

    /**
     * Converts a JSONObject into an Order Object
     * @param obj JSONObject to be converted
     * @return Order derived from JSONObject
     */
    private static Order objectToOrder(Object obj){

        JSONObject orderObj = (JSONObject) obj;
        Object typeObj = orderObj.get("type");
        Object dateObj = orderObj.get("order_date");
        Object itemsObj = orderObj.get("items");
        Object stageObj = orderObj.get("stage");
        Object orderNumObj = orderObj.get("orderNum");


        //Check Types and verify details are valid
        String type = ((typeObj instanceof String)) ? (String) typeObj : "Not Available";
//            type = (type.equals("ship") || type.equals("pickup")) ? type : "Not Available";
        String stage = ((stageObj instanceof String)) ? (String) stageObj : "incoming";
        int orderNum = (orderNumObj instanceof Number) ? ((Number) orderNumObj).intValue() : -1;
        Date date = ((dateObj instanceof Number)) ? new Date(((Number)dateObj).longValue()) : new Date();

        Order order = new Order(orderNum, type, stage, date);        //Begin Order creation

        //Get items, convert and place into order object before returning
        JSONArray items = (itemsObj instanceof JSONArray) ? (JSONArray) itemsObj : new JSONArray();
        for(Item i : convertItems(items)){
            order.addItem(i);
        }

        return order;
    }

    /**
     * Import a singular JSON order
     * @param fileLocation
     * @return
     */
    public static Order importOrder(String fileLocation){
        try (FileReader reader = new FileReader(fileLocation)){

            JSONParser parser = new JSONParser();
            JSONObject fullFileObj = (JSONObject) parser.parse(reader);
            JSONObject orderObj = (JSONObject) fullFileObj.get("order");

            return objectToOrder(orderObj);


        } catch(IOException | ParseException | NullPointerException e){
            System.out.println("Parse Exception: There was an error reading the JSON file. " +
                    "please confirm order is in correct format and try again");

        }
        return null;
    }


    public static ArrayList<Order> importPreviousState(String fileLocation){
        ArrayList<Order> orderList = new ArrayList<>();

        try (FileReader reader = new FileReader(fileLocation)){
            JSONParser parser = new JSONParser();
            JSONObject fullFileObj = (JSONObject) parser.parse(reader);
            JSONArray listObj = (JSONArray) fullFileObj.get("orders");

            for(Object obj : listObj){
                orderList.add(objectToOrder(obj));
            }


        } catch(IOException | ParseException | NullPointerException e){
            System.out.println("Parse Exception: There was an error reading the JSON file. " +
                    "please confirm order is in correct format and try again");

        }

        return orderList;
    }

    /** This converts a JSONArray Object to an ArrayList of Item objects
     *
     * @param jsonItems JSONArray
     * @return ArrayList<Item>
     */
    private static ArrayList<Item> convertItems(JSONArray jsonItems){
        ArrayList<Item> itemList = new ArrayList<>();

        for (Object object : jsonItems){

            //Get objects for Item and its details
            JSONObject item = (JSONObject) object;
            Object nameObj = item.get("name");
            Object quantityObj = item.get("quantity");
            Object priceObj = item.get("price");

            //Check type and set if invalid
            //!Figure out new failsafe method?
            String name = (nameObj instanceof String) ? (String) nameObj : "No Item name found";
            int quantity = (quantityObj instanceof Number) ? ((Number) quantityObj).intValue() : 0;
            double price = (priceObj instanceof Number) ? ((Number) priceObj).doubleValue() : 0.0;

            //Create item and add to list
            Item nextItem = new Item(name, quantity, price);
            itemList.add(nextItem);

        }
        return itemList;
    }
    /** This method takes all of the current orders in the system and
     *  writes them into a single JSON file at the given file location.
     * @param orders ArrayList of Order objects currently in memory
     * @param fileLocation location where thee JSON file will be saved
     */

    public static void exportAllOrders(ArrayList<Order> orders, String fileLocation) {

        JSONObject fullObj = new JSONObject(); //main JSON object for entire file
        JSONArray ordersArray = new JSONArray(); //array holding orders to export

        for (Order o : orders) {  //scanning each order & turning to JSON
            JSONObject orderObj = new JSONObject(); //Creating JSON object for each order
            orderObj.put("type", o.getType());
            orderObj.put("order_date", o.getDate().getTime());
            orderObj.put("stage", o.getStage());
            orderObj.put("orderNum", o.getOrderNumber());

            JSONArray itemsArray = new JSONArray(); //array holding items for this order

            for (Item item : o.getItems()) { //go thru each item in this order
                JSONObject itemObj = new JSONObject(); //Creating JSON object for each item
                itemObj.put("name", item.getName());
                itemObj.put("quantity", item.getQuantity());
                itemObj.put("price", item.getPrice());

                itemsArray.add(itemObj); //add this item into the items list
            }
                orderObj.put("items", itemsArray); //link the items list onto the order JSON
                ordersArray.add(orderObj);
            }
        fullObj.put("orders", ordersArray); //attach all orders to the main JSON object

        try (FileWriter writer = new FileWriter(fileLocation)) { // writing the JSON to the file location
            writer.write(fullObj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

