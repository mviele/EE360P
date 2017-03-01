/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */

import java.io.IOException; 
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread extends Thread {
	Socket Client;
	public ServerThread(Socket s){
		Client = s;
	}
	public void run() {
		Scanner sc1 = null;
		try{
			sc1 = new Scanner(Client.getInputStream());
			PrintWriter print = new PrintWriter(Client.getOutputStream());
			String command = sc1.nextLine();
			Scanner sc2 = new Scanner(command);
			String keyword = sc2.next();
			if(keyword.equals("setmode")){
				
			}
			else if(keyword.equals("purchase")){
				
			}
			else if(keyword.equals("cancel")){
				
			}
			else if(keyword.equals("search")){
				
			}
			else if(keyword.equals("list")){
				
			}
			Client.close();
		} catch (IOException e){
			System.err.println(e);
		} finally {
			sc1.close();
		}
	}

}
