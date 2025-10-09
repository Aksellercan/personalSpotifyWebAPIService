package com.example.SpotifyWebAPI.System;

import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;

public class TimerThread implements Runnable {
    private final SpotifySession spotifySession = SpotifySession.getInstance();

    @Override
    public void run() {
        CheckValue();
    }

    private void refreshAccessToken() {
        Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token();
        Logger.INFO.Log("Refreshed client credentials token");
        clientCredentialsToken.post_Access_Request();
    }

    private void CheckValue() {
        int i = 0;
        try {
            Logger.DEBUG.Log(Thread.currentThread().getName());
            while (!Thread.interrupted()) {
                if (spotifySession.getAccess_token() != null) {
                    if (i != 0) refreshAccessToken();
                    i++;
                    Logger.INFO.Log("Value checked " + i, false);
                    Thread.sleep(3600000); //1 hour
//                    Thread.sleep(600000); //debug 10 minutes
                }
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            Logger.ERROR.LogException(e, "Thread test exception");
        }
    }
}
