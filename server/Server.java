import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 *  Static multithreaded poll server.
 * 
 *  Clients can connect on port 7777 to view the poll or vote for an option.
 *  Currently only to be used on localhost.
 */
public class Server {
    private static Poll poll;			// The poll object
    private static Writer log;			// The log output stream
    private static ServerSocket listener;	// The listening socket

    /**	---CLI---
     *  Starts the server on port 7777 and listens for connections, creating new threads for each one. 
     *
     *	@param args the options for the poll
     */
    public static void main(String[] args) {
        if (args.length < 2){
            System.err.println("Too few vote options");
            System.exit(1);
        }

        try {
            poll = new Poll(args);
            log = new FileWriter("log.txt");

            listener = new ServerSocket(7777);
            System.out.println("The poll server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(20);

            // Hook is executed upon program execution (ctrl-C) to close the server socket, executer and log file.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println("\n\nClosing Server...");
                    try {
                        log.close();
                        listener.close();
                        pool.shutdown();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            while (!listener.isClosed()) {
                pool.execute(new PollHandler(listener.accept()));
            }
        }
        catch (Exception e) {
            // Ignore exception due to program execution (listener tries to accept after being closed)
            if (!listener.isClosed()) {
                e.printStackTrace();
            }
        }
    }

    /**	The handler Thread.
     *
     *	Given a client socket it will evaluate the request and provide the appropriate response.
     *  Logs the request with a timestamp and client ip.
     */
    private static class PollHandler implements Runnable {
        private Socket socket;
        private String timestamp;

        PollHandler(Socket socket) {
	    timestamp = new SimpleDateFormat("yyyy/MM/dd : HH/mm/ss : ").format(new Date()); 
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                // Establish socket streams for communication
                Scanner in = new Scanner(socket.getInputStream());  
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Get the request provided by the client
                String request = in.nextLine();

                // Write request to log
                synchronized(log) {
                    log.write(timestamp + socket.getInetAddress().getHostAddress() + " : " + request + "\n");
                } 

                if (request.equals("show")){                    // Send poll details
                    out.println(poll);
                }
                else {
                    try {
                        out.println(poll.vote(in.nextLine()));  // Get the option to vote for and send new total
                    }
                    catch (IllegalArgumentException e) {
                        out.println(e.getMessage());            // report invalid vote option
                    }
                }
            }
            catch (Exception e) {
                System.out.println("[Error] " + socket + e.getMessage());
            }
            finally {                                         // Close the connection
                try {
                    socket.close();
                }
                catch (IOException e) {
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}
