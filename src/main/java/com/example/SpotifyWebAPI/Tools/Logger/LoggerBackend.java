package com.example.SpotifyWebAPI.Tools.Logger;

import com.example.SpotifyWebAPI.Tools.Logger.Interfaces.LoggerBackendInterface;
import com.example.SpotifyWebAPI.Tools.Logger.Objects.LogExceptionObject;
import com.example.SpotifyWebAPI.Tools.Logger.Objects.LogObject;
import com.example.SpotifyWebAPI.Tools.Logger.Utility.ConsoleColours;
import com.example.SpotifyWebAPI.Tools.Logger.Utility.LoggerSettings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Logger backend runs on its own thread and monitors log queue from LoggerQueueInterface
 */
public class LoggerBackend implements LoggerBackendInterface, Runnable {

    /**
     * Start thread
     */
    @Override
    public void run() {
        try {
            Thread.currentThread().setName(generateThreadName());
            Logger.THREAD_INFO.LogThread(Thread.currentThread(), String.format("Started logger service on thread: %s :: Daemon = %s", Thread.currentThread().getName(), Thread.currentThread().isDaemon()));
            keepThreadAlive();
        } catch (InterruptedException e) {
            Logger.THREAD_CRITICAL.LogThreadException(Thread.currentThread(), e, "Thread Interrupted");
        }
    }

    /**
     * Generates Thread name with randomly generated long
     * @return  Generated Thread name
     */
    private String generateThreadName() {
        Random rand = new Random();
        long id = rand.nextLong();
        return Thread.currentThread().getName() + "_LoggerBackend_" + (id > 0 ? id : -1*id);
    }

    // **May** keep one thread at 100% use

    /**
     * Keeps thread from getting killed
     */
    public void keepThreadAlive() throws InterruptedException {
        while (!Thread.interrupted()) {
            if (!logQueue.isEmpty()) {
                for (LogObject log : logQueue) {
                    if (!log.isLogged()) {
                        log.setLogged(Controller(log));
                        logQueue.remove(log);
                    }
                }
            }
            Thread.sleep(LoggerSettings.getLoggerCheckEvery());
        }
    }

    /**
     * Gets the result of Implication (->)
     * @param log   Log object
     * @return  result of implication
     */
    private boolean getQuietImplicationResult(LogObject log) {
        if (LoggerSettings.getQuiet()) {
            if (log.isForce()) {
                return true;
            }
        }
        return !LoggerSettings.getQuiet();
    }

    /**
     * Controls which functions logs go to
     * @param log   Log/LogException/LogThread object
     * @return  successfully logged or not
     */
    private boolean Controller(LogObject log) {
        try {
            if (!getQuietImplicationResult(log)) return true;
            if (log.isSilent()) {
                saveLog(dateSeverityFormat(log) + log.getMessage());
                log.setLogged(true);
                return true;
            }
            if (log instanceof LogExceptionObject) {
                LogExceptionObject logExceptionObject = (LogExceptionObject) log;
                if (logExceptionObject.isExceptionLog()) {
                    if (logExceptionObject.getMessage() == null)
                        writeExceptions(logExceptionObject);
                    else
                        writeExceptionMessageLogs(logExceptionObject);
                    return true;
                }
            }
            writeLog(log);
        } catch (Exception e) {
            Logger.THREAD_CRITICAL.LogThreadException(Thread.currentThread(), e, "Failed to log for log with ID " + log.getId());
            return false;
        }
        return true;
    }

