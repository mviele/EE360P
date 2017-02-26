
/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.net.DatagramSocket;
import java.net.Socket;

import java.util.Scanner;

public class Client {

  public static void main(String[] args) {
    String hostAddress;
    int tcpPort;
    int udpPort;
    
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <hostAddress>: the address of the server");
      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(3) <udpPort>: the port number for UDP connection");
      System.exit(-1);
    }

    hostAddress = args[0];
    tcpPort = Integer.parseInt(args[1]);
    udpPort = Integer.parseInt(args[2]);

    boolean mode = false; //false = tcp, true = udp

    try{
      Socket tcp = new Socket("localhost", tcpPort);
      DatagramSocket udp = new DatagramSocket(udpPort);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    

    Scanner sc = new Scanner(System.in);
    while (sc.hasNextLine()) {
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");

      if (tokens[0].equals("setmode")) {
        if (tokens[1].equalsIgnoreCase("t")) {
          mode = false;
        } else if (tokens[1].equalsIgnoreCase("u")) {
          mode = true;
        } else {
          System.out.println("Invalid command, server mode unchanged.");
        }
      } else if (tokens[0].equals("purchase")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
        if(mode){
          try{
            udpPurchase(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
          }
          catch(Exception e){
            System.out.println("Invalid command format");
          }
        }
        else{
          try{

          }
          catch(Exception e){
            System.out.println("Invalid command format");
          }
        }
      } else if (tokens[0].equals("cancel")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else if (tokens[0].equals("search")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else if (tokens[0].equals("list")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else {
        System.out.println("ERROR: No such command");
      }
    }
  }

  private static void tcpPurchase(String username, String item, int value){

  }

  private static void udpPurchase(String username, String item, int value){

  }

  private static void tcpCancel(int orderID){

  }

  private static void udpCancel(int orderID){

  }

  private static void tcpSearch(String username){

  }

  private static void udpSearch(String username){

  }

  private static void tcpList(){

  }

  private static void udpList(){

  }
}
