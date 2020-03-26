import java.util.*;
import java.net.*;
import java.io.*;

/**
 * A command line client for the poll server. Requires a request to show
 * the poll or vote for an option. Exits after printing the response.
 */
public class Client {

    Client(String request, String option) throws IOException {
        try (Socket socket = new Socket("localhost", 7777)) {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(request);   // Send the request
            out.println(option);    // option will be null if request is show

            // Server Response
            while (in.hasNextLine()){
                System.out.println(in.nextLine());
            }
        }
        catch (ConnectException e){
            System.out.println("Cannot find server [port = 7777]");
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 1){
            System.err.println("Please provide a command (show or vote)");
        }
        else if (args[0].equals("show")) {
            if (args.length == 1){
                new Client(args[0], null);          // Ask server to show poll
            } else {
                System.err.println("show command takes no options");
            }
        }
        else if (args[0].equals("vote")){
            if (args.length == 2){
                new Client(args[0], args[1]);       // Ask server to vote 
            } else {
                System.err.println("vote command takes 1 option");
            }
        }
        else {
            System.err.println("Invalid command (show or vote)");
        }
    }

}