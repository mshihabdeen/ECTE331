package project_331;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FileLogger {

    public static final int MAX_BACKUP = 3;
    private static final Random random = new Random();
    private static final String BASE_FILE = "log.txt";

    public static void log(String message) {
        boolean success = false;

        try {
            simulateFailure();
            writeToFile(BASE_FILE, message);
            success = true;
        } catch (IOException e) {
            for (int i = 1; i <= MAX_BACKUP; i++) {
                String backupFile = "log" + i + ".txt";
                try {
                    simulateFailure();
                    writeToFile(backupFile, message);
                    success = true;
                    break;
                } catch (IOException ignored) {
                    // try next
                }
            }
        }

        if (!success) {
            try {
                writeToFile("principal_log.txt", "Failed to log message: " + message);
            } catch (IOException finalFail) {
                System.err.println("Logging failed completely.");
            }
        }
    }

    private static void writeToFile(String fileName, String message) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        fw.write(message + System.lineSeparator());
        fw.close();
    }

    private static void simulateFailure() throws IOException {
        if (random.nextInt(100) < 40) {
            throw new IOException("Simulated write failure");
        }
    }
}
