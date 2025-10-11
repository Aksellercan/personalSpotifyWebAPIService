package com.example.SpotifyWebAPI.System;

import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;

import java.util.Random;

public class TimerWorker implements Runnable {
    private final SpotifySession spotifySession = SpotifySession.getInstance();

    @Override
    public void run() {
        try {
            CheckValue();
        } catch (InterruptedException e) {
            Logger.THREAD_ERROR.LogException(e, Thread.currentThread().getName());
        }
    }

    private void refreshAccessToken() {
        if (spotifySession.getRefresh_token() == null) {
            Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token();
            Logger.THREAD_INFO.Log("Refreshed client credentials token");
            clientCredentialsToken.post_Access_Request();
        } else {
            User_Access_Token userAccessToken = new User_Access_Token();
            Logger.THREAD_INFO.Log("Refreshed user access token");
            userAccessToken.refresh_token_with_User_Token();
        }
    }

    private void CheckValue() throws InterruptedException {
        int i = 0;
        int skipCount = 0;
        Random rand = new Random();
        long id = rand.nextLong();
        Thread.currentThread().setName(Thread.currentThread().getName() + "_TimerWorker_" + (id > 0 ? id : -1*id));
        Logger.THREAD_INFO.LogThread(Thread.currentThread(),"Timer Thread name: " + Thread.currentThread().getName());
        while (!Thread.interrupted()) {
            if (spotifySession.getAccess_token() != null) {
                if (i != 0) refreshAccessToken();
                i++;
                Logger.THREAD_INFO.LogThread(Thread.currentThread(), "Access Token checked " + i + (i > 1 ? " times" : " time"), false, true);
                Thread.sleep(3600000); //1 hour
//                Thread.sleep(300000); //debug 5 minutes
                continue;
            }
            skipCount++;
            Logger.THREAD_DEBUG.LogThread(Thread.currentThread(),"Skipped " + skipCount + (skipCount > 1 ? " times" : " time"), false, false);
            Thread.sleep(120000); // 2 minutes
        }
    }
}
