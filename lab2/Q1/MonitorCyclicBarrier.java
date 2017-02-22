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
			threadIndex = this.index--;

			if(index >= 0){
				this.wait();
			}
			else{
				this.notifyAll();
			}
		}
		
		if(this.index < this.parties - 1){
			synchronized(this){
				this.index = this.parties - 1;
			}	
		}
	    return threadIndex;
	}
}
