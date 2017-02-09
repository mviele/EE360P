/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class CyclicBarrier {
	
	private int parties;	

	public CyclicBarrier(int parties) {
		this.parties = parties; //Maybe?
	}
	
	// Waits until all parties have invoked await on this barrier.
	// If the current thread is not the last to arrive then it is
	// disabled for thread scheduling purposes and lies dormant until
	// the last thread arrives.
	// Returns: the arrival index of the current thread, where index
	// (parties - 1) indicates the first to arrive and zero indicates
	// the last to arrive.
	public int await() throws InterruptedException {
           int index = 0;
		
          // you need to write this code
	    return index;
	}
}
