/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class Server {
  
  private static Map<String, Integer> inventoryMap;
  private static List<Order> orderList;
  private static int nextOrderID;
  private static boolean init = false;

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
    List<String> otherServers = new ArrayList<>();
    List<Integer> otherServersPorts = new ArrayList<>();
    Set<Integer> ignoreSet = new HashSet<>();
    int uniqueID = -1;
    int numServers = -1;
      
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
        String command = s.nextLine();
        String[] tokens = command.split(":");
        otherServers.add(tokens[0]);
        otherServersPorts.add(Integer.parseInt(tokens[1]));
      }
    }
    
    s.close();
    
    //change filename to Java File & pass to inventory class
    File inventoryFile = new File(fileName);
    Server.initialize(inventoryFile);
    
    //open TCP sockets
    try{
      ServerSocket listener = new ServerSocket(otherServersPorts.get(uniqueID - 1));
      while(true){
        //TCP
        System.out.println(otherServersPorts.get(uniqueID - 1));
        Socket tcpSocket;
        if((tcpSocket = listener.accept()) != null){
         /*
          * 4 Types of messages: 
          * Client Request 
          * Acknowledgement
          * Mutex Request from another server
          * Mutex Release
          */
          System.out.println(queue.toString());
          System.out.println(tcpSocket.toString());
          String returnString = ""; 
          InputStreamReader input = new InputStreamReader(tcpSocket.getInputStream());
          BufferedReader din = new BufferedReader(input); 
          PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);
          String command = din.readLine();
          String[] tokens = command.split(" ");
          System.out.println("Received Command: "+command);
          if(tokens[0].equals("purchase") || tokens[0].equals("cancel") || 
             tokens[0].equals("search") || tokens[0].equals("list")){

              //1. Generate Timestamp and send to all other servers
              long timestamp = System.currentTimeMillis();
              queue.add(timestamp);
              System.out.println("Timestamp: "+Long.toString(timestamp));
              for(int i = 0; i < otherServers.size(); i++){
                if(i == uniqueID - 1) continue;
                if(ignoreSet.contains(i)) continue;

                //1. Make Socket to connect to servers
                Socket otherServer = new Socket();
                try{
                  otherServer.connect(new InetSocketAddress(otherServers.get(i), otherServersPorts.get(i)), 100);
                }
                catch(Exception e){
                  ignoreSet.add(i);
                  otherServer.close();
                  numServers--;
                  continue;
                }
                PrintWriter servOut = new PrintWriter(otherServer.getOutputStream(), true);
                BufferedReader servIn = new BufferedReader(new InputStreamReader(otherServer.getInputStream()));

                //2. Send Message of the form "server request <myServerID> <timestamp>"
                servOut.write("server request " + Integer.toString(uniqueID) + " " + Long.toString(timestamp) + "\n");
                servOut.flush();
                otherServer.close();
              }

              System.out.println("Alerted other servers");

              //2. Wait for n - 1 acknowledgements
              int ack = 0;
              while(ack < numServers - 1 && queue.peek() != timestamp){
                Socket sock;
                if((sock = listener.accept()) != null){
                  System.out.println("Connected to other server, while waiting for acknowledge");
                  InputStreamReader inStream = new InputStreamReader(sock.getInputStream());
                  BufferedReader inReader = new BufferedReader(input); 
                  PrintWriter outWriter = new PrintWriter(sock.getOutputStream(), true);
                  String com = din.readLine();
                  String[] comTokens = command.split(" ");

                  if(comTokens[0].equals("server")){
                    if(comTokens[1].equals("acknowledge")){
                      ack++;
                    }
                    else if(comTokens[1].equals("request")){
                      //1. Add request to queue
                      Long stamp = Long.parseLong(tokens[3]);
                      queue.add(stamp);

                      //2. Send back acknowledgement
                      int otherID = Integer.parseInt(tokens[2]);
                      
                      Socket otherServer = new Socket(otherServers.get(otherID - 1), otherServersPorts.get(otherID - 1));
                      PrintWriter servOut = new PrintWriter(otherServer.getOutputStream(), true);
                      BufferedReader servIn = new BufferedReader(new InputStreamReader(otherServer.getInputStream()));

                      servOut.write("server acknowledge\n");
                      servOut.flush();
                    }
                    else if(comTokens[1].equals("release")){
                      //1. Remove given timestamp from queue
                      Long stamp = Long.parseLong(comTokens[2]);
                      Long head = queue.remove();
                      if(stamp != head){
                        throw new Exception("Queue error");
                      }
                      if(comTokens.length > 3){
                        if(comTokens.length == 4){
                          Server.cancel(Integer.parseInt(comTokens[3]));
                        }
                        else{
                          Server.purchase(comTokens[3], comTokens[4], Integer.parseInt(comTokens[5]));
                        }
                      }
                    }
                  }
                  sock.close();
                }   
              }

              System.out.println("Received acknowledgements");
              
              //3. Edit inventory
              if(tokens[0].equals("purchase")){
                returnString = Server.purchase(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                out.write(returnString + "\n");
                out.flush();
                tcpSocket.close();
              }
              else if(tokens[0].equals("cancel")){
                returnString = Server.cancel(Integer.parseInt(tokens[1]));
                out.write(returnString + "\n");
                out.flush();
                tcpSocket.close();
              } 
              else if(tokens[0].equals("search")){
                returnString = Server.search(tokens[1]);
                out.write(returnString + "\n");
                out.flush();
                tcpSocket.close();
              } 
              else if(tokens[0].equals("list")){
                returnString = Server.list();
                out.write(returnString + "\n");
                out.flush();
                tcpSocket.close();
              }

              System.out.println("Inventory access complete");

              //4. Send release to all other servers
              for(int i = 0; i < otherServers.size(); i++){
                if(i == uniqueID - 1) continue;
                if(ignoreSet.contains(i)) continue;

                //1. Make Socket to connect to servers
                Socket otherServer = new Socket();
                try{
                  otherServer.connect(new InetSocketAddress(otherServers.get(i), otherServersPorts.get(i)), 100);
                }
                catch(Exception e){
                  ignoreSet.add(i);
                  otherServer.close();
                  numServers--;
                  continue;
                }
                PrintWriter servOut = new PrintWriter(otherServer.getOutputStream(), true);
                BufferedReader servIn = new BufferedReader(new InputStreamReader(otherServer.getInputStream()));

                //2. Send Message of the form "server request <myServerID> <timestamp>"
                String releaseMsg = "server release " + Long.toString(timestamp);
                if(tokens[0].equals("purchase") || tokens[0].equals("cancel")){
                  for(int k = 1; k < tokens.length; k++){
                    releaseMsg += " " + tokens[k];
                  }
                }
                
                servOut.write(releaseMsg +"\n");
                servOut.flush();
                otherServer.close();
              }

              Long head = queue.remove();
              System.out.println("Head: "+Long.toString(head));
              if(timestamp != head){
                System.out.println("Queue error");
                System.exit(-1);
              }

              System.out.println("Mutex released");
          }
          else if(tokens[0].equals("server")){
            System.out.println("Inter-server communication detected");
            if(tokens[1].equals("request")){
              //1. Add request to queue
              Long stamp = Long.parseLong(tokens[3]);
              queue.add(stamp);

              //2. Send back acknowledgement
              int otherID = Integer.parseInt(tokens[2]);
              Socket otherServer = new Socket(otherServers.get(otherID - 1), otherServersPorts.get(otherID - 1));
              PrintWriter servOut = new PrintWriter(otherServer.getOutputStream(), true);
              BufferedReader servIn = new BufferedReader(new InputStreamReader(otherServer.getInputStream()));

              servOut.write("server acknowledge\n");
              servOut.flush();
              tcpSocket.close();
            }
            else if(tokens[1].equals("release")){
              //1. Remove given timestamp from queue
              long stamp = Long.parseLong(tokens[2]);
              System.out.println("Remove request: "+Long.toString(stamp));
              long head = queue.remove();
              System.out.println("Head: "+Long.toString(head));
              tcpSocket.close();
              if(stamp != head){
                throw new Exception("Queue error");
              }
              if(tokens.length > 3){
                if(tokens.length == 4){
                  Server.cancel(Integer.parseInt(tokens[3]));
                }
                else{
                  Server.purchase(tokens[3], tokens[4], Integer.parseInt(tokens[5]));
                }
              }
            }
          }
          else{
            tcpSocket.close();
          }
        }
      }
    } 
    catch (IOException e){
    	e.printStackTrace();
    	System.err.println("Server dead: " + e);
    } 
  }

  public static void initialize(File file){
        if(init) return;
        inventoryMap = new HashMap<>();
        try{
            Scanner s = new Scanner(file);
            orderList = new ArrayList<>();
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
                inventoryMap.put(cur, k);
            } 
            s.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        init = true;
    }

  public static String purchase(String username, String productName, int quantity){
      if(inventoryMap.containsKey(productName)){
          if(inventoryMap.get(productName) >= quantity){
              //Subtract inventory
              inventoryMap.put(productName, inventoryMap.get(productName) - quantity);

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

  public static String cancel(int orderID){
    for(Order order: orderList){
        if(order.getOrderID() == orderID){
            //Add inventory back
            String product = order.getProductName();
            inventoryMap.put(product, order.getQuantity() + inventoryMap.get(product));

            //Remove order from order list
            orderList.remove(order);

            //Successful cancellation message
            return "Order " + Integer.toString(orderID) + " is canceled"; 
        }
    }

    //No such order message
    return Integer.toString(orderID) + " not found, no such order";
  }

  public static String search(String username){
    String searchResult = new String();
    for(Order order: orderList){
      if(order.getUsername().equals(username)){
        searchResult += Integer.toString(order.getOrderID()) + ", " + 
          order.getProductName() + ", " + Integer.toString(order.getQuantity()) + "\n";
      }
    }

    return searchResult.length() != 0 ? searchResult : "No order found for " + username; 
  }

  public static String list(){
    String listString = new String();
    for(String s: inventoryMap.keySet()){
      listString += s + " " + Integer.toString(inventoryMap.get(s)) + "\n";
    }
    
    return listString;
  }
}

