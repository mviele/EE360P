import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Process extends RandomElector{

    public Process(File file){

        super(file);

    }

    @Override
    public void send(String message){

        try{
            Socket leftProcess = new Socket();
            leftProcess.connect(new InetSocketAddress(this.getLeftAddress(), this.getLeftPort()), 100);
            PrintWriter out = new PrintWriter(leftProcess.getOutputStream(), true);

            out.write(message);
            out.flush();
            leftProcess.close();
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

    }

    public static void main(String[] args){

        try{
            File file = new File(args[0]);
            Process p = new Process(file);

            ServerSocket listener = new ServerSocket(p.getMyPort());
            if(args.length > 1 && args[1].equals("init")){
                p.initiateElection();
            }

            Scanner s = new Scanner(System.in);
            while(true){

                Socket tcpSocket;
                if((tcpSocket = listener.accept()) != null){

                    InputStreamReader input = new InputStreamReader(tcpSocket.getInputStream());
                    BufferedReader din = new BufferedReader(input);
                    String message = din.readLine();
                    System.out.println("Received message: " + message);
                    String[] tokens = message.split(" ");
                    if(tokens[0].equals("election")){
                        p.handleElectionMessage(message);
                    }
                    else if(tokens[0].equals("leader")){
                        p.handleLeaderMessage(message);
                    }
                }

                if(p.getLeaderID() >= 0){
                    System.out.println("Leader ID: "+Integer.toString(p.getLeaderID()));
                    break;
                }
            }
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

    }

}