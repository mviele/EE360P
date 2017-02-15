/* EE 360P, HW2, #3
 * Created (February 12, 2017) by Royce Li, Matthew Viele, and Robbie Zuazua
 * Last edited: February 14, 2017 
 * */

/*    Newton: Digs
 *    Benjamin: Seeds
 *    Mary: Fills
*/

/* CONSTRAINTS 
 (a) Benjamin cannot plant a seed unless at least one empty hole exists 
 * and Mary cannot fill a hole unless at least one hole exists in which 
 * Benjamin has planted a seed.
 * 
 (b) Newton has to wait for Benjamin if there are 4 holes dug which have 
 * not been seeded yet. He also has to wait for Mary if there are 8 unfilled 
 * holes. Mary does not care how far Benjamin gets ahead of her.
 * 
 (c) There is only one shovel that can be used to dig and fill holes, and 
 * thus Newton and Mary need to coordinate between themselves for using the 
 * shovel; ie. only one of them can use the shovel at any point of time.*/

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

public class Garden {
	
	ReentrantLock garden; 
	AtomicInteger shovel, unfilled, filled, unseeded, seeded, dug; 
	final Condition waitToDig, waitToSeed, waitToFill;
	
	public Garden() { 
		this.garden = new ReentrantLock();
		this.shovel = new AtomicInteger(); // 1: in use 0: not in use
		this.unfilled = new AtomicInteger();
		this.unseeded = new AtomicInteger();
		this.seeded = new AtomicInteger();
		this.filled = new AtomicInteger();
		this.dug = new AtomicInteger();
		this.waitToDig = garden.newCondition();
		this.waitToSeed = garden.newCondition();
		this.waitToFill = garden.newCondition();
	}; 
	public void startDigging() { // Newton
		garden.lock();
		try{
			while((unseeded.get() >=4) || (unfilled.get() + unseeded.get() >= 8) 
			|| (shovel.get() == 1)){
				waitToDig.await();
			}
			shovel.incrementAndGet();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			garden.unlock();
		}
	}; 
	public void doneDigging() { // Newton
		garden.lock();
		try{
			shovel.decrementAndGet(); 
			dug.incrementAndGet(); // keep count of Newton's holes
			unseeded.incrementAndGet(); // keep count of unseeded holes for Ben
			waitToSeed.signal();
		}
		finally{
			garden.unlock();
		}
		
	}; 
	public void startSeeding() { // Benjamin
		garden.lock();
		try{
			while(unseeded.get() < 1){
				waitToSeed.await();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			garden.unlock();
		}
	};
	public void doneSeeding() { // Benjamin
		garden.lock();
		try{
			seeded.incrementAndGet(); // keep count of Ben's seed count
			unseeded.decrementAndGet(); 
			unfilled.incrementAndGet(); // keep count of unfilled for Mary
			waitToDig.signal();
			waitToFill.signal();
		}
		finally{
			garden.unlock();
		}
	}; 
	public void startFilling() { // Mary
		garden.lock();
		try{
			while(unfilled.get() < 1 || (shovel.get() == 1)){
				waitToFill.await();
			}
			shovel.incrementAndGet();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			garden.unlock();
		}
	}; 
	public void doneFilling() { // Mary
		garden.lock();
		try{
			shovel.decrementAndGet();
			filled.incrementAndGet(); // Keep count of mary's filled
			unfilled.decrementAndGet();
			waitToDig.signal();
		}finally{
			garden.unlock();
		}
	}; 
	 
	    /*
	    * The following methods return the total number of holes dug, seeded or 
	    * filled by Newton, Benjamin or Mary at the time the methods' are 
	    * invoked on the garden class. */
	public int totalHolesDugByNewton() { return dug.get(); }; 
	public int totalHolesSeededByBenjamin() { return seeded.get();  }; 
	public int totalHolesFilledByMary() { return filled.get();  }; 
}
