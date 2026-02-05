/**
 * This class handles the UI for order handling;
 * @author Jacob Wiggins
 */
public class UserInterface
{
    public UserInterface()
    {
        OrderHandler<Order> orderHandler = new OrderHandler<Order>();
    }

    public String menuChoice(String menuOption)
    {
        String menuState = "run"; //This will return what the state of the menu is back to Main

        switch(menuOption)
        {
            case "0":  //
                menuState = "quit";
                break;

            default: //This will happen if the user gives a response that does not correspond to an option
                System.out.println(menuOption + "is not a valid command, please enter a number that corresponds to an option");
                break;
        }
        return menuState;
    }

    public static void main(String[] args)
    {
        System.out.println("this is a placeholder aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }
}