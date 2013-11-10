import java.math.BigInteger;


public class Factor_SQUFOF implements FactorMethod {
	
	public Factor_PerfectGeometry geo = new Factor_PerfectGeometry();
	public static final int MAX_ITERATIONS = 15;
	
	public boolean isPerfectSquare(BigInteger N) {
		return BigMath.isqrt(N).pow(2).equals(N);
	}

	public BigInteger SQUFOF(Task task, BigInteger N, int k) {
//		System.out.println("# input");
//		System.out.println("N " + N);
//		System.out.println("k "  + k);
//		System.out.println();
		
		
		BigInteger kn = N.multiply(BigInteger.valueOf(k));		
		BigInteger sqrkn = BigMath.isqrt(kn);
		BigInteger P0 = sqrkn;
		BigInteger Q0 = BigInteger.ONE;
		BigInteger Q1 = kn.subtract(P0.pow(2));
		BigInteger P1 = null;
//		System.out.println("# init");
//		System.out.println("P0 " + P0);
//		System.out.println("Q0 " + Q0);
//		System.out.println("Q1 " + Q1);
//		System.out.println();
		
		int i = 0;
		do {
			if(task.isTimeout()) {
				return null;
			}
			if(Q1.equals(BigInteger.ZERO))
				return BigInteger.ONE;
			
			BigInteger bi = sqrkn.add(P0).divide(Q1);
			P1 = bi.multiply(Q1).subtract(P0);
			BigInteger tmp = Q1;
			Q1 = Q0.add(bi.multiply(P0.subtract(P1)));
			P0 = P1;
			
			Q0 = tmp;
//			System.out.println("P"+(++i)+" " + P0);
//			System.out.println("Q"+(i)+" "  + Q0);
//			System.out.println("Q"+(i+1)+ " " + Q1);
//			System.out.println();
			
		} while(Q1.compareTo(BigInteger.ONE) > 0 && isPerfectSquare(Q1) && i++ < MAX_ITERATIONS);
		
		if(i >= MAX_ITERATIONS)
			return BigInteger.ONE;
		
//		System.out.println("# Perfect square found:");
//		System.out.println(Q1);
////		System.out.println(geo.getPowRoot(Q1, 2));
//		System.out.println();
//		
		
		BigInteger qsqrt = BigMath.isqrt(Q1);		
		BigInteger b0 = sqrkn.subtract(P0).divide(qsqrt);
		P0 = b0.multiply(qsqrt).add(P0);
		Q0 = qsqrt;
		Q1 = kn.subtract(P0.pow(2)).divide(Q0);
//		System.out.println("# init second loop");
//		System.out.println("P0 " + P0);
//		System.out.println("Q0 " + Q0);
//		System.out.println("Q1 " + Q1);
//		System.out.println();
		i = 0;
		
		while(i++ < MAX_ITERATIONS) {
			if(task.isTimeout()) {
				return null;
			}
			if(Q1.equals(BigInteger.ZERO))
				return BigInteger.ONE;
			
			BigInteger bi = sqrkn.add(P0).divide(Q1);
			P1 = bi.multiply(Q1).subtract(P0);
			BigInteger tmp = Q1;
			Q1 = Q0.add(bi.multiply(P0.subtract(P1)));
		
			if(P0.equals(P1))
				break;
			P0 = P1;
			Q0 = tmp;
//			System.out.println("P"+(++i)+" " + P0);
//			System.out.println("Q"+(i)+" "  + Q0);
//			System.out.println("Q"+(i+1)+ " " + Q1);
//			System.out.println();
		} 
		
		if(i >= MAX_ITERATIONS)
			return BigInteger.ONE;
		
		return N.gcd(P1);
	}
	
	@Override
	public void factor(Task task) {
		while(!task.isFinished()) {
			BigInteger N = task.poll();
			
			if(N.isProbablePrime(20))
			{
				task.setPartResult(N);
				continue;
			}
					
			BigInteger v = null;
			int k = 1;
			while((v = SQUFOF(task, N, k)) != null && v.equals(BigInteger.ONE)) {
				k++;
			}
			
			if(task.isTimeout()) {
				task.push(N);
				return;
			}
			
			BigInteger quo = N.divide(v);
			task.push(quo);
			task.push(v);			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BigInteger N = new BigInteger("1827368123");
		
		if(N.isProbablePrime(20)) {
			System.out.println(N);
			return;
		}
		
		int k = 1;
		
		Task task = new Task(1337, N, new Timing(2000));
		
		Factor_SQUFOF squ = new Factor_SQUFOF();
		squ.factor(task);
		System.out.println(task);
			

	}

}
