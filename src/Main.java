
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author fred
 */
public class Main {
   
    public static boolean DEBUG = false;
    public static long totalTimeout = 10900;
    public static Stopwatch globalTimer = new Stopwatch();
    
    public static boolean Do_Division_First = true; //true;
    public static boolean Sort = true;
    public static boolean Do_Reverse_Sort = true;
    
    public static boolean PerfectSquareCheck = true;
    public static boolean TrialDivisionCheck = true;
    
    public static int sieveLimit = 500;
    public static int BackupTimeThreshold = 20;
    
    
    public static boolean isTimeout() {
    	return globalTimer.milliseconds() >= totalTimeout;
    }
    
    public static void main(String args[]) throws IOException {
        
        Task.REVERSE_SORT = Do_Reverse_Sort;
        
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        List<Task> tasks = new ArrayList<>();
        String line = read.readLine();
        
        globalTimer.start();
        Timing timer =  new Timing(totalTimeout);
        int i = 0;
        while(true) 
        {
            if(line == null || line.trim().equals("")) {
                break;
            }
            BigInteger b = new BigInteger(line.trim());
            Task t = new Task(i++, b, timer);
            tasks.add(t);
            line = read.readLine();
        }
        
        int tasks_finished = 0;
        if(Do_Division_First)
        	tasks_finished = doWorkDivFirst(tasks);
        
        Factor_PerfectGeometry geo = new Factor_PerfectGeometry();
        Factor_PollardRhoBrent pollard = new Factor_PollardRhoBrent();
        while(tasks_finished < tasks.size() && !isTimeout()) {   
        	
        	dPrintln("# No solved: " + tasks_finished + " R: " + pollard.getRValue());
            if(Sort) {
            	Collections.sort(tasks);
            }
        	
        	for(Task task : tasks) {
        		if(task.isFinished())
        			continue;
        		
	        	if(PerfectSquareCheck) {
	        		geo.factor(task);
	        	}
	        	
	        	if(task.isFinished()) {
	        		tasks_finished++;
        			continue;
	        	}
	        	
	        	removePrimes(task);
	        	
	        	pollard.factor(task);
	        	
	        	removePrimes(task);
	        	
	        	if(task.isFinished())
	        		tasks_finished++;
        	}
        	
        	
        	
        	pollard.doubleRValue();
        }
        
        printResults(tasks);
        
        if(DEBUG) 
        {
            int finished = 0;
            for (Task result : tasks) {
                if (result.isFinished()) { //  && !result.isTimeout() ) {
                    finished++;
                }
                else {
                    dPrintln("FAILED: ");
                    dPrintln("  " + result);
                }
            }
            dPrintln("Finished " + finished + "/"+ tasks.size());
            dPrintln("Executed for " + globalTimer.stop().milliseconds() + "ms");
        }
        
        
    }
    
    public static void removePrimes(Task task) {
    	Queue<BigInteger> todo = new LinkedList<>();
    	
    	while(!task.isFinished()) {    		
	        BigInteger toFactor = task.poll();
	        
	        if (toFactor.isProbablePrime(20)) { 
	            task.setPartResult(toFactor);
	            continue;
	        }
	        
	        if(toFactor.equals(BigInteger.ONE)) continue;
	        
	        todo.add(toFactor);
    	}
    	
    	for(BigInteger t : todo)
    		task.push(t);
    	
    	
    }
    
    public static int doWorkDivFirst(List<Task> tasks)
    {
        FactorMethod fStart = new Factor_TrialDivision(sieveLimit);
        
        int tasks_finished = 0;
        for(Task task:tasks)
        {
            fStart.factor(task);
            if(task.isFinished())
            	tasks_finished++;
        }       

        return tasks_finished;
    }
    
    public static void printResults(List<Task> tasks)
    {
        for(Task task : tasks)
        {
            if(task.isFinished()) { // !task.isTimeout()
                for(BigInteger b: task.getResults())
                {
                    System.out.println(b);
                }
            }
            else {
                System.out.println("fail");
            }
            System.out.println(""); 
        }
    }

    public static void dPrint(String s)
    {
        if(DEBUG)
        {
            if(s.equals(""))
                System.out.print(s);
            else
                System.out.print("DEBUG: " + s);
        }
    }
    
    public static void dPrintln(String s)
    {
        if(DEBUG)
        {
            if(s.equals(""))
                System.out.println(s);
            else
                System.out.println("DEBUG: " + s);
        }
    }
}
