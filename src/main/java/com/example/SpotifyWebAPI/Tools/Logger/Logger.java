package com.example.SpotifyWebAPI.Tools.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger Tool
 * Logs in varying severity levels such as INFO, WARN, ERROR, DEBUG, CRITICAL and Thread specific ones like: THREAD: INFO, THREAD: WARN, THREAD: DEBUG, THREAD: ERROR, THREAD: CRITICAL
 */

public enum Logger {
    INFO(" [ INFO ] "),
    WARN(" [ WARN ] "),
    ERROR(" [ ERROR ] "),
    DEBUG(" [ DEBUG ] "),
    CRITICAL(" [ CRITICAL ] "),
    THREAD_INFO(" [ THREAD: INFO ] "),
    THREAD_DEBUG(" [ THREAD: DEBUG ] "),
    THREAD_WARN(" [ THREAD: WARN ] "),
    THREAD_ERROR(" [ THREAD: ERROR ] "),
    THREAD_CRITICAL(" [ THREAD: CRITICAL ] "),
    ;

    /**
     * Severity level of a log
     */
    private String severity;
    /**
     * Outputs DEBUG severity logs to console
     */
    private static boolean debugOutput = false;
    /**
     * Writes DEBUG severity logs to logfile
     */
    private static boolean verboseLogFile = false;
    /**
     * Colour codes logs
     */
    private static boolean colouredOutput = false;
    /**
     * Prints out detailed Exceptions in console and writes it to logfile
     */
    private static boolean enableStackTraces = false;
    /**
     * Disables all logging except Exceptions. Can be bypassed with force boolean
     */
    private static boolean quiet = false;

    /**
     * Sets log path
     */
    private static String log_path = "logs";

    /**
     * Constructor for logger takes severity level as input
     *
     * @param severity Sets the severity
     */
    Logger(String severity) {
        this.severity = severity;
    }

    /**
     * Sets whether Logger should output DEBUG severity levels
     *
     * @param debugOutput Takes a boolean to set
     */
    public static void setDebugOutput(boolean debugOutput) {
        Logger.debugOutput = debugOutput;
    }

    /**
     * Sets if debug severities should be included in log file or not
     *
     * @param verboseLogFile Takes a boolean to set
     */
    public static void setVerboseLogFile(boolean verboseLogFile) {
        Logger.verboseLogFile = verboseLogFile;
    }

    /**
     * Enable or disable ANSI colours on console
     *
     * @param colouredOutput Takes a boolean to set
     */
    public static void setColouredOutput(boolean colouredOutput) {
        Logger.colouredOutput = colouredOutput;
    }

    /**
     * Enable or disable detailed stack traces in log
     *
     * @param enableStackTraces Takes a boolean to set
     */
    public static void setEnableStackTraces(boolean enableStackTraces) {
        Logger.enableStackTraces = enableStackTraces;
    }

    /**
     * Enable or disable all logging. Only logs with "force" boolean will be logged
     *
     * @param setQuiet Takes a boolean to set
     */
    public static void setQuiet(boolean setQuiet) {
        quiet = setQuiet;
    }

    public static void setLog_path(String newLog_Path) {
        log_path = newLog_Path;
    }

    /**
     * Returns debugOutput value
     *
     * @return debugOutput value
     */
    public static boolean getDebugOutput() {
        return debugOutput;
    }

    /**
     * Returns colouredOutput value
     *
     * @return colouredOutput value
     */
    public static boolean getColouredOutput() {
        return colouredOutput;
    }

    /**
     * Returns verboseLogFile value
     *
     * @return verboseLogFile value
     */
    public static boolean getVerboseLogFile() {
        return verboseLogFile;
    }

    /**
     * Returns enableStackTraces value
     *
     * @return enableStackTraces value
     */
    public static boolean getEnableStackTraces() {
        return enableStackTraces;
    }

    /**
     * Returns quiet value
     *
     * @return quiet value
     */
    public static boolean getQuiet() {
        return quiet;
    }

    public static String getLog_path() {
        return log_path;
    }

