
/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  public static void main(String[] args) throws Exception{
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

    Socket tcp; 
    
    PrintWriter out;
    BufferedReader in;

    Scanner sc = new Scanner(System.in);
    while (sc.hasNextLine()) {
      tcp = new Socket("localhost", tcpPort);
      out = new PrintWriter(tcp.getOutputStream(), true);
      udp = new DatagramSocket();
      in = new BufferedReader(new InputStreamReader(tcp.getInputStream()));
      out.flush();
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");

      if (tokens[0].equals("purchase") || tokens[0].equals("cancel") || 
                 tokens[0].equals("search") || tokens[0].equals("list")) {
          if(mode){
            byte[] cmdArray = cmd.getBytes();
            DatagramPacket dp = new DatagramPacket(cmdArray, cmdArray.length, InetAddress.getLocalHost(), udpPort);
            udp.send(dp);
            dp = new DatagramPacket(new byte[1024], 1024);
            udp.receive(dp);
            System.out.println(new String(dp.getData()));
          }
          else{
            out.write(cmd + "\n");
            out.flush();
            String line, message = new String();
            while((line = in.readLine()) != null){
              message += line + "\n";
            }
            System.out.println(message);
          }
      } else {
        System.out.println("ERROR: No such command");
      }
    }
  }

}
