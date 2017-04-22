import java.io.File;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;

public abstract class RandomElector {

    private int processID;
    private int electionID;
    private boolean awake;
    private int leaderID; //The processID of the leader

    private String myAddress;
    private int myPort;

    private String leftAddress;
    private int leftPort;

    private String leaderAddress;
    private int leaderPort;

    public RandomElector(File file){

        awake = false;

        Random random = new Random();
        electionID = random.nextInt(Integer.MAX_VALUE); //between 0 and 2^31 - 1

        Scanner s = new Scanner(file);

        String cmd = s.nextLine();
        processID = Integer.parseInt(cmd);

        cmd = s.nextLine();
        String[] tokens = cmd.split(":");
        myAddress = tokens[0];
        myPort = Integer.parseInt(tokens[1]);

        cmd = s.nextLine();
        tokens = cmd.split(":");
        leftAddress = tokens[0];
        leftPort = Integer.parseInt(tokens[1]);

        s.close();

    }

    public void initiateElection(){
        String message = "election " + Integer.toString(this.electionID) + " " + Integer.toString(this.processID);
        this.send(message);
        this.awake = true;
    }

    public void handleElectionMessage(String message){

        String[] tokens = message.split(" ");
        if(!tokens[0].equals("election")){
            throw new Exception("Incorrect message type passed to handleElectionMessage");
        }
        int otherElectionID = Integer.parseInt(tokens[1]);
        int otherProcessID = Integer.parseInt(tokens[2]);

        if(otherElectionID < this.electionID){
            this.send(message);
        }
        else if(otherElectionID == this.electionID && otherProcessID < this.processID){
            this.send(message);
        }
        else if(otherElectionID == this.electionID && otherProcessID > this.processID && !awake){
            String myMessage = "election " + Integer.toString(this.electionID) + " " + Integer.toString(this.processID);
            this.send(myMessage);
        }
        else if(otherElectionID == this.electionID && otherProcessID == this.processID){
            String myMessage = "leader " + Integer.toString(this.processID) + " " + this.myAddress + ":" + Integer.toString(this.myPort);
            this.send(myMessage);
        }
        else if(otherElectionID < this.electionID && !awake){
            String myMessage = "election " + Integer.toString(this.electionID) + " " + Integer.toString(this.processID);
            this.send(myMessage);
        }

        this.awake = true;
    }

    public void handleLeaderMessage(String message){
        
        String[] tokens = message.split(" ");
        if(!tokens[0].equals("leader")){
            throw new Exception("Incorrect message type passed to handleLeaderMessage");
        }

        this.leaderID = Integer.parseInt(tokens[1]);
        String[] addrTokens = tokens[2].split(":");

        this.leaderAddress = addrTokens[0];
        this.leaderPort = Integer.parseInt(addrTokens[1]);

        if(this.leaderID != this.processID){
            this.send(message);
        }

    }

    public void send(String message);

    /**
     * For the future:
     *   (1) Handle process crashes during election
     *   (2) Handle process addition during or after election
     */

}
