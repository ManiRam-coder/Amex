package org.cron;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * CronSchedulerUtil is a utility to read cron-style commands from a file,
 * parse their schedule, and run them at the appropriate times using a scheduler.
 * It supports basic cron syntax: minute, hour, day, month, and year.
 */
public class CronSchedulerUtil {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final String TMP_DIR_PROPERTY = "java.io.tmpdir";
    private static final String FILE_NAME = "commands.txt";

    public static void main(String[] args) {
        String tmpDir = System.getProperty(TMP_DIR_PROPERTY);
        String filePath = tmpDir + "/" + FILE_NAME;

        CronSchedulerUtil util = new CronSchedulerUtil();
        util.readFile(filePath);
    }

    public void readFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File not found: " + file.getAbsolutePath());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String command;
            while ((command = br.readLine()) != null) {
                parseAndSchedule(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseAndSchedule(String line) {
        try {
            Pattern pattern = Pattern.compile("^([\\d*/,-]+)\\s+([\\d*/,-]+)\\s+([\\d*/,-]+)\\s+([\\d*/,-]+)\\s+([\\d*/,-]+)\\s+(.+)$");
            Matcher matcher = pattern.matcher(line);

            if (!matcher.matches()) {
                System.err.println("Invalid cron format: " + line);
                return;
            }

            String minuteField = matcher.group(1);
            String hourField = matcher.group(2);
            String dayField = matcher.group(3);
            String monthField = matcher.group(4);
            String yearField = matcher.group(5);
            String command = matcher.group(6);

            Runnable task = () -> runShellCommand(command);

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    ZonedDateTime now = ZonedDateTime.now();
                    if (matches(now.getMinute(), minuteField, 0, 59) &&
                            matches(now.getHour(), hourField, 0, 23) &&
                            matches(now.getDayOfMonth(), dayField, 1, 31) &&
                            matches(now.getMonthValue(), monthField, 1, 12) &&
                            matches(now.getYear(), yearField, 1970, 2099)) {

                        scheduler.execute(task);
                    }
                } finally {

                }
            }, 0, 60, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runShellCommand(String command) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            ProcessBuilder builder;
            if (osName.contains("windows")) {
                builder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                builder = new ProcessBuilder("bash", "-c", command);
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();

            String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining(" "));
            System.out.println(output);

            process.waitFor();
        } catch (Exception e) {
            System.err.println("[ERROR] " + command);
            e.printStackTrace();
        }
    }

    public boolean matches(int value, String field, int min, int max) {
        if (field.equals("*")) return true;

        for (String part : field.split(",")) {
            if (part.contains("/")) {
                String[] split = part.split("/");
                int base = split[0].equals("*") ? min : Integer.parseInt(split[0]);
                int step = Integer.parseInt(split[1]);
                if ((value - base) % step == 0 && value >= base) return true;
            } else if (part.contains("-")) {
                String[] range = part.split("-");
                int start = Integer.parseInt(range[0]);
                int end = Integer.parseInt(range[1]);
                if (value >= start && value <= end) return true;
            } else {
                if (Integer.parseInt(part) == value) return true;
            }
        }
        return false;
    }
}