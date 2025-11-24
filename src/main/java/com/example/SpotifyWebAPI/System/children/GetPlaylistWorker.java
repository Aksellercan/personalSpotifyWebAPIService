package com.example.SpotifyWebAPI.System.children;

import com.example.SpotifyWebAPI.Objects.Spotify.Playlist;
import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.example.SpotifyWebAPI.System.HTTPWorker;
import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;

public class GetPlaylistWorker extends HTTPWorker {
    protected class PlaylistObj {
        public volatile Playlist playlist;
    }
    private PlaylistObj playlistObj;

    @Override
    public void run() {
        get_playlist_details();
    }

    public Playlist getPlaylist() {
        return playlistObj.playlist;
    }

    private void get_playlist_details() {
        if (SpotifySession.getInstance().getAccess_token() == null) {
            Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token();
            clientCredentialsToken.post_Access_Request();
        }
        Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();
        clientCredentialsRequest.getPlaylist(SpotifySession.getInstance().getPlaylist_id());
        playlistObj.playlist = clientCredentialsRequest.getPlaylist();
        Logger.THREAD_INFO.LogThread(Thread.currentThread(), "Received Playlist details");
    }

    public void get_track_details() {
        if (SpotifySession.getInstance().getAccess_token() == null) {
            Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token();
            clientCredentialsToken.post_Access_Request();
        }
        Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();
        clientCredentialsRequest.getTrackInformation("4r8AQvzullpWTDpgv70KxD");
    }
}
