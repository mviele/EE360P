
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Client {
  
  public static void main(String[] args) throws Exception{
	  
	  List<InetAddress> addresses = new ArrayList<InetAddress>();
      List<Integer> ports = new ArrayList<Integer>();
      boolean firstInitialization = true;
      int numServers = -1;
	  
   /*************************
   * Read config file begin
   ***************************/
    if (args.length != 1) {
        System.out.println("ERROR: Provide 1 argument");
        System.out.println("\t(1) <configFile>: the configuration file for the server");
        System.exit(-1);
      }
      
      String fileName = args[0];
      Scanner s = new Scanner(new File(fileName));
             
      while (s.hasNextLine()) {
        if(firstInitialization){
          String cmd = s.nextLine();
          numServers = Integer.parseInt(cmd);
          firstInitialization = false;
        }
        else{
          String command = s.nextLine();
          String[] tokens = command.split(":");
          InetAddress address = InetAddress.getByName(tokens[0]);
          addresses.add(address);
          ports.add(Integer.parseInt(tokens[1]));
        }
      }
      s.close();
      
       /*************************
	   * Read config file done
	   ***************************/
	  
      /*************************
	   * Read scanner input begin
	   ***************************/
    PrintWriter out;
    BufferedReader in;
    Scanner sc = new Scanner(System.in);
    boolean failed = false;
    String commandToServer = "dummy command";
    while (true){
      
        while (sc.hasNextLine()) {
            if(!failed){ 
              commandToServer = sc.nextLine();
            }
            failed = false;
            int currentServerNumber = 0;
            boolean connected = false;
            boolean timedOut = false;

            while(!connected){
              System.out.println("currentServerNumber: "+Integer.toString(currentServerNumber));
              InetAddress address = addresses.get(currentServerNumber);
              int port = ports.get(currentServerNumber);
              
              Socket socket = new Socket();
              try{
            	  socket.connect(new InetSocketAddress(address, port), 100);
              }
              catch(Exception e){
            	  timedOut = true;
                failed = true;
                addresses.remove(currentServerNumber);
                ports.remove(currentServerNumber);
                System.out.println("Connection failed initially");
              }
              
              //You connected to a server, so now do your shitttt
              if (!timedOut){
                connected = true;
                String inputFromServer = "";
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Command: "+commandToServer);
                out.write(commandToServer + "\n");
                out.flush();
                socket.setSoTimeout(100);       
                try{
                  String message = "";
                  while((message = in.readLine()) != null){
                    inputFromServer += message + "\n";
                  }
                }
                catch(Exception e){
                  connected = false;
                  failed = true;
                  addresses.remove(currentServerNumber);
                  ports.remove(currentServerNumber);
                  System.out.println("Connection failed receiving response");
                }
                if(connected){
                  System.out.println(inputFromServer);
                }
              }
              timedOut = false;
              // Else, re-enter while loop
            	
              if(addresses.size() == 0){
                System.out.println("All servers dead, rip");
                System.exit(-1);
              }
              currentServerNumber = (currentServerNumber + 1) % addresses.size();
              socket.close();
            }
          
           
        }
       
    }
  }
}
