package hmwk3;

/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
  	
  public static void main (String[] args) throws Exception {
    
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");
      System.exit(-1);
    }
    
    Server myServer = new Server();
    int tcpPort = Integer.parseInt(args[0]);
    int udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];
    //double check packetLength value
    int packetLength = 1024;
		
    Scanner s = new Scanner(new File(fileName));

    Map<String, Integer> inventory = new HashMap<>();
    
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
    for(String str: inventory.keySet()){
    	System.out.println(str+" "+inventory.get(str));
    }
    
    //open UDP and TCP sockets
    try{
    	//TCP
		ServerSocket listener = new ServerSocket(tcpPort);
		Socket tcpSocket;
		
		//UDP
		DatagramSocket datasocket = new DatagramSocket (udpPort);
		byte [] buf = new byte [packetLength];
		
		while(true){
			
			//TCP
			while ((tcpSocket = listener.accept()) != null){
				Thread t = new ServerThread(tcpSocket);
				t.start();
			}
			
			//UDP
		}
    } 
    catch (IOException e){
    	e.printStackTrace();
    	System.err.println("Server dead: " + e);
    } 
  }

}




DatagramPacket datapacket , returnpacket ; 
int port = 2018; int len = 1024;
try {
byte [] buf = new byte [len ] ;
while (true ) {
datapacket = new DatagramPacket( buf , buf . length ); datasocket . receive ( datapacket ); returnpacket = new DatagramPacket( datapacket . getData () ,
datapacket . getLength () , datapacket . getAddress () , datapacket . getPort ( ) ) ;
datasocket . send ( returnpacket );
}
} 



















