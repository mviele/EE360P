

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
    
    //open UDP and TCP sockets
    try{
      //TCP
      ServerSocket listener = new ServerSocket(tcpPort);
      Socket tcpSocket;
      
      //UDP
      datasocket = new DatagramSocket(udpPort);
      byte[] buf = new byte[packetLength];
      
      while(true){
        if(!mode){
          //TCP
          if((tcpSocket = listener.accept()) != null){
            Thread t = new TCPServerThread(tcpSocket);
            t.start();
            t.join();
          }
        }
        else{
          //UDP
          DatagramPacket datapacket, returnpacket; 
          try {
            buf = new byte[packetLength];
            datapacket = new DatagramPacket(buf, buf.length);
            datasocket.receive(datapacket);
            Thread t = new UDPServerThread(datapacket);
            t.start();
            t.join();
          }
          catch(Exception e){
            e.printStackTrace();
          }
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

  public synchronized static void udpSend(DatagramPacket packet){
    try{
      datasocket.send(packet);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public synchronized static void setMode(boolean bool){
    mode = bool;
  }

}










































