package com.example.SpotifyWebAPI.Tools.Logger.Interfaces;

import com.example.SpotifyWebAPI.Tools.Logger.Objects.LogObject;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface LoggerQueueInterface {
    //Queue
    ConcurrentLinkedQueue<LogObject> logQueue = new ConcurrentLinkedQueue<>();
}
