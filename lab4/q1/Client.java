
/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Client {
	
  private static List<InetAddress> addresses = new ArrayList<>();
  private static List<Integer> ports = new ArrayList<>();
  private static Socket current = null;
  static Socket tcp;
  
  public static void main(String[] args) throws Exception{
    String hostAddress;
    //hostAddress = args[0];
    //tcpPort = Integer.parseInt(args[1]);
    PrintWriter out;
    BufferedReader in;
    int placeholder = -1;
    Scanner sc = new Scanner(System.in);
    
    int numServers = Integer.parseInt(args[0]); //get num servers available
    
    for(int count = 0; count < numServers; count++){
    	  String info = sc.nextLine();
        String[] tokens = info.split(":");
        InetAddress address = InetAddress.getByName(tokens[0]);
        addresses.add(address);
        int port = Integer.parseInt(tokens[1]);
        ports.add(port);
    } //add all current server addresses and ports to the lists
    for(int count = 0; count < ports.size(); count++){
        placeholder = -1;
        InetAddress address = addresses.get(count);
        int port = ports.get(count);
        try{
        	tcp = new Socket(address, port);
        	placeholder = count; //keep track of which server we are using
        }
        catch (IOException e){
        	System.out.println("Current server unavailable");
        }
    }
    if(placeholder == -1){
        System.out.println("No connections");
        System.exit(-1);
    }    
    while (sc.hasNextLine()) {
    	try{
    		  out = new PrintWriter(tcp.getOutputStream(), true);
          in = new BufferedReader(new InputStreamReader(tcp.getInputStream()));
          String cmd = sc.nextLine();
          String[] tokens = cmd.split(" ");
          if (tokens[0].equals("purchase") || tokens[0].equals("cancel") || 
              tokens[0].equals("search") || tokens[0].equals("list")) {
              out.write(cmd + "\n");
              out.flush();
              try {
                  double time1 = System.currentTimeMillis();
                  while (!(in.ready())) {
                    double time2 = System.currentTimeMillis();
                    if (time2 - time1 >= 100) {
                      throw new SocketTimeoutException("Timeout");
                    }
                  }
              } catch (IOException e) {
                  System.out.println("Error");
                  System.exit(-1);
              }
              String line, message = new String();
              while((line = in.readLine()) != null){
                message += line + "\n";
              }
              System.out.println(message);
              
          } else {
            System.out.println("ERROR: No such command");
          }
    	} catch (SocketTimeoutException e) {
    		addresses.remove(placeholder);
    		ports.remove(placeholder);
            // Timeout, connect to new server and try again
    	    	for(int count=0; count<ports.size(); count++){
    	    		placeholder = -1;
    	        	InetAddress address = addresses.get(count);
    	        	int port = ports.get(count);
    	        	try{
    	        		tcp = new Socket(address, port);
    	        		placeholder = count; //keep track of which server we are using
    	        	}
    	        	catch (IOException a){
    	        		System.out.println("Current server unavailable");
    	        	}
    	        }
    	        if(placeholder == -1){
    	        	System.out.println("No connections");
    	        	System.exit(-1);
    	        }    
    	}
            
    }   
  }

}
