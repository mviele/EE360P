/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Server {
  
  private static Map<String, Integer> inventory;
  private static List<Order> orderList;
  private static DatagramSocket datasocket;
  private static int nextOrderID;
  private static boolean mode;

  public static void main (String[] args) throws Exception {
    
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");
      System.exit(-1);
    }
    
    mode = false;
    int tcpPort = Integer.parseInt(args[0]);
    int udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];
    //double check packetLength value
    int packetLength = 1024;
		
    Scanner s = new Scanner(new File(fileName));
    orderList = new ArrayList<>();
    inventory = new HashMap<>();
    nextOrderID = 1;
    
    // parse the inventory file
    while(s.hasNext()){
      int k;
      String cur = s.next();		
      if(s.hasNextInt()){
        k = s.nextInt();
      }
      else{
        throw new Exception("ERROR: Invalid input file");
      }
      inventory.put(cur, k);
    } 
    s.close();
    
    //open TCP sockets
    try{
      while(true){
        //TCP
        ServerSocket listener = new ServerSocket(tcpPort);
        Socket tcpSocket;
        if((tcpSocket = listener.accept()) != null){
          //Identify, is it a client or another server?
         /*
         * 3 Types of messages: 
         * Client Request 
         * Acknowledgement
         * Mutex Request from another server
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
              //2. Wait for n - 1 acknowledgements
              //3. Edit inventory
              //4. Send release to all other servers
              //5. Send return message back to client
          }
          else if(tokens[0].equals("server")){
            if(tokens[1].equals("request")){
              //1. Add request to queue
              //2. Send back acknowledgement
            }
            else if(tokens[1].equals("relase")){
              //1. Remove given timestamp from queue
            }
          }

          Thread t = new TCPServerThread(tcpSocket);
          t.start();
          t.join();
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










































