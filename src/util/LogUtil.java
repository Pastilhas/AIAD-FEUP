package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LogUtil {

    private static BufferedWriter writer;
    private String fileName;


    public LogUtil() throws IOException {
        File dir = new File("logs/"); // Create logs/ folder
        dir.mkdir();

        this.fileName = "logs/" + LocalDateTime.now().withNano(0) + ".txt"; // File name is current datetime

        File logFile = new File(this.fileName);

        logFile.createNewFile(); // Create log file

        this.writer = new BufferedWriter(new FileWriter(this.fileName, true)); // Create file writer

        writer.write(this.fileName + "\n\n\n");
    }

    public static void writeToLog(String msg, LocalTime timestamp) {
        System.out.println(msg);
        try {
            writer.write( "[" + timestamp + "]\t" + msg + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
