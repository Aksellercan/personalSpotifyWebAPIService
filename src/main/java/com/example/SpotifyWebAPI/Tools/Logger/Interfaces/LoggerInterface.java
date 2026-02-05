package com.example.SpotifyWebAPI.Tools.Logger.Interfaces;

public interface LoggerInterface extends LoggerQueueInterface {
    //Logger methods
    void Log(String message);
    void Log(String message, boolean writeToFile);
    void Log(String message, boolean writeToFile, boolean force);

    //Log if true
    void LogIfTrue(boolean statement);
    void LogIfTrue(String message, boolean statement);
    void LogIfTrue(boolean statement, boolean writeToFile, boolean force);
    void LogIfTrue(String message, boolean statement, boolean writeToFile, boolean force);

    //Log threads
    void LogThread(Thread thread, String message);
    void LogThread(Thread thread, String message, boolean writeToFile);
    void LogThread(Thread thread, String message, boolean writeToFile, boolean force);

    //Log exceptions
    void LogException(Exception e, String message);
    void LogException(Exception e, String message, boolean writeToFile);
    void LogException(Exception e);
    void LogException(Exception e, boolean writeToFile);

    //Log exceptions in threads
    void LogThreadException(Thread thread, Exception e);
    void LogThreadException(Thread thread, Exception e, boolean writeToFile);
    void LogThreadException(Thread thread, Exception e, String message);
    void LogThreadException(Thread thread, Exception e, String message, boolean writeToFile);

    //Log silently
    void LogSilently(String message);
    void LogExceptionSilently(Exception e, String message);
}
