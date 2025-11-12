package com.example.SpotifyWebAPI.Tools.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface LoggerQueueInterface {
    //Queue
    ConcurrentLinkedQueue<Log> logQueue = new ConcurrentLinkedQueue<>();
}
