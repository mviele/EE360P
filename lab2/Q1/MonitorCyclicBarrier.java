/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */

public class MonitorCyclicBarrier {

	private int parties;
	private int index;
	
	public MonitorCyclicBarrier(int parties) {
		this.parties = parties;
		this.index = parties - 1;
	}
	
	public int await() throws InterruptedException {
		int threadIndex;
		synchronized(this){
			threadIndex = index--;
		}

		while(index >= 0){
			Thread.sleep(1000);
		}
		
	    return threadIndex;
	}
}
