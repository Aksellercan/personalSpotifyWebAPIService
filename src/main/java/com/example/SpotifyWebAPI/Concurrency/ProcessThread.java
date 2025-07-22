package com.example.SpotifyWebAPI.Concurrency;

import com.example.SpotifyWebAPI.HTTP.HTTPServer;
import com.example.SpotifyWebAPI.Tools.Logger;

import java.net.Socket;

public class ProcessThread implements Runnable {
    HTTPServer httpServer = new HTTPServer(8080, 10);

    @Override
    public void run() {
        Thread thread = new Thread(this::StartServerTask);
        thread.start();
        Logger.DEBUG.Log("Current Thread is " + Thread.currentThread());
    }

    public Socket getSocket() {
        return httpServer.getSocket();
    }

    public void StartServerTask() {
        httpServer.StartServer();
        Logger.DEBUG.Log("HTTP Server Started");
        Logger.DEBUG.Log("Current Thread is " + Thread.currentThread());
    }

//    public void StopServerTask() {
//        try {
//            httpServer.StopServer();
//        } catch (InterruptedException e) {
//            Logger.ERROR.LogException(e, "Exception in StopServerTask");
//        }
//    }
}
