package edu.ics372;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class XmlParser implements ParserInterface {

    private String filePath = "src/main/orders/order.xml";

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public void setNewPath(String newPath) {
        this.filePath = newPath;
    }

    //https://www.youtube.com/watch?v=w3WibDOie1Y
    @Override
    public Order parseFile(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            //gets the root element of the XML file, which should be <Orders>
            Element root = doc.getDocumentElement();

            // gets the first <Order> element found insdie the root <Orders> element
            Element orderEl = (Element) root.getElementsByTagName("Order").item(0);

            // reads the order id attribute from <Order id="485">
            String orderID = orderEl.getAttribute("id");

            // read the text inside the <OrderType> tag
            String orderType = orderEl.getElementsByTagName("OrderType").item(0).getTextContent();

            // gets all <Item> elements that belong in this order
            NodeList itemNodes = orderEl.getElementsByTagName("Item");

            // creates a new order with the second constructor.
            // used java systems to display a date since the sample file didnt include one
            // used "New" for order status like in the JSON parser
            Order newOrder = new Order(orderID, System.currentTimeMillis(), "NEW", orderType,itemNodes.getLength(), null);

            // Loop through each <Item> element inside the order
            for (int i = 0; i < itemNodes.getLength(); i++) {

                //gets the current <Item> element from the nodeList
                Element itemEl = (Element) itemNodes.item(i);

                // reads the item name from the type attribute in <Item type="Rubber duck">
                String name = itemEl.getAttribute("type");

                // rads the price value from the <Price> tag and converts it from text to a double
                double price = Double.parseDouble(itemEl.getElementsByTagName("Price").item(0).getTextContent());

                //rads the quantity value from the <Quantity> tag and converts it from text to an int
                int quantity = Integer.parseInt(itemEl.getElementsByTagName("Quantity").item(0).getTextContent());

                newOrder.addItem(new Item("I" + i, name, price, quantity,null));
            }

            return newOrder;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public void exportOrders(List<Order> orders, String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("orders");
            doc.appendChild(root);

            for (Order order : orders) {
                Element orderEl = doc.createElement("order");

                appendText(doc, orderEl, "orderID", order.getOrderID());
                appendText(doc, orderEl, "order_date", String.valueOf(order.getOrderDate()));
                appendText(doc, orderEl, "status", order.getOrderStatus());
                appendText(doc, orderEl, "type", order.getOrderType());

                Element itemsEl = doc.createElement("items");
                Item[] items = order.getItems();
                if (items != null) {
                    for (Item item : items) {
                        if (item == null) continue;
                        Element itemEl = doc.createElement("item");
                        appendText(doc, itemEl, "itemID", item.getItemID());
                        appendText(doc, itemEl, "name", item.getItemName());
                        appendText(doc, itemEl, "price", String.valueOf(item.getItemPrice()));
                        appendText(doc, itemEl, "quantity", String.valueOf(item.getItemQuantity()));
                        itemsEl.appendChild(itemEl);
                    }
                }
                appendText(doc, orderEl, "item_count", String.valueOf(items != null ? items.length : 0));
                orderEl.appendChild(itemsEl);
                root.appendChild(orderEl);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));

            System.out.println("Exported XML to: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendText(Document doc, Element parent, String tag, String value) {
        Element el = doc.createElement(tag);
        el.appendChild(doc.createTextNode(value));
        parent.appendChild(el);
    }
}