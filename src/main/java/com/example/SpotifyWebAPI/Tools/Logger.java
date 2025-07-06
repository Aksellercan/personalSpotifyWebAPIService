package com.example.SpotifyWebAPI.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public enum Logger {
    INFO(" [ INFO ] "),
    WARN(" [ WARN ] "),
    ERROR(" [ ERROR ] "),
    DEBUG(" [ DEBUG ] ");

    private final String severity;
    private boolean debugOutput = false;

    Logger(String severity) {
        this.severity = severity;
    }

    public void setDebugOutput(boolean debugOutput) {
        this.debugOutput = debugOutput;
    }

    public boolean getDebugOutput() {
        return debugOutput;
    }

    private String logFormat(String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + severity + message;
    }

    public void Log(String message) {
        if (!debugOutput && this == DEBUG) {
            debugLogs(message);
            return;
        }
        Log(message, true);
    }

    public void Log(String message, boolean writetoFile) {
        String fullMessage = logFormat(message);
        if (writetoFile) {
            writeLog(fullMessage);
        }
        System.out.println(fullMessage);
    }

    public void Log(Exception e, String message) {
        System.out.println(WriteExceptionMessageLogs(e, message));
    }

    public void Log(Exception e, boolean writetoFile) {
        String exceptionLog = WriteExceptions(e);
        System.out.println(exceptionLog);
        if (writetoFile) {
            writeLog(exceptionLog);
        }
    }

    public void Log(Exception e) {
        System.out.println(WriteExceptions(e));
    }

    public void Log(Exception e, String message, boolean writetoFile) {
        String exceptionLog = WriteExceptionMessageLogs(e, message);
        System.out.println(exceptionLog);
        if (writetoFile) {
            writeLog(exceptionLog);
        }
    }

    private String WriteExceptions(Exception e) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + severity + e.getMessage();
    }

    private String WriteExceptionMessageLogs(Exception e, String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + severity + message + ". Exception: " + e.getMessage();
    }

    public void LogSilently(String message) {
        String fullMessage = logFormat(message);
        writeLog(fullMessage);
    }

    public void LogSilently(Exception e, String message) {
        String fullMessage = WriteExceptionMessageLogs(e, message);
        writeLog(fullMessage);
    }

    private void debugLogs(String message) {
        String fullMessage = logFormat(message);
        System.out.println(fullMessage);
    }

    private void writeLog(String fullMessage) {
        try {
            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fileName = "log_" + today.format(formatter);
            File logPath = new File("Logs");
            if (!logPath.exists()) {
                boolean createDir = logPath.mkdirs();
                if (!createDir) {
                    throw new IOException("Could not create log directory");
                }
            }
            File logFile = new File(logPath + File.separator + fileName + ".log");
            if (!logFile.exists()) {
                boolean createFile = logFile.createNewFile();
                if (!createFile) {
                    throw new IOException("Could not create log file");
                }
            }
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(fullMessage + "\n");
            }
        } catch (IOException e) {
            Log(e, "writeLog(String fullMessage)");
        }
    }
}