    /**
     * Decides what to do with logs depending on settings.
     * If only "debugOutput" is set to false it won't display it and by default will not write to file.
     * If "debugOutput" is set to false but "verboseLogFile" is set to true, It will only write it to log file.
     * If only "verboseLogFile" is set to false but "debugOutput" is set to true, then it will only display it on console.
     * Parameter "writeToFile" still has priority on deciding whether to write to file or not, however.
     *
     * @param log Log from queue
     */
    private void writeLog(LogObject log) {
        String fullMessage = dateSeverityFormat(log) + log.getMessage();
        if (!LoggerSettings.getDebugOutput() && (log.getSeverityEnum() == Logger.DEBUG || log.getSeverityEnum() == Logger.THREAD_DEBUG)) {
            if (LoggerSettings.getVerboseLogFile() && log.isWriteToFile()) saveLog(fullMessage);
            log.setLogged(true);
            return;
        } else if (log.getSeverityEnum() == Logger.DEBUG) {
            if (LoggerSettings.getVerboseLogFile() && log.isWriteToFile()) saveLog(fullMessage);
            colourOutput(log, fullMessage);
            log.setLogged(true);
            return;
        }
        if (log.isWriteToFile()) {
            saveLog(fullMessage);
        }
        colourOutput(log, fullMessage);
        log.setLogged(true);
    }

    /**
     * Writes log to file
     * @param fullMessage   Full log message with date/severity/message/exception/threadName
     */
    public synchronized void saveLog(String fullMessage) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(getLogFile(generateFilename()), true))) {
            bufferedWriter.write(fullMessage + "\n");
        } catch (IOException e) {
            Logger.CRITICAL.LogException(e, "Error while writing logs", false);
        }
    }

    /**
     * Generate filename in format: log_day-month-year.log
     *
     * @return Generated filename
     */
    public String generateFilename() {
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
    public File getLogFile(String fileName) throws IOException {
        File logPath = new File(LoggerSettings.getLog_path());
        if (!Files.exists(logPath.toPath())) {
            Path createdLogPath = Files.createDirectories(logPath.toPath());
            if (!Files.exists(createdLogPath)) {
                throw new IOException("Could not create log directory");
            }
        }
        File logFile = new File(logPath + File.separator + fileName + ".log");
        if (!Files.exists(logFile.toPath())) {
            Path createdLogFile = Files.createFile(logFile.toPath());
            if (!Files.exists(createdLogFile)) {
                throw new IOException("Could not create log file");
            }
        }
        return logFile;
    }

    /**
     * Combines Date and Severity string together
     * @param log   Log object
     * @return  formatted date and log severity
     */
    @Override
    public String dateSeverityFormat(LogObject log) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + log.getSeverity();
    }

    /**
     * Colours log messages and outputs them to STDOUT if colouredOutput is set to true
     * @param log   Log object
     * @param fullMessage   Full message with formatting
     */
    @Override
    public void colourOutput(LogObject log, String fullMessage) {
        if (LoggerSettings.getColouredOutput()) {
            switch (log.getSeverityEnum()) {
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
     * Handles writing exceptions. If enableStackTraces is set to true, it will include stacktraces in the log
     * @param log   LogException object
     */
    private void writeExceptions(LogExceptionObject log) {
        String fullMessage;
        if (LoggerSettings.getEnableStackTraces()) {
            fullMessage = dateSeverityFormat(log) + log.getException().getMessage() + "\n" + getStackTraceAsString(log.getException());
        } else {
            fullMessage = dateSeverityFormat(log) + log.getException().getMessage();
        }
        if (log.isWriteToFile()) {
            saveLog(fullMessage);
        }
        colourOutput(log, fullMessage);
    }


    /**
     * Handles writing exceptions with messages. If enableStackTraces is set to true, it will include stacktraces in the log
     * @param log   LogException object
     */
    private void writeExceptionMessageLogs(LogExceptionObject log) {
        String fullMessage;
        if (LoggerSettings.getEnableStackTraces()) {
            fullMessage = dateSeverityFormat(log) + log.getMessage() + ". Exception: " + log.getException().getMessage() + "\n" + getStackTraceAsString(log.getException());
        } else {
            fullMessage = dateSeverityFormat(log) + log.getMessage() + ". Exception: " + log.getException().getMessage();
        }
        if (log.isWriteToFile()) {
            saveLog(fullMessage);
        }
        colourOutput(log, fullMessage);
    }

    /**
     * Gets detailed stack trace and returns it as string with tab indentation
     *
     * @param e Exception stack trace to be returned
     * @return Stacktrace with tab indentation
     */
    private String getStackTraceAsString(Exception e) {
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
}
