package org.example;

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
    public Order convertToOrder(String fileLocation){

        try (FileReader reader = new FileReader(fileLocation)){
            JSONParser parser = new JSONParser();
            //Create array of objects from JSON file
            JSONArray array = (JSONArray) parser.parse(reader);

            //Iterate to get each individual object
            for(Object object : array){
                //Get individual order object
                JSONObject orderObj = (JSONObject) object;

                //Get order details
                String type = (String) orderObj.get("type");
                Date date = (Date) orderObj.get("date");
                Order order = new Order(type, date);

                //Get items, convert and place into order object before returning
                JSONArray items = (JSONArray) orderObj.get("items");
                for(Item i : convertItems(items)){
                    order.addItem(i);
                }
                return order;
            }

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
    private ArrayList<Item> convertItems(JSONArray jsonItems){
        ArrayList<Item> itemList = new ArrayList<Item>();
        for (Object object : jsonItems){
            JSONObject item = (JSONObject) object;
            String name = (String) item.get("name");
            int quantity = (int) item.get("quantity");
            double price = (double) item.get("price");

            //Fix when Item constructor is made:
            Item nextItem = new Item(name, quantity, price);
            itemList.add(nextItem);

        }
        return itemList;
    }

}
