package project_331;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

public class Driver {

    private static final Scanner sc = new Scanner(System.in);
    private static final Random random = new Random();
    private static double previousValidSensor3Value = -1;

    public static void main(String[] args) {

        System.out.print("Enter a temperature cap: ");
        double cap = sc.nextDouble();

        double temperature = generateTemp(cap);
        System.out.println("Generated Temperature: " + temperature);

        double humidity = generateHumidity();
        System.out.println("Generated Humidity: " + humidity + "%");

        double[] thirdSensor = new double[3];
        for (int i = 0; i < 3; i++) {
            thirdSensor[i] = generateThird();
            System.out.println("Sensor 3." + (i + 1) + ": " + thirdSensor[i]);
        }

        double votedValue = majorityVote(thirdSensor);
        System.out.println("Sensor 3 Final Value: " + votedValue);
    }

    private static double generateThird() {
        return (int)(random.nextDouble() * 10);
    }

    private static double generateTemp(double cap) {
        return random.nextDouble() * cap;
    }

    private static double generateHumidity() {
        return random.nextDouble() * 100;
    }

    private static double majorityVote(double[] values) {
        if (values[0] == values[1] || values[0] == values[2]) {
            previousValidSensor3Value = values[0];
            return values[0];
        } else if (values[1] == values[2]) {
            previousValidSensor3Value = values[1];
            return values[1];
        } else {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String outliers = String.format("%s - Discrepancy in Sensor 3 readings: 3.1=%.1f, 3.2=%.1f, 3.3=%.1f", time, values[0], values[1], values[2]);
            FileLogger.log(outliers);
            System.out.println("Discrepancy detected. Using previous valid value: " + previousValidSensor3Value);
            return previousValidSensor3Value;
        }
    }
}
