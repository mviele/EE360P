
/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.PrintWriter;
import java.net.DatagramPacket;
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

    PrintWriter out = new PrintWriter(tcp.getOutputStream());
    BufferedReader in = new BufferedReader(
        new InputStreamReader(tcp.getInputStream()));

    Scanner sc = new Scanner(System.in);
    while (sc.hasNextLine()) {
      out.flush();
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
      } else if (tokens[0].equals("cancel") || tokens[0].equals("search") || tokens[0].equals("list")) {
          if(mode){
            byte[] cmdArray = cmd.getBytes();
            DatagramPacket dp = new DatagramPacket(cmdArray, cmdArray.length, "localhost", udpPort);
            udp.sendPacket(dp);
          }
          else{
            out.write(cmd);
          }
      } else {
        System.out.println("ERROR: No such command");
      }
    }
  }

}
