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
    List<String> otherServers = new ArrayList<>();
    List<Integer> otherServersPorts = new ArrayList<>();
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
    Inventory inventory = Inventory.getInstance(inventoryFile);
    
    //open TCP sockets
    try{
      while(true){
        //TCP
        ServerSocket listener = new ServerSocket(otherServersPorts.get(uniqueID - 1));
        Socket tcpSocket;
        if((tcpSocket = listener.accept()) != null){
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
                if(i == uniqueID - 1) continue;

                //1. Make Socket to connect to servers
                Socket otherServer = new Socket(otherServers.get(i), otherServersPorts.get(i));
                PrintWriter servOut = new PrintWriter(otherServer.getOutputStream(), true);
                BufferedReader servIn = new BufferedReader(new InputStreamReader(otherServer.getInputStream()));

                //2. Send Message of the form "server request <myServerID> <timestamp>"
                servOut.write("server request " + Integer.toString(uniqueID) + Long.toString(timestamp) + "\n");
                servOut.flush();
                otherServer.close();
              }

              //2. Wait for n - 1 acknowledgements
              int ack = 0;
              while(ack < numServers - 1 && queue.peek() != timestamp){
                Socket sock;
                if((sock = listener.accept()) != null){
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
                      
                      Socket otherServer = new Socket(otherServers.get(otherID), otherServersPorts.get(otherID));
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
                    }
                  }
                  sock.close();
                }   
              }
              
              //3. Edit inventory
              if(tokens[0].equals("purchase")){
                returnString = inventory.purchase(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                out.write(returnString + "\n");
                out.flush();
                tcpSocket.close();
              }
              else if(tokens[0].equals("cancel")){
                returnString = inventory.cancel(Integer.parseInt(tokens[1]));
                out.write(returnString + "\n");
                out.flush();
                tcpSocket.close();
              } 
              else if(tokens[0].equals("search")){
                returnString = inventory.search(tokens[1]);
                out.write(returnString + "\n");
                out.flush();
                tcpSocket.close();
              } 
              else if(tokens[0].equals("list")){
                returnString = inventory.list();
                out.write(returnString + "\n");
                out.flush();
                tcpSocket.close();
              }


              //4. Send release to all other servers
              for(int i = 0; i < otherServers.size(); i++){
                if(i == uniqueID) continue;

                //1. Make Socket to connect to servers
                Socket otherServer = new Socket(otherServers.get(i), otherServersPorts.get(i));
                PrintWriter servOut = new PrintWriter(otherServer.getOutputStream(), true);
                BufferedReader servIn = new BufferedReader(new InputStreamReader(otherServer.getInputStream()));

                //2. Send Message of the form "server request <myServerID> <timestamp>"
                servOut.write("server release" + Long.toString(timestamp) +"\n");
                servOut.flush();
                otherServer.close();
              }
          }
          else if(tokens[0].equals("server")){
            if(tokens[1].equals("request")){
              //1. Add request to queue
              Long stamp = Long.parseLong(tokens[3]);
              queue.add(stamp);

              //2. Send back acknowledgement
              int otherID = Integer.parseInt(tokens[2]);
              Socket otherServer = new Socket(otherServers.get(otherID), otherServersPorts.get(otherID));
              PrintWriter servOut = new PrintWriter(otherServer.getOutputStream(), true);
              BufferedReader servIn = new BufferedReader(new InputStreamReader(otherServer.getInputStream()));

              servOut.write("server acknowledge\n");
              servOut.flush();
              tcpSocket.close();
            }
            else if(tokens[1].equals("release")){
              //1. Remove given timestamp from queue
              Long stamp = Long.parseLong(tokens[2]);
              Long head = queue.remove();
              tcpSocket.close();
              if(stamp != head){
                throw new Exception("Queue error");
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
}
