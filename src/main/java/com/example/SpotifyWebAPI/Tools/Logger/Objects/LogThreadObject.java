package com.example.SpotifyWebAPI.Tools.Logger.Objects;

import com.example.SpotifyWebAPI.Tools.Logger.Logger;

public class LogThreadObject extends LogObject {
    private Thread thread;

    // Threads
    public LogThreadObject(long id, Thread thread, Logger severityEnum, String severity, String message) {
        super(id, severityEnum, severity, message);
        this.thread = thread;
        this.severity = setThreadSeverity();
    }
    public LogThreadObject(long id, Thread thread, Logger severityEnum, String severity, String message, boolean writeToFile) {
        super(id, severityEnum, severity, message, writeToFile);
        this.thread = thread;
        this.severity = setThreadSeverity();
    }
    public LogThreadObject(long id, Thread thread, Logger severityEnum, String severity, String message, boolean writeToFile, boolean force) {
        super(id, severityEnum, severity, message, writeToFile, force);
        this.thread = thread;
        this.severity = setThreadSeverity();
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    private boolean isThreadSeverity() {
        return (severityEnum.equals(Logger.THREAD_CRITICAL) || severityEnum.equals(Logger.THREAD_INFO) || severityEnum.equals(Logger.THREAD_DEBUG) || severityEnum.equals(Logger.THREAD_ERROR) || severityEnum.equals(Logger.THREAD_WARN));
    }

    public String setThreadSeverity() {
        if (isThreadSeverity()) {
            String threadName;
            if (this.getThread() != null)
                threadName = this.getThread().getName();
            else
                threadName = "lost-thread";
            return String.format(" [ %s%s", threadName, this.getSeverity().substring(this.getSeverity().indexOf(':')));
        }
        return this.getSeverity();
    }
}
