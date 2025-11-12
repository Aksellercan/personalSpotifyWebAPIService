package com.example.SpotifyWebAPI.Tools.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class LoggerBackend implements LoggerBackendInterface, Runnable {

    @Override
    public void run() {
        Thread.currentThread().setName(generateThreadName());
        Logger.THREAD_INFO.LogThread(Thread.currentThread(), "Started logger service on thread: " + Thread.currentThread().getName());
        keepThreadAlive();
    }

    public String generateThreadName() {
        Random rand = new Random();
        long id = rand.nextLong();
        return Thread.currentThread().getName() + "_LoggerBackend_" + (id > 0 ? id : -1*id);
    }

    public void keepThreadAlive() {
        while (!Thread.interrupted()) {
            if (!logQueue.isEmpty()) {
                for (Log log : logQueue) {
                    if (!log.isLogged()) {
                        if (log.isSilent()) {
                            saveLog(DateSeverityFormat(log) + log.getMessage());
                            log.setLogged(true);
                            logQueue.remove(log);
                            continue;
                        }
                        writeLogController(log);
                    };
                }
            }
        }
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
    private void writeLogController(Log log) {
        String fullMessage = DateSeverityFormat(log) + log.getMessage();
        if (!LoggerSettings.getDebugOutput() && (log.getSeverityEnum() == Logger.DEBUG || log.getSeverityEnum() == Logger.THREAD_DEBUG)) {
            if (LoggerSettings.getVerboseLogFile() && log.isWriteToFile()) saveLog(fullMessage);
            log.setLogged(true);
            logQueue.remove(log);
            return;
        } else if (log.getSeverityEnum() == Logger.DEBUG) {
            if (LoggerSettings.getVerboseLogFile() && log.isWriteToFile()) saveLog(fullMessage);
            ColourOutput(log, fullMessage);
            log.setLogged(true);
            logQueue.remove(log);
            return;
        }
        if (log.isWriteToFile()) {
            saveLog(fullMessage);
        }
        ColourOutput(log, fullMessage);
        log.setLogged(true);
        logQueue.remove(log);
    }

    public void saveLog(String fullMessage) {
        try (FileWriter writer = new FileWriter(getLogFile(generateFilename()), true)) {
            writer.write(fullMessage + "\n");
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

    @Override
    public String DateSeverityFormat(Log log) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + log.getSeverity();
    }

    @Override
    public void ColourOutput(Log log, String fullMessage) {
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

    private void WriteExceptions(LogExceptionObject log) {
        String fullMessage;
        if (LoggerSettings.getEnableStackTraces()) {
            fullMessage = DateSeverityFormat(log) + log.getException().getMessage() + "\n" + GetStackTraceAsString(log.getException());
        } else {
            fullMessage = DateSeverityFormat(log) + log.getException().getMessage();
        }
        if (log.isWriteToFile()) {
            saveLog(fullMessage);
        }
        ColourOutput(log, fullMessage);
    }

    private void WriteExceptionMessageLogs(LogExceptionObject log) {
        String fullMessage;
        if (LoggerSettings.getEnableStackTraces()) {
            fullMessage = DateSeverityFormat(log) + log.getMessage() + ". Exception: " + log.getException().getMessage() + "\n" + GetStackTraceAsString(log.getException());
        } else {
            fullMessage = DateSeverityFormat(log) + log.getMessage() + ". Exception: " + log.getException().getMessage();
        }
        if (log.isWriteToFile()) {
            saveLog(fullMessage);
        }
        ColourOutput(log, fullMessage);
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
}
