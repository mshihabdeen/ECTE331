package proj_task3;

class Data {
	int A1,A2,A3,B1,B2,B3;
	//create boolean flags for wait() and notify ()
	boolean gotoB2,gotoA2,gotoB3,gotoA3 = false;
}

class Utility{
	public static int calculate(int n) {
		return (n*(n+1)/2);
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
			data.notify();
		}
		
		//FuncA2 block
		synchronized(data) {
			try {
				while(data.gotoA2 == false){
					data.wait();
					System.out.println("Currently in A2, waiting for B2 to send notify()");
					}
				data.A2 = data.B2 + Utility.calculate(300);
				System.out.println("A2 completed: " + data.A2);
				data.gotoB3 = true;
				data.notify();
				}
			catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

public class Question3 {
	
	public static void main(String [] args) {
		
	}

}
