/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class MonitorCyclicBarrier extends CyclicBarrier {
	
	public MonitorCyclicBarrier(int parties) {
	}
	
	@Override
	public int await() throws InterruptedException {
           int index = 0;
		
          // you need to write this code
	    return index;
	}
}
