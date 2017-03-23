

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
  
  public static void main (String[] args) throws Exception {
    
	boolean firstInitialization = true;
	List<String> otherServers = new ArrayList<String>();
	List<String> otherServersPorts = new ArrayList<String>();
	int uniqueID = -1;
	int numServers = -1;
	String fileName = null;
    
  //Use Scanner to read in inputs
  Scanner sc = new Scanner(System.in);
  while (sc.hasNextLine()) {
	  if(firstInitialization){
		  String cmd = sc.nextLine();
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
//PASS FILE HERE
  
  

try{
	
	InetAddress addr = InetAddress.getByName(otherServers.get(uniqueID));
	ServerSocket listener = new ServerSocket(Integer.parseInt(otherServersPorts.get(uniqueID)),0,addr);

	while(true){
		Socket tcpSocket = new Socket();
		if((tcpSocket = listener.accept()) != null){
			//logic here
			
		}
		//remember to close tcpSocket at some point
	}
	
}
catch (Exception e){
	e.printStackTrace();
	System.err.println("Server dead: " + e);
} 

  }

}











































