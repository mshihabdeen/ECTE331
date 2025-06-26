package proj_task3;

class Data {
	int A1,A2,A3,B1,B2,B3;
	boolean gotoB2,gotoA2,gotoB3,gotoA3 = false;
	boolean funcB3Complete = false;
}

class Utility{
	public static int calculate(int n) {
		int sum = 0;
		for(int i = 0; i <= n; i++) {
			sum += i;
		}
		return sum;
	}
}

class ThreadA extends Thread{
	private Data data;

	public ThreadA(Data data) {
		super();
		this.data = data;
	}
	
	public void run() {
		//FuncA1 block
		synchronized (data) {
			data.A1 = Utility.calculate(500);
			System.out.println("A1 completed: "+data.A1);
			data.gotoB2 = true;
			data.notifyAll();
		}
		
		//FuncA2 block
		synchronized(data) {
			try {
				while(data.gotoA2 == false){
					data.wait();
				}
				data.A2 = data.B2 + Utility.calculate(300);
				System.out.println("A2 completed: " + data.A2);
				data.gotoB3 = true;
				data.notifyAll();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//FuncA3 block
		synchronized(data) {
			try {
				while(data.gotoA3 == false){
					data.wait();
				}
				data.A3 = data.B3 + Utility.calculate(400);
				System.out.println("A3 completed: " + data.A3);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class ThreadB extends Thread{
	private Data data;

	public ThreadB(Data data) {
		super();
		this.data = data;
	}
	
	public void run() {
		//FuncB1 block
		synchronized (data) {
			data.B1 = Utility.calculate(250);
			System.out.println("B1 completed: "+data.B1);
		}
		
		//FuncB2 block
		synchronized(data) {
			try {
				while(data.gotoB2 == false){
					data.wait();
				}
				data.B2 = data.A1 + Utility.calculate(200);
				System.out.println("B2 completed: " + data.B2);
				data.gotoA2 = true;
				data.notifyAll();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//FuncB3 block
		synchronized(data) {
			try {
				while(data.gotoB3 == false){
					data.wait();
				}
				data.B3 = data.A2 + Utility.calculate(400);
				System.out.println("B3 completed: " + data.B3);
				data.gotoA3 = true;
				data.funcB3Complete = true;
				data.notifyAll();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class ThreadC extends Thread{
	private Data data;
	private int result;

	public ThreadC(Data data) {
		super();
		this.data = data;
	}
	
	public void run() {
		synchronized(data) {
			try {
				while(data.funcB3Complete == false){
					data.wait();
				}
				result = data.A2 + data.B3;
				System.out.println("Thread C result (A2 + B3): " + result);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getResult() {
		return result;
	}
}

public class Question3 {
	
	public static void main(String [] args) throws InterruptedException {
		System.out.println("Running single iteration test:");
		runSingleTest();
		
		System.out.println("\nRunning multiple iterations test:");
		runMultipleIterations(1000);
	}
	
	private static void runSingleTest() throws InterruptedException {
		Data data = new Data();
		ThreadA threadA = new ThreadA(data);
		ThreadB threadB = new ThreadB(data);
		ThreadC threadC = new ThreadC(data);
		
		threadA.start();
		threadB.start();
		threadC.start();
		
		threadA.join();
		threadB.join();
		threadC.join();
		
		System.out.println("Final values: A1=" + data.A1 + ", A2=" + data.A2 + ", A3=" + data.A3);
		System.out.println("Final values: B1=" + data.B1 + ", B2=" + data.B2 + ", B3=" + data.B3);
		System.out.println("Thread C result: " + threadC.getResult());
	}
	
	private static void runMultipleIterations(int iterations) throws InterruptedException {
		int expectedResult = calculateExpectedResult();
		int correctResults = 0;
		
		for(int i = 0; i < iterations; i++) {
			Data data = new Data();
			ThreadA threadA = new ThreadA(data);
			ThreadB threadB = new ThreadB(data);
			ThreadC threadC = new ThreadC(data);
			
			threadA.start();
			threadB.start();
			threadC.start();
			
			threadA.join();
			threadB.join();
			threadC.join();
			
			if(threadC.getResult() == expectedResult) {
				correctResults++;
			}
		}
		
		System.out.println("Test completed: " + correctResults + "/" + iterations + " correct results");
		System.out.println("Expected result: " + expectedResult);
		System.out.println("Success rate: " + (100.0 * correctResults / iterations) + "%");
	}
	
	private static int calculateExpectedResult() {
		int A1 = Utility.calculate(500);
		int B2 = A1 + Utility.calculate(200);
		int A2 = B2 + Utility.calculate(300);
		int B3 = A2 + Utility.calculate(400);
		return A2 + B3;
	}
}
