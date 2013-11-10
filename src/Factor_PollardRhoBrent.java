
import java.math.BigInteger;
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
    
    private BigInteger TEN = BigInteger.TEN;
    private BigInteger TWO = new BigInteger("2");
    private BigInteger ONE = BigInteger.ONE;
    private BigInteger ZERO = BigInteger.ZERO;
    
    private BigInteger MAXITERS = new BigInteger("100000000");
    
    private Random random = new Random(13133937);
    
    private BigInteger f(BigInteger y, BigInteger C, BigInteger n)
    {
        return ((y.multiply(y)).add(C)).mod(n);
    }
    
    public BigInteger factorizebrent(Task task, BigInteger n) {
        
//        if(n.mod(TWO).equals(ZERO))
//        {
//            return TWO;
//        }
        
        BigInteger x = null;

        int m = random.nextInt(100)+1; 
//        
        int r= 1;    
        BigInteger z = new BigInteger(n.bitCount(), random);
        BigInteger q=ONE;
        BigInteger C = new BigInteger(n.bitCount(), random);
        BigInteger y = z;
   
        do {        	
        	
            x=y;
            for (int i = 0; i <= r; ++i) 
                y = y.multiply(y).add(C).mod(n); 
            
            int k = 0;
            do {    
            	if(task.isTimeout())
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

//        if(z.equals(ONE)) {
//            return null;
//        }
        
//        if (z.compareTo(n)==0)  {
//        	return null;
////            do {
////                ys = ((y.multiply(y)).add(C)).mod(n); 
////                z = n.gcd(ys.subtract(x));
////            } while (z.compareTo(ONE)==0);
//        }
        
        return z;
    }
    
//    public BigInteger factorizebrent(Task task, BigInteger n) {
//        
//        if(n.mod(TWO).equals(ZERO))
//        {
//            return TWO;
//        }
//        
////        BigInteger k = null;
////        BigInteger i = null;
//        BigInteger x = null;
//        BigInteger ys = null;
//
//        int m = random.nextInt(10)+1; 
////        BigInteger m = BigInteger.valueOf(tmp);
//        int r= 1;    
//        BigInteger z=new BigInteger(n.bitCount(), random);
//        BigInteger q=ONE;
//        
//        
//        BigInteger C = new BigInteger(n.bitCount(), random);
//
//        BigInteger y = z;
//        
//        do {
//            x=y;
//            for (int i = 0; i <= r; ++i) 
//                y = y.multiply(y).add(C).mod(n); 
//            
//            int k = 0;
//            do {
//                if(task.isTimeout())
//                {
//                    return null;
//                }
//                
//                // System.out.print("iter=" + iter.toString() + '\r');
////                ys=y;
//                int rk = r-k;
//                for (int i=1; i <= Math.min(m, rk); ++i) {
//                    y = y.multiply(y).add(C).mod(n); 
//                    q = ((y.subtract(x)).multiply(q)).mod(n);
//                }
//                z = n.gcd(q);
//                k += m;
//            } while (k < r && z.compareTo(ONE) == 0);
//            r = r*2;
//        } while (z.compareTo(ONE)==0);
//
////        if(z.equals(ONE)) {
////            return null;
////        }
//        
//        if (z.compareTo(n)==0)  {
//        	return null;
////            do {
////                ys = ((y.multiply(y)).add(C)).mod(n); 
////                z = n.gcd(ys.subtract(x));
////            } while (z.compareTo(ONE)==0);
//        }
//        
//        return z;
//    }
//    
    private BigInteger min(BigInteger v1, BigInteger v2)
    {
        if(v1.compareTo(v2) > 1)
        {
            return v2;
        }
        return v1;
    }
    
    @Override
    public void factor(Task task) {
    	while(!task.isFinished()) {    		
	        BigInteger toFactor = task.poll();
//	        System.out.println(toFactor);
	        
	        if (toFactor.isProbablePrime(20)) { 
	            task.setPartResult(toFactor);
	            continue;
	        }
	        if(toFactor.equals(BigInteger.ONE)) continue;
                if(task.isTimeout())
                {
                    task.push(toFactor);
                    return;
                }
                
	        BigInteger divisor = null;
	        while(true)
	        {
                    divisor = factorizebrent(task, toFactor);
                    if(task.isTimeout())
                    {
                        task.push(toFactor);
                        return;
                    }
                    if(divisor == null)
                        continue;
                    if(!divisor.equals(toFactor))
                        break;
//                    System.out.println(divisor);
	        }
	        task.push(divisor);	   
//	        factor(task);
	        task.push(toFactor.divide(divisor));	   
//	        factor(task);
    	}
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
