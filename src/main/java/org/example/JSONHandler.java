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
     */
    public static Order convertToOrder(String fileLocation){

        try (FileReader reader = new FileReader(fileLocation)){
            JSONParser parser = new JSONParser();

            JSONObject fullOrderObj = (JSONObject) parser.parse(reader);
            JSONObject orderObj = (JSONObject) fullOrderObj.get("order");

            //Get order details
            String type = (String) orderObj.get("type");

            Date date = new Date(((Number) orderObj.get("order_date")).longValue());
            Order order = new Order(type, date);

            //Get items, convert and place into order object before returning
            JSONArray items = (JSONArray) orderObj.get("items");
            for(Item i : convertItems(items)){
                order.addItem(i);
            }
            return order;


        } catch(IOException | ParseException e){
            e.printStackTrace();
        }

        return null;
    }

    /** This converts a JSONArray Object to an ArrayList of Item objects
     *
     * @param jsonItems JSONArray
     * @return ArrayList<Item>
     */
    private static ArrayList<Item> convertItems(JSONArray jsonItems){
        ArrayList<Item> itemList = new ArrayList<>();
        for (Object object : jsonItems){
            JSONObject item = (JSONObject) object;
            String name = (String) item.get("name");
            int quantity = ((Number) item.get("quantity")).intValue();
            double price = (double) item.get("price");

            //Fix when Item constructor is made:
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
        } catch (IOException e)
            e.printStackTrace();
        }
    }

}

