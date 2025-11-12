package com.example.SpotifyWebAPI.Tools.Logger;

public class LogExceptionObject extends Log {
    private Thread thread;
    private Exception exception;

    // Threads & Exceptions
    public LogExceptionObject(long id, Thread thread, Exception exception, Logger severityEnum, String severity) {
        super(id, severityEnum, severity);
        this.thread = thread;
        this.exception = exception;
    }
    public LogExceptionObject(long id, Thread thread, Exception exception, Logger severityEnum, String severity, String message) {
        super(id, severityEnum, severity, message);
        this.thread = thread;
        this.exception = exception;
    }
    public LogExceptionObject(long id, Thread thread, Exception exception, Logger severityEnum, String severity, String message, boolean writeToFile) {
        super(id, severityEnum, severity, message, writeToFile);
        this.thread = thread;
        this.exception = exception;
    }

    // Exceptions
    public LogExceptionObject(long id, Exception exception, Logger severityEnum, String severity) {
        super(id, severityEnum, severity);
        this.exception = exception;
    }
    public LogExceptionObject(long id, Exception exception, Logger severityEnum, String severity, String message) {
        super(id, severityEnum, severity, message);
        this.exception = exception;
    }
    public LogExceptionObject(long id, Exception exception, Logger severityEnum, String severity, boolean writeToFile) {
        super(id, severityEnum, severity);
        this.exception = exception;
        this.writeToFile = writeToFile;
    }
    public LogExceptionObject(long id, Exception exception, Logger severityEnum, String severity, String message, boolean writeToFile) {
        super(id, severityEnum, severity, message, writeToFile);
        this.exception = exception;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
