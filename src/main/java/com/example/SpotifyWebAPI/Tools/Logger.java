package com.example.SpotifyWebAPI.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum Logger {
    INFO(" [ INFO ] "),
    WARN(" [ WARN ] "),
    ERROR(" [ ERROR ] "),
    DEBUG(" [ DEBUG ] "),
    ;
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
        Log(message, true);
    }

    public void Log(String message, boolean writetoFile) {
        String fullMessage = logFormat(message);
        if (writetoFile) {
            writeLog(fullMessage);
        }
        if (!debugOutput && this == DEBUG) {
            debugLogs(message);
            return;
        }
        System.out.println(fullMessage);
    }

    public void LogSilently(String message) {
        String fullMessage = logFormat(message);
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
            ERROR.Log("writeLog(String fullMessage) Error: " + e.getMessage(), false);
        }
    }
}