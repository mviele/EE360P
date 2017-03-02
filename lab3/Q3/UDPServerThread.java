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
            String[] tokens = command.split(" ");
            String returnString;
            if(tokens[0].equals("purchase")){
                returnString = Server.purchase(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
            }
            else if(tokens[0].equals("cancel")){
                returnString = Server.cancel(Integer.parseInt(tokens[1]));
            }
            else if(tokens[0].equals("search")){
                returnString = Server.search(tokens[1]);
            }
            else if(tokens[0].equals("list")){
                returnString = Server.list();
            }
            else{
                returnString = "Invalid command";
            }
            
            DatagramPacket returnPacket = new DatagramPacket(returnString.getBytes(), returnString.getBytes().length, 
                packet.getAddress(), packet.getPort());
            Server.udpSend(returnPacket);
		} catch (Exception e){
			e.printStackTrace();
        }
	}

}
