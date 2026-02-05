package com.example.SpotifyWebAPI.Tools.Logger;

import com.example.SpotifyWebAPI.Tools.Logger.Interfaces.LoggerInterface;
import com.example.SpotifyWebAPI.Tools.Logger.Objects.LogExceptionObject;
import com.example.SpotifyWebAPI.Tools.Logger.Objects.LogObject;
import com.example.SpotifyWebAPI.Tools.Logger.Objects.LogThreadObject;

/**
 * Front end of Logger, runs synchronously and adds logs to log queue for backend to handle
 */
public enum Logger implements LoggerInterface {
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
    private final String severity;

    Logger(String severity) {
        this.severity = severity;
    }

    @Override
    public void Log(String message) {
        logQueue.add(new LogObject(0,
                this,
                severity,
                message));
    }

    @Override
    public void Log(String message, boolean writeToFile) {
        logQueue.add(new LogObject(
                (long) logQueue.size()+1,
                this,severity,
                message,
                writeToFile));
    }

    @Override
    public void Log(String message, boolean writeToFile, boolean force) {
        logQueue.add(new LogObject((long) logQueue.size()+1,
                this,
                severity,
                message,
                writeToFile,
                force));
    }

    @Override
    public void LogIfTrue(boolean statement) {
        if (statement) logQueue.add(new LogObject((long) logQueue.size()+1,
                this,
                severity,
                String.format("Statement is %s", statement)));
    }

    @Override
    public void LogIfTrue(String message, boolean statement) {
        if (statement) logQueue.add(new LogObject((long) logQueue.size()+1,
                this,
                severity,
                String.format("%s is %s", message, statement)));
    }

    @Override
    public void LogIfTrue(boolean statement, boolean writeToFile, boolean force) {
        if (statement) logQueue.add(new LogObject((long) logQueue.size()+1,
                this,
                severity,
                String.format("Statement is %s", statement),
                writeToFile,
                force));
    }

    @Override
    public void LogIfTrue(String message, boolean statement, boolean writeToFile, boolean force) {
        if (statement) logQueue.add(new LogObject((long) logQueue.size()+1,
                this,
                severity,
                message,
                writeToFile,
                force));
    }

    @Override
    public void LogThread(Thread thread, String message) {
        logQueue.add(new LogThreadObject((long) logQueue.size()+1,
                thread,
                this,
                severity,
                message));
    }

    @Override
    public void LogThread(Thread thread, String message, boolean writeToFile) {
        logQueue.add(new LogThreadObject((long) logQueue.size()+1,
                thread,
                this,
                severity,
                message,
                writeToFile));
    }

    @Override
    public void LogThread(Thread thread, String message, boolean writeToFile, boolean force) {
        logQueue.add(new LogThreadObject((long) logQueue.size()+1,
                thread,
                this,
                severity,
                message,
                writeToFile,
                force));
    }

    @Override
    public void LogException(Exception e, String message) {
        logQueue.add(new LogExceptionObject((long) logQueue.size()+1,
                e,
                this,
                severity,
                message));
    }

    @Override
    public void LogException(Exception e, String message, boolean writeToFile) {
        logQueue.add(new LogExceptionObject((long) logQueue.size()+1,
                e,
                this,
                severity,
                message,
                writeToFile));
    }

    @Override
    public void LogException(Exception e) {
        logQueue.add(new LogExceptionObject((long) logQueue.size()+1,
                e,
                this,
                severity));
    }

    @Override
    public void LogException(Exception e, boolean writeToFile) {
        logQueue.add(new LogExceptionObject((long) logQueue.size()+1,
                e,
                this,
                severity,
                writeToFile));
    }

    @Override
    public void LogThreadException(Thread thread, Exception e) {
        logQueue.add(new LogExceptionObject((long) logQueue.size()+1,
                thread,
                e,
                this,
                severity));
    }

    @Override
    public void LogThreadException(Thread thread, Exception e, boolean writeToFile) {
        logQueue.add(new LogExceptionObject((long) logQueue.size()+1,
                e,
                this,
                severity,
                writeToFile));
    }

    @Override
    public void LogThreadException(Thread thread, Exception e, String message) {
        logQueue.add(new LogExceptionObject((long) logQueue.size()+1,
                thread,
                e,
                this,
                severity,
                message));
    }

    @Override
    public void LogThreadException(Thread thread, Exception e, String message, boolean writeToFile) {
        logQueue.add(new LogExceptionObject((long) logQueue.size()+1,
                thread,
                e,
                this,
                severity,
                message,
                writeToFile));
    }

    @Override
    public void LogSilently(String message) {
        LogObject log = new LogObject((long) logQueue.size()+1,
                this,
                severity,
                message,
                true);
        log.setSilent(true);
        logQueue.add(log);
    }

    @Override
    public void LogExceptionSilently(Exception e, String message) {
        LogObject log = new LogExceptionObject((long) logQueue.size()+1,
                e,
                this,
                severity,
                message,
                true);
        log.setSilent(true);
        logQueue.add(log);
    }
}