    /**
     * Formats the message with date and the severity level
     *
     * @return Formatted message string
     */
    private String DateSeverityFormat() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + severity;
    }

    private String setThreadSeverity(Thread thread) {
        StringBuilder sb = new StringBuilder();
        boolean add = false;
        for (int i = 0; i < this.severity.length(); i++) {
            if (add) {
                sb.append(severity.charAt(i));
                continue;
            }
            if (severity.charAt(i) == ':') {
                add = true;
            }
        }
        String threadName;
        if (thread != null) threadName = thread.getName();
        else
            threadName = "lost-thread";
        return String.format(" [ %s:%s", threadName, sb);
    }

    /**
     * Logs message and writes it to logfile
     *
     * @param message Message to be logged
     */
    public void Log(String message) {
        if (!quiet) WriteLog(message, true);
    }

    /**
     * Logs message and writes it to logfile if "writeToFile" variable it set to true.
     * If debugOutput is set to false it won't display it or write it to file
     *
     * @param message     Message to be logged
     * @param writeToFile Whether to write to logfile or not
     */
    public void Log(String message, boolean writeToFile) {
        if (!quiet) WriteLog(message, writeToFile);
    }

    /**
     * Bypasses quiet flag if it's forced using force boolean. Formula p -> q implication where p is quiet flag and q is force boolean.
     *
     * @param message     Message to be logged
     * @param writeToFile Whether to write to logfile or not
     * @param force       Forcefully bypass quiet flag
     */

    public void Log(String message, boolean writeToFile, boolean force) {
        // (p -> q) implication
        if (quiet) if (force) WriteLog(message, writeToFile);
        if (!quiet) WriteLog(message, writeToFile);
    }

    /**
     * Logs if statement is true. Logs to log file by default
     *
     * @param statement Boolean statement
     */
    public void LogIfTrue(boolean statement) {
        if (statement) if (!quiet) WriteLog("Statement is " + statement, true);
    }

    /**
     * Logs if statement is true with a message in beginning, connecting with " is ". Logs to log file by default
     *
     * @param statement Boolean statement
     */
    public void LogIfTrue(String message, boolean statement) {
        if (statement) if (!quiet) WriteLog(message + " is " + statement, true);
    }


    /**
     * Logs if statement is true
     *
     * @param statement   Boolean statement
     * @param writeToFile Whether to write to log file or not
     */
    public void LogIfTrue(boolean statement, boolean writeToFile) {
        if (statement) if (!quiet) WriteLog("Statement is " + statement, writeToFile);
    }

    /**
     * Logs if statement is true, for bypassing quiet flag
     *
     * @param statement   Boolean statement
     * @param writeToFile Whether to write to log file or not
     * @param force       Bypass quiet flag
     */
    public void LogIfTrue(boolean statement, boolean writeToFile, boolean force) {
        if (statement) {
            if (quiet) {
                if (force) WriteLog("Statement is " + statement, writeToFile);
            }
            if (!quiet) WriteLog("Statement is " + statement, writeToFile);
        }
    }

    /**
     * Logs if statement is true with a message in beginning, connecting with " is "
     *
     * @param message     Message to be logged
     * @param statement   Boolean statement
     * @param writeToFile Whether to write to log file or not
     */
    public void LogIfTrue(String message, boolean statement, boolean writeToFile) {
        if (statement) if (!quiet) WriteLog(message + " is " + statement, writeToFile);
    }

    /**
     * Logs if statement is true with a message in beginning, connecting with " is ". For bypassing quiet flag
     *
     * @param message     Message to be logged
     * @param statement   Boolean statement
     * @param writeToFile Whether to write to log file or not
     * @param force       Bypass quiet flag
     */
    public void LogIfTrue(String message, boolean statement, boolean writeToFile, boolean force) {
        if (statement) {
            if (quiet) {
                if (force) WriteLog(message + " is " + statement, writeToFile);
            }
            if (!quiet) WriteLog(message + " is " + statement, writeToFile);
        }
    }

    /**
     * Logs message associated to the thread it's running on
     *
     * @param thread  Thread object
     * @param message Message to be logged
     */
    public void LogThread(Thread thread, String message) {
        if (!quiet) {
            this.severity = setThreadSeverity(thread);
            WriteLog(message, true);
        }
    }

    /**
     * Logs message associated to the thread it's running on
     *
     * @param thread      Thread object
     * @param message     Message to be logged
     * @param writeToFile Whether to write to logfile or not
     */
    public void LogThread(Thread thread, String message, boolean writeToFile) {
        if (!quiet) {
            this.severity = setThreadSeverity(thread);
            WriteLog(message, writeToFile);
        }
    }

    /**
     * Logs message associated to the thread it's running on
     *
     * @param thread      Thread object
     * @param message     Message to be logged
     * @param writeToFile Whether to write to logfile or not
     * @param force       Bypass quiet flag
     */
    public void LogThread(Thread thread, String message, boolean writeToFile, boolean force) {
        // (p -> q) implication
        this.severity = setThreadSeverity(thread);
        if (quiet) if (force) WriteLog(message, writeToFile);
        if (!quiet) WriteLog(message, writeToFile);
    }

    /**
     * Decides what to do with logs depending on settings.
     * If only "debugOutput" is set to false it won't display it and by default will not write to file.
     * If "debugOutput" is set to false but "verboseLogFile" is set to true, It will only write it to log file.
     * If only "verboseLogFile" is set to false but "debugOutput" is set to true, then it will only display it on console.
     * Parameter "writeToFile" still has priority on deciding whether to write to file or not, however.
     *
     * @param message     Message to be logged
     * @param writeToFile Whether to write to log file or not
     */
    private void WriteLog(String message, boolean writeToFile) {
        String fullMessage = DateSeverityFormat() + message;
        if (!debugOutput && (this == DEBUG || this == THREAD_DEBUG)) {
            if (verboseLogFile && writeToFile) SaveLog(fullMessage);
            return;
        } else if (this == DEBUG) {
            if (verboseLogFile && writeToFile) SaveLog(fullMessage);
            ColourOutput(fullMessage);
            return;
        }
        if (writeToFile) {
            SaveLog(fullMessage);
        }
        ColourOutput(fullMessage);
    }

    /**
     * Colour console output depending on severity level
     *
     * @param fullMessage Message to be logged
     */
    private void ColourOutput(String fullMessage) {
        if (colouredOutput) {
            switch (this) {
                case THREAD_INFO:
                    System.out.println(ConsoleColours.GREEN_BOLD_BRIGHT + fullMessage + ConsoleColours.RESET);
                    break;
                case THREAD_DEBUG:
                    System.out.println(ConsoleColours.BLUE_BOLD_BRIGHT + fullMessage + ConsoleColours.RESET);
                    break;
                case THREAD_WARN:
                    System.out.println(ConsoleColours.YELLOW_BOLD_BRIGHT + fullMessage + ConsoleColours.RESET);
                    break;
                case THREAD_ERROR:
                    System.out.println(ConsoleColours.RED_BOLD + fullMessage + ConsoleColours.RESET);
                    break;
                case THREAD_CRITICAL:
                    System.out.println(ConsoleColours.RED_BOLD_BRIGHT + fullMessage + ConsoleColours.RESET);
                    break;
                case WARN:
                    System.out.println(ConsoleColours.YELLOW + fullMessage + ConsoleColours.RESET);
                    break;
                case DEBUG:
                    System.out.println(ConsoleColours.BLUE + fullMessage + ConsoleColours.RESET);
                    break;
                case ERROR:
                    System.out.println(ConsoleColours.RED_BRIGHT + fullMessage + ConsoleColours.RESET);
                    break;
                case CRITICAL:
                    System.out.println(ConsoleColours.RED + fullMessage + ConsoleColours.RESET);
                    break;
                case INFO:
                    System.out.println(ConsoleColours.GREEN + fullMessage + ConsoleColours.RESET);
                    break;
                default:
                    System.out.println(fullMessage);
            }
        } else {
            System.out.println(fullMessage);
        }
    }

    /**
     * Used to log exceptions with messages and writes them to logfile
     *
     * @param e       Exception to be logged
     * @param message Message to be logged
     */
    public void LogException(Exception e, String message) {
        WriteExceptionMessageLogs(e, message, true);
    }

    /**
     * Used to log exceptions with messages and writes them to logfile if "writeToFile" is set to true
     *
     * @param e           Exception to be logged
     * @param message     Message to be logged
     * @param writeToFile Whether to write to logfile or not
     */
    public void LogException(Exception e, String message, boolean writeToFile) {
        WriteExceptionMessageLogs(e, message, writeToFile);
    }

    /**
     * Used to log exceptions and writes them to logfile
     *
     * @param e Exception to be logged
     */
    public void LogException(Exception e) {
        WriteExceptions(e, true);
    }

    /**
     * Used to log exceptions and writes them to logfile if "writeToFile" is set to true
     *
     * @param e           Exception to be logged
     * @param writeToFile Whether to write to logfile or not
     */
    public void LogException(Exception e, boolean writeToFile) {
        WriteExceptions(e, writeToFile);
    }

    public void LogThreadException(Thread thread, Exception e) {
        this.severity = setThreadSeverity(thread);
        WriteExceptions(e, true);
    }

    public void LogThreadException(Thread thread, Exception e, boolean writeToFile) {
        this.severity = setThreadSeverity(thread);
        WriteExceptions(e, writeToFile);
    }

    public void LogThreadException(Thread thread, Exception e, String message) {
        this.severity = setThreadSeverity(thread);
        WriteExceptionMessageLogs(e, message, true);
    }

    public void LogThreadException(Thread thread, Exception e, String message, boolean writeToFile) {
        this.severity = setThreadSeverity(thread);
        WriteExceptionMessageLogs(e, message, writeToFile);
    }

    /**
     * Formats Exceptions with date and severity then writes them to file if "writeToFile" is set to true
     *
     * @param e           Exception to be formatted
     * @param writeToFile Whether to write to logfile or not
     */
    private void WriteExceptions(Exception e, boolean writeToFile) {
        String fullMessage;
        if (enableStackTraces) {
            fullMessage = DateSeverityFormat() + e.getMessage() + "\n" + GetStackTraceAsString(e);
        } else {
            fullMessage = DateSeverityFormat() + e.getMessage();
        }
        if (writeToFile) {
            SaveLog(fullMessage);
        }
        ColourOutput(fullMessage);
    }

    /**
     * Formats Exceptions with message, date and severity then writes them to file if "writeToFile" is set to true
     *
     * @param e           Exception to be formatted
     * @param message     Message to be formatted
     * @param writeToFile Whether to write to logfile or not
     */
    private void WriteExceptionMessageLogs(Exception e, String message, boolean writeToFile) {
        String fullMessage;
        if (enableStackTraces) {
            fullMessage = DateSeverityFormat() + message + ". Exception: " + e.getMessage() + "\n" + GetStackTraceAsString(e);
        } else {
            fullMessage = DateSeverityFormat() + message + ". Exception: " + e.getMessage();
        }
        if (writeToFile) {
            SaveLog(fullMessage);
        }
        ColourOutput(fullMessage);
    }

    /**
     * Gets detailed stack trace and returns it as string with tab indentation
     *
     * @param e Exception stack trace to be returned
     * @return Stacktrace with tab indentation
     */
    private String GetStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < e.getStackTrace().length; i++) {
            if (i + 1 == e.getStackTrace().length) {
                sb.append("\t").append(e.getStackTrace()[i]);
                continue;
            }
            sb.append("\t").append(e.getStackTrace()[i]).append("\n");
        }
        return sb.toString();
    }

    /**
     * Logs silently by not printing them to the console instead writes them to logfile
     *
     * @param message Message to be logged
     */
    public void LogSilently(String message) {
        String fullMessage = DateSeverityFormat() + message;
        SaveLog(fullMessage);
    }

    /**
     * Logs exceptions with message silently by not printing them to the console instead writes them to logfile
     *
     * @param e       Exception to be logged
     * @param message Message to be logged
     */
    public void LogExceptionSilently(Exception e, String message) {
        WriteExceptionMessageLogs(e, message, true);
    }

    /**
     * Writes the logs to logfile in directory "logs/*" with filename as current date with ".log" extension. Checks if the directory exists if not creates it.
     * Same for logfile as well, checks if the file exists if it does append to it. Otherwise, creates it.
     *
     * @param fullMessage Full formatted message that will be written to logfile
     */
    private void SaveLog(String fullMessage) {
        try (FileWriter writer = new FileWriter(getLogFile(generateFilename()), true)) {
            writer.write(fullMessage + "\n");
        } catch (IOException e) {
            CRITICAL.LogException(e, "Error while writing logs", false);
        }
    }

    /**
     * Generate filename in format: log_day-month-year.log
     *
     * @return Generated filename
     */
    private String generateFilename() {
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return "log_" + today.format(formatter);
    }

    /**
     * Checks if log path is valid and if parameter filename exists. If the file does not exist then creates and returns it
     *
     * @param fileName logfile name
     * @return return the found logfile
     * @throws IOException If the file doesn't exist
     */
    private File getLogFile(String fileName) throws IOException {
        File logPath = new File(log_path);
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
        return logFile;
    }
}