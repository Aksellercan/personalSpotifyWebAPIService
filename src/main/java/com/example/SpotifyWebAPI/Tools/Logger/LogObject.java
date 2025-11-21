package com.example.SpotifyWebAPI.Tools.Logger;

public class LogObject {
    protected long id;
    protected Logger severityEnum;
    protected String severity;
    protected String message;
    protected boolean writeToFile = true;
    protected boolean force = false;
    protected boolean silent = false;
    protected boolean logged = false;

    // Basic
    public LogObject(long id, Logger severityEnum, String severity) {
        this.id = id;
        this.severityEnum = severityEnum;
        this.severity = severity;
    }
    public LogObject(long id, Logger severityEnum, String severity, String message) {
        this.id = id;
        this.severityEnum = severityEnum;
        this.severity = severity;
        this.message = message;
    }
    public LogObject(long id, Logger severityEnum, String severity, String message, boolean writeToFile) {
        this.id = id;
        this.severityEnum = severityEnum;
        this.severity = severity;
        this.message = message;
        this.writeToFile = writeToFile;
    }
    public LogObject(long id, Logger severityEnum, String severity, String message, boolean writeToFile, boolean force) {
        this.id = id;
        this.severityEnum = severityEnum;
        this.severity = severity;
        this.message = message;
        this.writeToFile = writeToFile;
        this.force = force;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public Logger getSeverityEnum() {
        return severityEnum;
    }

    public void setSeverityEnum(Logger severityEnum) {
        this.severityEnum = severityEnum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isWriteToFile() {
        return writeToFile;
    }

    public void setWriteToFile(boolean writeToFile) {
        this.writeToFile = writeToFile;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
