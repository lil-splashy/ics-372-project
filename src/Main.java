import java.util.Scanner;
public class Main {
    public static void main(String[] args){
        System.out.println("Hello World");

        Debug debug = new Debug();

        // Maybe have to take in a file?
        Scanner scan = new Scanner(System.in);
        System.out.println("Could you give me the file path that you want? :D");

        String fileName = scan.nextLine();
        System.out.println("okok your file name is: " + fileName);
        // Create Order_Interface Object and call menuChoice() and pass in fileName

        // userInterface userinterface = new userInterface();
        // userinterface.menuChoice();

        //
    }
}