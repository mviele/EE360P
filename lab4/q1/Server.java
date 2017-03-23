/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Server {
  
  public static void main (String[] args) throws Exception {
    
    if (args.length != 1) {
      System.out.println("ERROR: Provide 1 argument");
      System.out.println("\t(1) <configFile>: the configuration file for the server");
      System.exit(-1);
    }
    
    String fileName = args[0];
		
    Scanner s = new Scanner(new File(fileName));
    
    PriorityQueue<Long> queue = new PriorityQueue<>();
    boolean firstInitialization = true;
    List<String> otherServers = new ArrayList<String>();
    List<String> otherServersPorts = new ArrayList<String>();
    int uniqueID = -1;
    int numServers = -1;
    String fileName = null;
      
    while (s.hasNextLine()) {
      if(firstInitialization){
        String cmd = s.nextLine();
        String[] tokens = cmd.split(" ");
        uniqueID = Integer.parseInt(tokens[0]);
        numServers = Integer.parseInt(tokens[1]);
        fileName = tokens[2];
        firstInitialization = false;
      }
      //Need to make sure we have been initialized
      else{
        String command = sc.nextLine();
        String[] tokens = command.split(":");
        otherServers.add(tokens[0]);
        otherServersPorts.add(tokens[1]);
      }
    }
    
    sc.close();
      
    
    //change filename to Java File & pass to inventory class
    File inventoryFile = new File(fileName);
    Inventory inventory = Inventory.getInstance(inventoryFile);
    
    //open TCP sockets
    try{
      while(true){
        //TCP
        ServerSocket listener = new ServerSocket(tcpPort);
        Socket tcpSocket;
        if((tcpSocket = listener.accept()) != null){
          //Identify, is it a client or another server?
         /*
         * 4 Types of messages: 
         * Client Request 
         * Acknowledgement
         * Mutex Request from another server
         * Mutex Release
         */

          String returnString = ""; 
          InputStreamReader input = new InputStreamReader(tcpSocket.getInputStream());
          BufferedReader din = new BufferedReader(input); 
          PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
          String command = din.readLine();
          String[] tokens = command.split(" ");
          if(tokens[0].equals("purchase") || tokens[0].equals("cancel") || 
             tokens[0].equals("search") || tokens[0].equals("list")){

              //1. Generate Timestamp and send to all other servers
              long timestamp = System.currentTimeMillis();
              for(int i = 0; i < otherServers.size(); i++){
                if(i == uniqueID) continue;

                //1. Make Socket to connect to servers
                Socket otherServer = new Socket(otherServers.get(i), otherServersPorts.get(i));
                PrintWriter servOut = new PrintWriter(otherServer.getOutputStream(), true);
                BufferedReader servIn = new BufferedReader(new InputStreamReader(otherServer.getInputStream()));

                //2. Send Message of the form "server request <myServerID> <timestamp>"
                servOut.write("server request " + Integer.toString(uniqueID) + Long.toString(timestamp) + "\n");
                servOut.flush();
              }

              //2. Wait for n - 1 acknowledgements
              int ack = 0;
              while(ack < numServers - 1){

              }
              
              //3. Edit inventory
              //4. Send release to all other servers
              //5. Send return message back to client
          }
          else if(tokens[0].equals("server")){
            if(tokens[1].equals("request")){
              //1. Add request to queue
              Long stamp = Long.parseLong(tokens[3]);
              queue.add(stamp);

              //2. Send back acknowledgement
              int otherID = Integer.parseInt(tokens[2]);
              //TODO: Setup connection with sender and send acknowledgement
            }
            else if(tokens[1].equals("relase")){
              //1. Remove given timestamp from queue
              Long stamp = Long.parseLong(tokens[2]);
              Long head = queue.remove();
              if(stamp != head){
                throw new Exception("Queue error");
              }
            }
          }

          // Thread t = new TCPServerThread(tcpSocket);
          // t.start();
          // t.join();
        }
      }
    } 
    catch (IOException e){
    	e.printStackTrace();
    	System.err.println("Server dead: " + e);
    } 
  }

  public synchronized static String purchase(String username, String productName, int quantity){
    if(inventory.containsKey(productName)){
      if(inventory.get(productName) >= quantity){
        //Subtract inventory
        inventory.put(productName, inventory.get(productName) - quantity);

        //Add order to orderList
        Order order = new Order(nextOrderID++, username, productName, quantity);
        orderList.add(order);

        //Successful purchase message
        return "You order has been placed, " + order.toString();
      }

      //Insufficient quantity message
      return "Not Available - Not enough items";
    }
    else{
      //No such product message
      return "Not Available - We do not sell this product";
    }
    
  }

  public synchronized static String cancel(int orderID){
    for(Order order: orderList){
      if(order.getOrderID() == orderID){
        //Add inventory back
        String product = order.getProductName();
        inventory.put(product, order.getQuantity() + inventory.get(product));

        //Remove order from order list
        orderList.remove(order);

        //Successful cancellation message
        return "Order " + Integer.toString(orderID) + " is canceled"; 
      }
    }

    //No such order message
    return Integer.toString(orderID) + " not found, no such order";
  }

  public synchronized static String search(String username){
    String searchResult = new String();
    for(Order order: orderList){
      if(order.getUsername().equals(username)){
        searchResult += Integer.toString(order.getOrderID()) + ", " + 
          order.getProductName() + ", " + Integer.toString(order.getQuantity()) + "\n";
      }
    }

    return searchResult.length() != 0 ? searchResult : "No order found for " + username; 
  }

  public synchronized static String list(){
    String listString = new String();
    for(String s: inventory.keySet()){
      listString += s + " " + Integer.toString(inventory.get(s)) + "\n";
    }

    return listString;
  }

}
