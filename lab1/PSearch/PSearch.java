import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PSearch implements Callable<Integer>{
	
	public static ExecutorService threadPool = Executors.newCachedThreadPool();
	private int searchValue;
	private int[] searchArray;
	private int numThreads;
	private int start;
	private int end;
	
	public PSearch(int searchValue, int[] searchArray, int numThreads){
		this.searchValue = searchValue;
		this.searchArray = searchArray;
		this.numThreads = numThreads;
		this.start = 0;
		this.end = searchArray.length;
	}
	
	public PSearch(int searchValue, int[] searchArray, int start, int end, int numThreads){
		this.searchValue = searchValue;
		this.searchArray = searchArray;
		this.numThreads = numThreads;
		this.start = start;
		this.end = end;
	}
	

	public Integer call(){
		try{
			if(start >= end) return -1;
			if(start + 1 == end) return searchArray[start] == searchValue ? start : -1;
			if(numThreads == 1){
				for(int i = 0; i < searchArray.length; i++){
					if(searchArray[i] == searchValue) return i;
				}
				return -1;
			}

			int stepSize = (end - start) / numThreads;
			if(stepSize <= 0) stepSize = 1;

			List<Future<Integer>> threadList = new ArrayList<>();
			for(int k = 0; k < numThreads && k < end; k++){
				PSearch p;
				if(k == numThreads - 1){
					p = new PSearch(searchValue, searchArray, start + (stepSize * k), end, numThreads);
				}
				else{
					p = new PSearch(searchValue, searchArray, start + (stepSize * k), 
							start + (stepSize * (k + 1)), numThreads);
				}
				Future<Integer> n = threadPool.submit(p);
				threadList.add(n);
			}

			for(Future<Integer> n: threadList){
				Integer k = n.get();
				if(k != -1){
					return k;
				}
			}
			return -1;
		}
		catch (Exception e) { System.err.println (e); return 1;}
	}
	
	public static int parallelSearch(int x, int[] A, int numThreads) { 
		try{
			PSearch p = new PSearch(x, A, numThreads);
			Future<Integer> f1 = threadPool.submit(p);
			Integer answer = f1.get();
      return answer;
		}
		catch(Exception e){
			e.printStackTrace();
			return -42;
		}
	}
}
