package org.example;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import org.json.simple.JSONArray;

import javax.xml.parsers.ParserConfigurationException;      // For XML Parser Errors (misconfiguration)
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;                            // For XML Data Errors
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.time.ZoneId;

public class XMLHandler {
    // Convert XML File to Order
    // Assumes each method call takes in 1 order / each file only has 1 order
    public static Order convertXMLToOrder(String fileLocation) throws ParserConfigurationException, SAXException, IOException {
        //System.out.println("Testing convertToXMLOrder \n -----------");                                               //testing
        int orderNumber;                                    // ID num for orders. <Order id = "__">
        String orderStage;                                  // Stage order is in. Incoming, Fulfilling, Completed, Canceled. <OrderStage>__</OrderStage>
        String orderType;                                   // Pick-Up, Ship, Delivery. <OrderType>__</OrderType>
        LocalDate orderDate;//= new Date();                 // Date order was placed.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);                 // IF using LocalDate; formatter determines how to parse (and display?) dates.

        // Create factory object > Create builder object > Use builder to parse file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileLocation);

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
                // Get length of value/NodeList
                int orderNumSize = element.getAttribute("id").length();
                NodeList orderStageList = element.getElementsByTagName("OrderStage");
                NodeList orderTypeList = element.getElementsByTagName("OrderType");
                NodeList orderDateList =  element.getElementsByTagName("OrderDate");

                // If length of value/NodeList > 0, then Get Found Value. Else, Set Default Value
                orderNumber = ((orderNumSize > 0)) ? Integer.parseInt(element.getAttribute("id")) : -1;           // NOTE: orderNumber is a value within an attribute tag. Get value from there.
                orderStage = ((orderStageList.getLength() > 0)) ?  orderStageList.item(0).getTextContent() : "Incoming";
                orderType = ((orderTypeList.getLength() > 0)) ? orderTypeList.item(0).getTextContent() : null;    // NOTE: orderType is a node nested in an attribute. Get node by tag name > Get first node (only 1) > Get String Value

                if (orderDateList.getLength() > 0) {
                    String newDate = orderDateList.item(0).getTextContent();
                    orderDate = LocalDate.parse(newDate, formatter);
                } else {
                    orderDate = LocalDate.now();
                }

                // Error Handling
                // -------------------
                // If given orderNumber already exists in OrderList, it might be a duplicate or create other data integrity errors. Report error and return a null object.
                if (OrderList.exists(String.valueOf(orderNumber))){
                    System.out.println("Alert: The given Order Number: " + orderNumber + ", already exists. Double check that there are no duplicate files, or update the Order Number in the import file.");
                    return null;
                }

                // If orderType is not given, report error and return a null object
                if (orderType == null) {
                    System.out.println("Alert: No orderType given by XML import file. Please add orderType to import file for Order: " + orderNumber);
                    return null;
                }


                // Create New Order Object
                Date orderDate2 = Date.from(orderDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Order order = new Order(orderNumber, orderType, orderStage, orderDate2);

                /*System.out.println("orderNumber: " + orderNumber);                                                    // testing
                System.out.println("orderType: " + orderType);
                System.out.println("orderStage: " + orderStage);
                System.out.println("orderDate2: " + orderDate2);*/


                // Get List of Items > Add to Order Obj
                //  --------------------------------------------
                NodeList xmlItems = element.getElementsByTagName("Item");

                for(Item itm : convertXMLToItems(xmlItems)){
                    order.addItem(itm);
                }
                //orderList.add(order);
                //System.out.println(order);                                                                            // testing

                return order;
            }
           // System.out.println("------------------------");                                                           // testing
        }
        return null;
    }

    // Convert XML File to Items
    private static ArrayList<Item> convertXMLToItems(NodeList xmlItems){
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


    // Assumes we are exporting 1 order per file
    public static void exportXMLOrder(Order o, String fileLocation) throws TransformerException, ParserConfigurationException {
        //System.out.println("Testing exportALlXMLOrders");
        //String exportFileLoc = ".\\src\\data\\exampleExport.xml";
        // Date Format
        String pattern = "MM/dd/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);


        // Create factory object > Create builder object > Use builder to write file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Create Root Element <Orders>
        Element root = doc.createElement("Orders");
        doc.appendChild(root);

        // Order Data
        //------------
        // Create Order Element w/ orderNumber
        Element order = doc.createElement("Order");
        order.setAttribute("id", String.valueOf(o.getOrderNumber()));

        // OrderStage (eg Incoming, Completed)
        Element orderStage = doc.createElement("OrderStage");
        orderStage.appendChild(doc.createTextNode(o.getStage()));
        order.appendChild(orderStage);

        // OrderType (eg Delivery, Pick-up)
        Element orderType = doc.createElement("OrderType");
        orderType.appendChild(doc.createTextNode(o.getType()));
        order.appendChild(orderType);

        // OrderDate (formatted as "MM/dd/yyyy")
        Element orderDate = doc.createElement("OrderDate");
        orderDate.appendChild(doc.createTextNode(df.format(o.getDate())));
        order.appendChild(orderDate);

        // Item Data
        //------------
        ArrayList<Item> itemList = o.getItems();
        for(Item i : itemList){
            // Create Item Element w/ Type (itemName)
            Element item = doc.createElement("Item");
            item.setAttribute("type", i.getName());

            // Item Price
            Element price = doc.createElement("Price");
            price.appendChild(doc.createTextNode(String.valueOf(i.getPrice())));
            item.appendChild(price);
            // !!! price unit dollars? do we need to check / handle currency?

            // Item Quantity
            Element quantity = doc.createElement("Quantity");
            quantity.appendChild(doc.createTextNode(String.valueOf(i.getQuantity())));
            item.appendChild(quantity);

            // Add <Item> to its parent node/tag <Order>
            order.appendChild(item);
        }

        // Append <Order> node to root node <Orders>
        root.appendChild(order);

        // Write to XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        // Specify File Location
        StreamResult output = new StreamResult(fileLocation);
        transformer.transform(source, output);
    }
}
