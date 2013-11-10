
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author figgefred
 */
public class Factor_PollardRhoBrent implements FactorMethod {
    
    private BigInteger ONE = BigInteger.ONE;
    private BigInteger C;
    private int R_Value = 64;
    
    
    public int getRValue() {
    	return R_Value;
    }
    
    public Factor_PollardRhoBrent() {
    	C = new BigInteger(32, random);
    }
    
    
    public void doubleRValue() {
    	C = new BigInteger(32, random);    	
    	R_Value *= 2;    	
    }
    
    
    private Random random = new Random(13133937);
    
    
    public BigInteger factorizebrent(Task task, BigInteger n) {        
        BigInteger x = null;

        int m = random.nextInt(100)+1; 
//        
        int r= 1;    
        BigInteger z = new BigInteger(n.bitCount(), random);
        BigInteger q=ONE;
        
        BigInteger y = z;
        
        
   
        do {        
        	
            x=y;
            for (int i = 0; i <= r; ++i) 
                y = y.multiply(y).add(C).mod(n); 
            
            int k = 0;
            do {    
            	if(r > R_Value || task.isTimeout())
                {
                    return null;
                }
                
                int rk =  Math.min(m, r-k);
                for (int i=1; i <= rk; ++i) {
                    y = y.multiply(y).add(C).mod(n); 
                    q = y.subtract(x).multiply(q).mod(n);
                }
                
                z = n.gcd(q);
                k += m;
            } while (k < r && z.compareTo(ONE) == 0);
            r = r*2;
            
            
        } while (z.compareTo(ONE)==0);

        return z;
    }
    
    @Override
    public void factor(Task task) {
    	Queue<BigInteger> newTasks = new LinkedList<>();
    	while(!task.isFinished()) {    		
	        BigInteger toFactor = task.poll();	     
                
	        BigInteger divisor = factorizebrent(task, toFactor);
	        
	        if(divisor == null || divisor.equals(toFactor))
	        	newTasks.add(toFactor);
	        else {	        
		        newTasks.add(divisor);
		        newTasks.add(toFactor.divide(divisor));
	        }
    	}
    	
    	for(BigInteger todo : newTasks)
    		task.push(todo);
    }
    
    public static void main(String[] args)
    {
        BigInteger b = new  BigInteger("83209473483892");
        Task t = new Task(0, b, new Timing(200000000));
        FactorMethod f = new Factor_PollardRhoBrent();
        f.factor(t);
        System.out.println(t);
        
    }
    
}
