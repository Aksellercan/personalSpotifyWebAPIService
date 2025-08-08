package com.example.SpotifyWebAPI.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger Tool
 * Logs in varying severity levels such as INFO, WARN, ERROR, DEBUG and CRITICAL
 * Has one variable called debugOutput set to false by default
 */

public enum Logger {
    INFO(" [ INFO ] "),
    WARN(" [ WARN ] "),
    ERROR(" [ ERROR ] "),
    DEBUG(" [ DEBUG ] "),
    CRITICAL(" [ CRITICAL ] "),;

    private final String severity;
    private static boolean debugOutput = false;

    /**
     * Constructor for logger takes severity level as input
     * @param severity  Sets the severity
     */
    Logger(String severity) {
        this.severity = severity;
    }

    /**
     * Sets whether Logger should output DEBUG severity levels
     * @param debugOutput Takes a boolean to set
     */
    public static void setDebugOutput(boolean debugOutput) {
        Logger.debugOutput = debugOutput;
    }

    /**
     * Returns debugOutput value
     * @return debugOutput value
     */
    public static boolean getDebugOutput() {
        return debugOutput;
    }

    /**
     * Formats the message with date and the severity level
     * @return Formatted message string
     */
    private String DateSeverityFormat() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + severity;
    }

    /**
     * Logs message and writes it to logfile. If "debugOutput" is set to false it won't display it or write it to file
     * @param message   Message to be logged
     */
    public void Log(String message) {
        if (!debugOutput && this == DEBUG) {
            return;
        } else if (this == DEBUG) {
            Log(message,false);
            return;
        }
        Log(message, true);
    }

    /**
     * Logs message and writes it to logfile if "writetoFile" variable it set to true. If debugOutput is set to false it won't display it or write it to file
     * @param message   Message to be logged
     * @param writetoFile Whether to write to logfile or not
     */
    public void Log(String message, boolean writetoFile) {
        if (!debugOutput && this == DEBUG) {
            return;
        }
        String fullMessage = DateSeverityFormat() + message;
        if (writetoFile) {
            writeLog(fullMessage);
        }
        System.out.println(fullMessage);
    }

    /**
     * Used to log exceptions with messages and writes them to logfile
     * @param e Exception to be logged
     * @param message   Message to be logged
     */
    public void LogException(Exception e, String message) {
        WriteExceptionMessageLogs(e, message, true);
    }

    /**
     * Used to log exceptions with messages and writes them to logfile if "writetoFile" is set to true
     * @param e Exception to be logged
     * @param message   Message to be logged
     * @param writetoFile   Whether to write to logfile or not
     */
    public void LogException(Exception e, String message, boolean writetoFile) {
        WriteExceptionMessageLogs(e, message, writetoFile);
    }

    /**
     * Used to log exceptions and writes them to logfile
     * @param e Exception to be logged
     */
    public void LogException(Exception e) {
        WriteExceptions(e, true);
    }

    /**
     * Used to log exceptions and writes them to logfile if "writetoFile" is set to true
     * @param e Exception to be logged
     * @param writetoFile   Whether to write to logfile or not
     */
    public void LogException(Exception e, boolean writetoFile) {
        WriteExceptions(e, writetoFile);
    }

    /**
     * Formats Exceptions with date and severity then writes them to file if "writetoFile" is set to true
     * @param e Exception to be formatted
     * @param writetoFile   Whether to write to logfile or not
     */
    private void WriteExceptions(Exception e, boolean writetoFile) {
        String fullMessage = DateSeverityFormat()  + e.getMessage();
        if (writetoFile) {
            writeLog(fullMessage);
        }
        System.out.println(fullMessage);
    }

    /**
     * Formats Exceptions with message, date and severity then writes them to file if "writetoFile" is set to true
     * @param e Exception to be formatted
     * @param message   Message to be formatted
     * @param writetoFile   Whether to write to logfile or not
     */
    private void WriteExceptionMessageLogs(Exception e, String message, boolean writetoFile) {
        String fullMessage = DateSeverityFormat()  + message + ". Exception: " + e.getMessage();
        if (writetoFile) {
            writeLog(fullMessage);
        }
        System.out.println(fullMessage);
    }

    /**
     * Logs silently by not printing them to the console instead writes them to logfile
     * @param message   Message to be logged
     */
    public void LogSilently(String message) {
        String fullMessage = DateSeverityFormat() + message;
        writeLog(fullMessage);
    }

    /**
     * Logs exceptions with message silently by not printing them to the console instead writes them to logfile
     * @param e Exception to be logged
     * @param message   Message to be logged
     */
    public void LogExceptionSilently(Exception e, String message) {
        WriteExceptionMessageLogs(e, message, true);
    }

    /**
     * Writes the logs to logfile in directory "Logs/*" with filename as the date log was created with ".log" extension. Checks if the directory exists if not creates it.
     * Same for logfile as well, checks if the file exists if it does, it appends to it. Otherwise, creates it.
     * @param fullMessage   Full formatted message that will be written to logfile
     */
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
            LogException(e, "writeLog(String fullMessage)");
        }
    }
}