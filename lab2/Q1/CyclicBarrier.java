/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores
import java.util.ArrayList;
import java.util.List;

public class CyclicBarrier {
	
	private int parties;
	private int index;	
	private List<Semaphore> semaphoreList;

	public CyclicBarrier(int parties) {
		this.parties = parties; //Maybe?
		this.index = parties - 1;
		this.semaphoreList = new ArrayList<>();
		for(int i = 0; i < this.parties; i++){
			Semaphore s = new Semaphore(1); //Binary Semaphore
			try{
				s.acquire();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			this.semaphoreList.add(s);
		}
	}
	
	// Waits until all parties have invoked await on this barrier.
	// If the current thread is not the last to arrive then it is
	// disabled for thread scheduling purposes and lies dormant until
	// the last thread arrives.
	// Returns: the arrival index of the current thread, where index
	// (parties - 1) indicates the first to arrive and zero indicates
	// the last to arrive.
	public int await() throws InterruptedException {
		int threadIndex = this.index--;
		this.acquire(threadIndex);
		return threadIndex;
	}

	private void acquire(int threadIndex) throws InterruptedException {
		if(this.index < 0){
			for(Semaphore s: this.semaphoreList){
				s.release();
			}
			this.index = this.parties - 1;
		}
		this.semaphoreList.get(threadIndex).acquire();
	}
}
