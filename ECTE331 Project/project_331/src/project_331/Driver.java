package project_331;

import java.util.Random;
import java.util.Scanner;

public class Driver {
	
	private static Scanner sc = new Scanner(System.in); 
	private static final Random random = new Random ();
	
	public static void main (String[] args) {
		
		double cap;
		
		System.out.println("Enter a temperature cap ");
		cap = sc.nextDouble();
		
		double temperature =  generateTemp(cap);
		System.out.println("The randomly generated temperature is: "+ temperature);
		
		
		double humidity = generateHumidity();
		System.out.println("The randomly generated humidity is: "+ humidity+"%");
		
		double [] thirdSensor = new double [3];
		
		for (int i=0;i<3; i++) {
			thirdSensor[i] = generateThird();
			System.out.println("Value "+ i+ "of thirdSensor is "+thirdSensor[i]);
		}
	}
	
	private static double generateThird() {
		return (int)(random.nextDouble()*10);
	}
	
	private static double generateTemp(double cap) {
		return random.nextDouble()*cap;
	}
	
	private static double generateHumidity () {
		return random.nextDouble()*100;
	}
}
