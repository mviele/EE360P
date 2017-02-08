import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PSort {

	static class SortThread implements Runnable {
		private int[] A;
		private int begin;
		private int end;
		public static ExecutorService threadPool = Executors.newCachedThreadPool();

		public SortThread(int[] A, int begin, int end) {
			this.begin = begin;
			this.end = end;
			this.A = A;
		}

		public void run(){

			try{
				if(begin + 1 == end || begin >= end) return;
				int pivotPtr = partition(A, begin, end);
	        	Future t1 = threadPool.submit(new SortThread(A, begin, pivotPtr));
	        	Future t2 = threadPool.submit(new SortThread(A, pivotPtr + 1, end));
	        	t1.get();
	        	t2.get();
	        	return;
			}
			catch (Exception e) { System.err.println (e); return ;}
		}

		private int partition(int[] A, int begin, int end){
			int pivot = A[(begin + end - 1)/2];
			int i = begin, j = end - 1;
			while(true){
				if(A[i] == A[j]) i++;
				while(A[i] < pivot){
					i++;
				}
				while(A[j] > pivot){
					j--;
				}

				if(i >= j) {
					return j;
				}
				
				swapIndices(A, i, j);
			}
			
			
		}
		
		private static void swapIndices(int[] A, int i, int j){
			int temp = A[j];
			A[j] = A[i];
			A[i] = temp;
		}

	}


    public static void parallelSort(int[] A, int begin, int end) {
    	try{
    		SortThread t = new SortThread(A, begin, end);
    		Thread thread = new Thread(t);
    		thread.start();
    		thread.join();
    	}
    	 catch (Exception e) { System.err.println (e); }
    }
}

