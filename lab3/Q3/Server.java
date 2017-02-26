/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
  
  public static void main (String[] args) throws Exception {
    int tcpPort;
    int udpPort;
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");

      System.exit(-1);
    }
    tcpPort = Integer.parseInt(args[0]);
    udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];
		
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

    // TODO: handle request from clients
  }
}

