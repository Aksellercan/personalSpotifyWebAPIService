package com.example.SpotifyWebAPI.System;

import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;

public class HTTPWorker implements Runnable {
    @Override
    public void run() {
        request_user_access_token();
    }

    private void request_user_access_token() {
        User_Access_Token userAccessToken = new User_Access_Token();
        if (SpotifySession.getInstance().getRefresh_token() != null && SpotifySession.getInstance().getAccess_token() == null)
            userAccessToken.refresh_token_with_User_Token();
        Logger.THREAD_INFO.LogThread(Thread.currentThread(), "Received Token");
    }
}
