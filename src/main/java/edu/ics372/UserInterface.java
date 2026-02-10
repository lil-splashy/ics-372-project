package edu.ics372;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class handles the UI for order handling.
 *
 * @version 1.0 - Added basic functionality for a text based menu, added functionality allowing it to work with an order handler
 * @author Jacob Wiggins
 */
public class UserInterface
{
    public UserInterface()
    {
        OrderHandler orderHandler = new OrderHandler();
        JsonParser parser = new JsonParser();
        Scanner keybored = new Scanner(System.in);
        String menuState = "main"; //Tells us what menu to display, May get turned into an instance var in the future
        Order orderFocus = null;  //This will hold a reference to an order, usually for display purposes
        String menuOption = "";  //this will hold the choice that the user makes
        System.out.println("Welcome!");

        while(!menuState.equals("quit")) //This will run as long as the state is not quit
        {
            switch(menuState)
            {
                case "main": //This will display the main menu
                    System.out.println("Main menu, type the number that corresponds to an option or type quit to exit");
                    System.out.println("1: Load/Save orders from/to a .JSON file\n2: Print a list of orders by status\n3: Display order" +
                            "\n4: Start order\n5: Complete order\n6: Display uncompleted orders\nquit: Exit program");
                    menuOption = keybored.nextLine();

                    switch(menuOption)
                    {
                        case "1":
                            System.out.println("Load/Save orders from/to a JSON file");
                            menuState = "orderMenu";
                            break;

                        case "2":
                            System.out.println("Print lists");
                            menuState = "printList";
                            break;

                        case "3":
                            System.out.println("Display an order");
                            System.out.println("Please enter the ID of the order you wish to view:");
                            orderFocus = orderHandler.getOrder(keybored.nextLine());

                            if(orderFocus != null) //I figure it makes no sense to have it print null if an invalid ID is entered
                            { System.out.println(orderFocus); }

                            break;

                        case "4":
                            System.out.println("Start an order");
                            System.out.println("Which order will be started?\n" + printOrderIDS(orderHandler.getIncomingOrders()));
                            orderHandler.startOrder(keybored.nextLine());
                            break;

                        case "5":
                            System.out.println("Complete an order");
                            System.out.println("Which order will be completed?\n" + printOrderIDS(orderHandler.getStartedOrders()));
                            orderHandler.completeOrder(keybored.nextLine());
                            break;

                        case "6":
                            System.out.println("Display uncompleted orders");
                            orderHandler.displayUncompletedOrders();
                            break;

                        case "quit","Quit":
                            System.out.println("Quitting program.");
                            menuState = "quit";
                            break;

                        default:
                            System.out.println("Please enter a number that corresponds to an option, or quit to exit.");
                            break;
                    }
                    break;

                case "orderMenu": //Submenu for importing and exporting Orders from JSON files
                    System.out.println("Do you want to load orders from a JSON file or save files to a File");
                    System.out.println("1: Load orders from JSON file\n2: Save orders to JSON file\nback: Return to the main menu");
                    menuOption = keybored.nextLine();
                    switch(menuOption)
                    {
                        case "1":
                            System.out.println("Please input a JSON file that you want to load from, don't forget to include the file extension!");
                            parser.setNewPath(keybored.nextLine());
                            System.out.println("DEBUG path: " + parser.getFilePath());
                            orderHandler.loadOrders();
                            menuState = "main";
                            break;

                        case "2":
                            System.out.println("Enter the ID of the order you want to export; the available orders are:" +
                                    "\nUnstarted: " + printOrderIDS(orderHandler.getIncomingOrders()) +
                                    "\nStarted: " + printOrderIDS(orderHandler.getStartedOrders()) +
                                    "\nCompleted: " + printOrderIDS(orderHandler.getCompletedOrders()));

                            orderFocus = orderHandler.getOrder(keybored.nextLine());

                            if(orderFocus != null) //To avoid trying to export a non-existent order
                            {
                                System.out.println("Name the file you want to save the orders to, don't forget to include .json at the end");
                                parser.exportJSON(orderFocus, keybored.nextLine());
                                menuState = "main";
                            }

                            break;

                        case "back":
                            menuState = "main";
                            break;

                        default:
                            System.out.println("Please choose a number that corresponds to an option, or back to return to the main menu.");
                            break;
                    }
                    break;

                case "printList": //Submenu for selecting a list to print
                    System.out.println("Type the number of the list would you like to print, or type 'back' to return to the main menu");
                    System.out.println("1: Incoming orders\n2: In-progress orders\n3: Completed orders\nback: Return to the main menu");
                    menuOption = keybored.nextLine();
                    switch(menuOption)
                    {
                        case "1":
                            System.out.println("Incoming orders:");
                            orderHandler.displayIncomingOrders();
                            break;

                        case "2":
                            System.out.println("Orders that are in progress:");
                            orderHandler.displayStartedOrders();
                            break;

                        case "3":
                            System.out.println("Completed orders:");
                            orderHandler.displayCompletedOrders();
                            break;

                        case "back","'back'": //This will return the user to the main menu
                            menuState = "main";
                            break;

                        default:
                            System.out.println("Please choose a number that corresponds to the list");
                            break;
                    }
                    break;

                default: //This should never happen normally, but exists as a failsafe
                    System.out.println("If you are seeing this message, please notify Jacob Wiggins");
                    menuState = "main";  //Changes the state from its unexpected value back to main
                    break;
            }

            System.out.println("------------------------------------------------------------------------"); //This should space things out a bit
        }

        keybored.close();

    }

    /**
     * This method will extract order IDs from a LinkedList for the purpose of display. May be subject to change in future
     * @param orders A linked list that contains at least one order
     * @return A String of the array of IDs for each order from the linked list.
     */
    private String printOrderIDS(LinkedList<Order> orders) {
        if (orders.isEmpty()) //If the list has no orders it will return a String saying so
        { return "There are no orders present in this list"; }
        else
        {
            String[] orderIDs = new String[orders.size()];

            for (int i = 0; i < orders.size(); i++) {
                orderIDs[i] = orders.get(i).getOrderID();
            }

            return Arrays.toString(orderIDs);
        }
    }

    public static void main(String[] args)
    {
        UserInterface ui = new UserInterface();
    }
}