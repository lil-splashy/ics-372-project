package edu.ics372;
package edu.parser;
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

    private String filePath = "order.xml";

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public void setNewPath(String newPath) {
        this.filePath = newPath;
    }

    @Override
    public Order parseFile(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            Element orderEl = doc.getDocumentElement();

            String orderType = orderEl.getElementsByTagName("type").item(0).getTextContent();
            long orderDate = Long.parseLong(orderEl.getElementsByTagName("order_date").item(0).getTextContent());

            NodeList itemNodes = orderEl.getElementsByTagName("item");
            Order newOrder = new Order(orderDate, "NEW", orderType, itemNodes.getLength());

            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element itemEl = (Element) itemNodes.item(i);
                String name = itemEl.getElementsByTagName("name").item(0).getTextContent();
                double price = Double.parseDouble(itemEl.getElementsByTagName("price").item(0).getTextContent());
                int quantity = Integer.parseInt(itemEl.getElementsByTagName("quantity").item(0).getTextContent());

                newOrder.addItem(new Item("I" + i, name, price, quantity));
            }

            return newOrder;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    @Override
    public void exportJSON(List<Order> orders, String filePath) {
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