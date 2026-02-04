import java.util.Collections;
import java.util.Scanner;
public class Main {
    public static void main(String[] args){
        System.out.println("Hello World");

        Debug debug = new Debug();



        // Maybe have to take in a file?
        Scanner scan = new Scanner(System.in);
        System.out.println("Could you give me the file path that you want? :D");

        String filePath = scan.nextLine();
        System.out.println("okok your file path is: " + filePath);

        scan.close();

        // new json object = filename

        //proxy method calls


        // Create User_Interface Object and call menuChoice() and pass in fileName
        UserInterface userInterface = new UserInterface();

        // have parameters as strings so far,
        // but if Jacob wants switch statements then maybe 1...n integers instead.
        userInterface.menuChoice("loadFilePath", filePath); // Give user interface file path

        userInterface.menuChoice("loadOrder"); // Load order

        userInterface.menuChoice("displayOrder"); // Display Order

        userInterface.menuChoice("startOrder"); // Start Order

        userInterface.menuChoice("CompleteOrder"); // Complete Order

        userInterface.menuChoice("displayUncomplete"); // Display Uncompleted Orders

        userInterface.menuChoice("displayComplete"); //Display Completed Orders

        userInterface.menuChoice("exportJSON"); // Export Json

 //       JsonParser parser = new JsonParser();
//        OrderHandler handler = new OrderHandler();

        //Order order = parser.parseFile(filePath);
        //
//        if (order != null) {
//            List<Order> orders = new ArrayList<>();
//            orders.add(order);
//            handler.loadOrders(orders);
//
//            handler.displayUncompletedOrders();
//
//            handler.startOrder(order.getOrderID());
//            handler.displayUncompletedOrders();
//
//            handler.completeOrder(order.getOrderID());
//            handler.displayCompletedOrders();
        //}
//
//        handler.doesOrderThings(parser.madeOrder());

        //menuChoice(displayOrder) or displayStatus

        //menuChoice(displayUncomplete)

        //menuChoice(displayComplete)

        //OrderHandler orderHandler = new OrderHandler(); // OrderHandler object
        //accepts order from json parser
        // accept json file use jsonmethods to create the order

        // menuChoice(exportJSON)
    }
}