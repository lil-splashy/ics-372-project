import java.util.LinkedList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class handles the UI for order handling;
 * @author Jacob Wiggins
 */
public class UserInterface
{
    public UserInterface()
    {
        OrderHandler orderHandler = new OrderHandler();
        Scanner keybored = new Scanner(System.in);
        String menuState = "main"; //Tells us what menu to display, May get turned into an instance var in the future
        String menuOption = "";  //this will hold the choice that the user makes
        System.out.println("Welcome!");

        while(!menuState.equals("quit")) //This will run as long as the state is not quit
        {
            switch(menuState)
            {
                case "main": //This will display the main menu
                    System.out.println("Main menu, type the number that corresponds to an option or type quit to exit");
                    System.out.println("1: Load orders from a .JSON file\n2: Print a list of orders by status\n3: Start order");
                    menuOption = keybored.nextLine();

                    switch(menuOption)
                    {
                        case "1":
                            System.out.println("Load orders from a .JSON file");
                            menuState = "orderIN";
                            break;

                        case "2":
                            System.out.println("Print lists");
                            menuState = "printList";
                            break;

                        case "3":
                            System.out.println("Display an order");
                            break;

                        case "4":
                            System.out.println("Start an order");
                            System.out.println("Which order will be started?\n" + printOrderIDS(orderHandler.getIncomingOrders()));
                            break;

                        case "5":
                            System.out.println("Complete an order");
                            System.out.println("Which order will be completed?\n" + printOrderIDS(orderHandler.getStartedOrders()));
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

                case "orderIN":
                    System.out.println("Please input a JSON file that you want to load from, don't forget to include the file extension!");
                    menuState = "main";
                    break;

                case "printList": //Submenu for selecting a list to print
                    System.out.println("Type the number of the list would you like to print, or type 'back' to return to the main menu");
                    System.out.println("1: Started orders\n2: In-progress orders\n3: Completed orders\nback: Return to the main menu");
                    menuOption = keybored.nextLine();
                    switch(menuOption)
                    {
                        case "1":
                            System.out.println("Started orders");
                            break;

                        case "2":
                            System.out.println("Orders that are in progress");
                            break;

                        case "3":
                            System.out.println("Completed orders");
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
        System.out.println("this is a placeholder aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        UserInterface ui = new UserInterface();
    }
}