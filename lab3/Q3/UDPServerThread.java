/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */

import java.io.IOException; 
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.util.Scanner;

public class UDPServerThread extends Thread {

    DatagramPacket packet;
	
	public UDPServerThread(DatagramPacket packet){
        this.packet = packet;
	}
	public void run() {
		try{
			String command = new String(packet.getData());
			String[] commands = command.split("\n");
            String returnString;
            for(String s: commands){
                String[] tokens = s.split(" ");
                if(tokens[0].equals("purchase")){
                    String returnData = Server.purchase(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                    returnString += returnData + "\n";
                }
                else if(tokens[0].equals("cancel")){
                    String returnData = Server.cancel(Integer.parseInt(tokens[1]));
                    returnString += returnData + "\n";
                }
                else if(tokens[0].equals("search")){
                    String returnData = Server.search(tokens[1]);
                    returnString += returnData + "\n";
                }
                else if(tokens[0].equals("list")){
                    String returnData = Server.list();
                    returnString += returnData + "\n";
                }
                else{
                    returnString += "Invalid command\n";
                }
            }
            
            returnPacket = new DatagramPacket(returnString.getBytes(), returnString.getBytes().length, 
                packet.getAddress(), packet.getPort());
            Server.udpSend(returnPacket);
		} catch (Exception e){
			e.printStackTrace();
        }
	}

}
