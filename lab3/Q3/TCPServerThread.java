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
import java.net.Socket;
import java.util.StringTokenizer;

public class TCPServerThread extends Thread {

	Socket client;
	
	public TCPServerThread(Socket client){
		this.client = client;
	}
	
	public void run() {
		try{
			String returnString = ""; 
			InputStreamReader input = new InputStreamReader(client.getInputStream());
			BufferedReader din = new BufferedReader(input); 
			PrintWriter print = new PrintWriter(client.getOutputStream());
			String command = din.readLine();
			StringTokenizer st = new StringTokenizer(command); 
			String[] tokens = command.split(" ");
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
			print.append(returnString);
			Socket returnSocket = new Socket(client.getInetAddress(), client.getPort());
			Server.tcpSend(returnSocket); 
			client.close();
		} catch (IOException e){
			System.err.println(e);
		} 
	}

}
