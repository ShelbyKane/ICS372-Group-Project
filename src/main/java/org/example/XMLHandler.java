package org.example;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
//import org.json.simple.JSONArray;

import javax.xml.parsers.ParserConfigurationException;      // For XML Parser Errors (misconfiguration)
import org.xml.sax.SAXException;                            // For XML Data Errors
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.time.ZoneId;

/*
directorywatcher / threading
Feature 2) Export for every change
Feature 5) import automatically in the background
 */
/*  Questions:
    - If saving orders and what stage they're in, then files might have a stage tag (but they might also not).  Do we default to "incoming" if no orderStage tag?
        Guessing yes. Added functionality for this.

    - Should there be error checks on no orders in XML file, no items in an order, etc?
    - Should there be functionality for dollars vs euros etc? Currency conversion?
        - Should it require unit specification? If not, assume dollars?
        Asking because: Paper Towel Roll specifies dollar units

    - Should item ALWAYS have price and quantity? (assume yes?)

    - Should it include possibility of date ordered tag; incoming vs fulfilled tag?
        - !!! I think we need to add functionality for this. So that the auto-update and import continue to work for non-new orders.
        - Also functionality for order ID. It's specified in XML file.
 */
/*
    To do:
    JSON/XML Handlers
        - Create interface? Abstract class? / facade?
            - Takes file, if .json > JSON Handler; if .xml > XML Handler

    Import: Largely done. Might need minor changes depending on design.
        - Need Order to be able to create objects with additional details (eg orderStage, orderNumber)
                - orderStage: Default to incoming; take in different stage if already set
                - orderNumber: Default to incrementing based on Order class's orderIndex; take in different number if already set
        - Need Order to be able to edit objects
        ~ Add Functionality to loop through anything in working directory\src\data?  This can also be handled outside of the handlers (make a loop that runs __ handler each time)

    Export:
        - Create export functionality
        - Create functionality that allows auto-changes >> file. Needs to be flexible to any change. Could:
                - do override functions
                - make a function that always changes everything (this seems prone to errors?)
                - make a function that takes everything in; updates where old object data doesn't match new data ?? ?
 */

public class XMLHandler {
    static String fileLoc = ".\\src\\data\\ExampleOrder1.xml";       // File location - !!! Update to a folder to loop through? Make a parameter? (likely latter)
    //String fileLoc = System.getProperty("user.dir") + "\\src\\data\\ExampleOrder1.xml";

    // Convert XML File to Orders
    public static void convertToXMLOrder(/*String fileLocation*/) throws ParserConfigurationException, SAXException, IOException {
        //System.out.println("Testing convertToXMLOrder \n -----------");                                               //testing
        int orderNumber;                                    // ID num for orders. <Order id = "__">
        String orderStage;                                  // Stage order is in. Incoming, Fulfilling, Completed, Canceled. <OrderStage>__</OrderStage>
        String orderType;                                   // Pick-Up, Ship, Delivery. <OrderType>__</OrderType>
        LocalDate orderDate;//= new Date();                 // Date order was placed. // !!! Use localdate?
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);                 // IF using LocalDate; formatter determines how to parse (and display?) dates.

        // Create factory object > Create builder object > Use builder to parse file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileLoc);

        // Get first element node in doc (The root node. e.g. <Orders> node) > Get list of all child nodes
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();


        //  Loop through each Element node; Get Order data
        //  ----------------------------------------------
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            //System.out.println("Loop " + i);                                                                          // testing

            // If node is an Element node (not attribute / text node); get element data
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) n;
                //System.out.println(" === ");   System.out.println("Node " + i + " is an element node.");              // testing

                // Get Order Data
                // ----------------------
                // !!! Should there be a default if orderType is not given?
                orderType = element.getElementsByTagName("OrderType").item(0).getTextContent();                   // NOTE: orderType is a node nested in an attribute. Get node by tag name > Get first node (only 1) > Get String Value

                // Optional Nodes
                // Get length of value/NodeList
                int orderNumSize = element.getAttribute("id").length();
                NodeList orderStageList = element.getElementsByTagName("OrderStage");
                NodeList orderDateList =  element.getElementsByTagName("OrderDate");

                // If length of value/NodeList > 0, then Get Found Value. Else, Set Default Value
                orderNumber = ((orderNumSize > 0)) ? Integer.parseInt(element.getAttribute("id")) : -1;           // NOTE: orderNumber is a value within an attribute tag. Get value from there.
                orderStage = ((orderStageList.getLength() > 0)) ?  orderStageList.item(0).getTextContent() : "Incoming";

                if (orderDateList.getLength() > 0) {
                    String newDate = orderDateList.item(0).getTextContent();
                    orderDate = LocalDate.parse(newDate, formatter);
                } else {
                    orderDate = LocalDate.now();
                }

                // Create New Order Object
                // !!!! use date instead of LocalDate?
                Date orderDate2 = Date.from(orderDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Order order = new Order(orderType, orderDate2);

                /*System.out.println("orderNumber: " + orderNumber);                                                    // testing
                System.out.println("orderType: " + orderType);
                System.out.println("orderStage: " + orderStage);
                System.out.println("orderDate2: " + orderDate2);*/


                // Get List of Items > Add to Order Obj
                //  --------------------------------------------
                NodeList xmlItems = element.getElementsByTagName("Item");

                for(Item itm : convertXMLItems(xmlItems)){
                    order.addItem(itm);
                }
                //System.out.println(order);                                                                            // testing
            }
           // System.out.println("------------------------");                                                           // testing
        }
    }

    // Convert XML File to Items
    private static ArrayList<Item> convertXMLItems(NodeList xmlItems){
        //System.out.println("\n=== \n Testing convertXMLItems \n ------------");                                       //testing
        ArrayList<Item> itemList = new ArrayList<>();
        String itemName;                // <Item type="__">
        int itemQuantity;               // <Quantity>__</Quantity>
        double itemPrice;               // <Price>__</Price>

        //  Loop through each Item; get Item Data
        //  ---------------------------------------
        for (int i = 0; i < xmlItems.getLength(); i++) {
            Node item = xmlItems.item(i);
            //System.out.println("\n+++ Item List +++");  System.out.println("Item " + i);                              // testing

            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element elmItem = (Element) item;

                itemName = elmItem.getAttribute("type");                                                          // Get itemName value from within attribute tag. <Item type="Rubber Ducky">
                itemPrice = Double.parseDouble(elmItem.getElementsByTagName("Price").item(0).getTextContent());   // Get itemPrice from node within Price node. Only 1 Price node per Item == .item(0)
                itemQuantity = Integer.parseInt(elmItem.getElementsByTagName("Quantity").item(0).getTextContent());

                Item nextItem = new Item(itemName, itemQuantity, itemPrice);
                itemList.add(nextItem);

                /*System.out.println("itemName: " + itemName);                                                          // testing
                System.out.println("itemPrice: " + itemPrice);
                System.out.println("itemQuantity: " + itemQuantity);*/
            }
        }
        return itemList;
    }
}
