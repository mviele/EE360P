

public class FairReadWriteLock {
	
	private int numReaders;
	private int numWriters;
	private int numThreads;
	private int line;
	
	//Constructor
	public FairReadWriteLock(){
		this.numReaders = 0;
		this.numWriters = 0;
		this.numThreads = 0;
		this.line = 0;
	}
	
	public synchronized void beginRead() {
		
		//assign a ProcessId and then increment count
		int myNumInLine = this.numThreads;
		this.numThreads++;
		
		//Wait until there are no writers and its your turn in line
		while(this.numWriters>0 || this.line<myNumInLine){
			try{
				wait();
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		
		//Update globals and notify
		this.line++;
		this.numReaders++;
		notifyAll();
	}
	
	public synchronized void endRead() {
		//One reader left, so notify everyone
		this.numReaders --;
		notifyAll();
		
	}
	
	public synchronized void beginWrite() {
		
		//assign a ProcessId and then increment count
		int myNumInLine = this.numThreads;
		this.numThreads++;
		
		//Wait until there are no writers or readers and its your turn in line
		while(this.numReaders>0 || this.numWriters>0 || this.line<myNumInLine){
			try{
				wait();
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		
		//Update globals and notify
		this.line++;
		this.numWriters++;
		notifyAll();
	}
	
	
	public synchronized void endWrite() {
		//One writer left, so notify everyone
		this.numWriters--;
		notifyAll();
	}
}






//40 points) Implement a Java class FairReadWriteLock that synchronizes reader and writer threads using monitors with wait, notify and notifyAll methods. The class should provide the following methods: void beginRead(), void endRead(), void beginWrite(), and void endWrite(). A reader thread only invokes beginRead() and endRead() while a writer thread only invokes beginWrite() and endWrite(). In addition, the lock (instance of this class) should provide the following properties:
//1

//(a) There is no read-write or write-write conﬂict.
//(b) A writer thread that invokes beginWrite() will be blocked until all preceding reader and writer threads have acquired and released the lock.
//(c) A reader thread that invokes beginRead() will be blocked until all preceding writer threads have acquired and released the lock.
//(d) A reader thread cannot be blocked if any preceding writer thread has acquired and released the lock.
//The precedence of threads is determined by the timestamp (sequence number) that threads obtain on arrival.
