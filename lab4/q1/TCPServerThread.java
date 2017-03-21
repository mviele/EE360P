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
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			String command = din.readLine();
			String[] tokens = command.split(" ");
			if(tokens[0].equals("setmode")){
				if(tokens[1].equalsIgnoreCase("t")){
					Server.setMode(false);
					return;
				}
				else if(tokens[1].equalsIgnoreCase("u")){
					Server.setMode(true);
					return;
				}
			}
			else if(tokens[0].equals("purchase")){
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

			out.write(returnString);
			out.flush();
			client.close();

		} catch (IOException e){
			System.err.println(e);
		} 
	}

}
