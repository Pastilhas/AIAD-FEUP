package util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogUtil {
    public static File logFile;

    public LogUtil() {
        File dir = new File("logs/"); // Create logs/ folder
        dir.mkdir();
        LocalDateTime now = LocalDateTime.now().withNano(0);

        logFile = new File("logs/" + now + ".txt");

        try {
            logFile.createNewFile(); // Create log file
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void write(String msg) {
        System.out.println(msg + logFile);
    }
}
