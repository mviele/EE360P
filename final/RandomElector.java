import java.io.File;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;

public abstract class RandomElector {

    private int processID; //processID must be positive or zero
    private int electionID;
    private boolean awake;
    private int leaderID; //The processID of the leader

    private String myAddress;
    private int myPort;

    private String leftAddress;
    private int leftPort;

    private String leaderAddress;
    private int leaderPort;

    private static int MAX_ELECTION_ID = 100;

    public RandomElector(File file){

        this.awake = false;
        this.leaderID = -1;

        Random random = new Random();
        this.electionID = random.nextInt(this.MAX_ELECTION_ID); //between 0 and 2^31 - 1
        System.out.println("My Election ID: " + Integer.toString(this.electionID));

        try{
            Scanner s = new Scanner(file);

            String cmd = s.nextLine();
            this.processID = Integer.parseInt(cmd);

            cmd = s.nextLine();
            String[] tokens = cmd.split(":");
            this.myAddress = tokens[0];
            this.myPort = Integer.parseInt(tokens[1]);

            cmd = s.nextLine();
            tokens = cmd.split(":");
            this.leftAddress = tokens[0];
            this.leftPort = Integer.parseInt(tokens[1]);

            s.close();
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

    }

    public void initiateElection(){

        if(this.awake) return;

        String message = "election " + Integer.toString(this.electionID) + " " + Integer.toString(this.processID);
        this.send(message);
        this.awake = true;
    }

    public void handleElectionMessage(String message) throws Exception{

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
        else if(otherElectionID > this.electionID && !awake){
            String myMessage = "election " + Integer.toString(this.electionID) + " " + Integer.toString(this.processID);
            this.send(myMessage);
        }

        this.awake = true;
    }

    public void handleLeaderMessage(String message) throws Exception{
        
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

    public int getProcessID() {
		return processID;
	}

	public int getElectionID() {
		return electionID;
	}

	public boolean isAwake() {
		return awake;
	}

	public int getLeaderID() {
		return leaderID;
	}

	public String getMyAddress() {
		return myAddress;
	}

	public int getMyPort() {
		return myPort;
	}

	public String getLeftAddress() {
		return leftAddress;
	}

	public int getLeftPort() {
		return leftPort;
	}

	public String getLeaderAddress() {
		return leaderAddress;
	}

	public int getLeaderPort() {
		return leaderPort;
	}

    public abstract void send(String message);

    /**
     * For the future:
     *   (1) Handle process crashes during election
     *   (2) Handle process addition during or after election
     */

}
