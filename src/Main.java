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



        // new json object = filename

        //proxy method calls


        // Create User_Interface Object and call menuChoice() and pass in fileName
        UserInterface userInterface = new UserInterface();
        String choice;
        // create switch statement
        do {
            System.out.println("\nMenu:");
            System.out.println("1. Load Orders");
            System.out.println("2. Display Orders");
            System.out.println("3. Start Order");
            System.out.println("4. Complete Order");
            System.out.println("5. Display Uncompleted Orders");
            System.out.println("6. Display Completed Orders");
            System.out.println("7. Export JSON");
            System.out.println("0. Exit");

            System.out.print("Choose an option of what you would like me to do :D ");
            choice = scan.nextLine();

            switch (choice) {
                case "1":
                    userInterface.menuChoice("1");
                    break;
                case "2":
                    userInterface.menuChoice("2");
                    break;
                case "3":
                    userInterface.menuChoice("3");
                    break;
                case "4":
                    userInterface.menuChoice("4");
                    break;
                case "5":
                    userInterface.menuChoice("5");
                    break;
                case "6":
                    userInterface.menuChoice("6");
                    break;
                case "7":
                    userInterface.menuChoice("7");
                    break;
                case "0":
                    System.out.println("Okok see you later :D");
                    break;
                default:
                    System.out.println("Oman Invalid choice D:");
            }


        } while (!choice.equals("0"));

        scan.close();

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