package com.example.SpotifyWebAPI.Run_Modes;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.Files.JSONParser;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Automation to change description of Playlist to the current size of playlist with limit of 120
 */
public class AutoMode {
    /**
     * Executes automation methods in order and catches Exceptions
     */
    public void runFunctions() {
        // Initialize Spotify session
        Logger.INFO.Log("Starting AutoMode.runFunctions()");
        SpotifySession spotify_session = SpotifySession.getInstance();
        try {
            // Perform authentication
            User_Access_Token userAccessToken = new User_Access_Token();
            userAccessToken.refresh_token_with_User_Token();
            Logger.INFO.Log("Received the access token");
            User_Request userRequest = new User_Request();
            //Get the size of the playlist
            Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();
            clientCredentialsRequest.getPlaylist(spotify_session.getPlaylist_id());
            int playlistSize = clientCredentialsRequest.getplaylistSize();
            String stringDescCount = readString(clientCredentialsRequest.getplaylistDescription());
            int readDescCount;
            Pattern pattern= Pattern.compile("[a-zA-Z/]");
            Matcher matcher = pattern.matcher(stringDescCount);
            if (matcher.find()) {
                Logger.INFO.Log("Playlist size: " + playlistSize);
                // Set the playlist description
                userRequest.setPlaylistDetails(spotify_session.getPlaylist_id(), clientCredentialsRequest.getplaylistName(),playlistSize + "/120",true,false);
                Logger.INFO.Log("Completed the automated run");
                return;
            } else {
                readDescCount = Integer.parseInt(stringDescCount);
            }
            if (playlistSize == 120) {
                String prevPlaylistName = clientCredentialsRequest.getplaylistName();
                int nextNumber = nextNumber(prevPlaylistName);
                userRequest.createPlaylist(spotify_session.getUser_id(), "Favorites " + nextNumber, "0/120");
                Logger.INFO.Log("New playlist created with playlist_id: " + spotify_session.getPlaylist_id() + " and Name: " + "Favorites " + nextNumber);
                ProgramOptions.setAutoMode(false);
                JSONParser.MapAndWriteConfig();
                return;
            } else if (playlistSize == readDescCount) {
                Logger.INFO.Log("Playlist size is already set to " + playlistSize);
                Logger.INFO.Log("Completed the automated run, no changes made");
                return;
            }
            Logger.INFO.Log("Playlist size: " + playlistSize);
            // Set the playlist description
            userRequest.setPlaylistDetails(spotify_session.getPlaylist_id(), clientCredentialsRequest.getplaylistName(),playlistSize + "/120",true,false);
            Logger.INFO.Log("Completed the automated run");
        } catch (Exception e) {
            Logger.ERROR.LogException(e,"Auto Mode");
        }
    }

    /**
     * Finds what number should the next playlist be
     * @param str   Name of current playlist name
     * @return  Number for the next playlist
     */
    private int nextNumber(String str) {
        int number = 0;
        String[] splitLine;
        if (str.isEmpty()) {
            return 0;
        }
        splitLine = str.split(" ");
        if (splitLine.length == 2) {
            number = Integer.parseInt(splitLine[1]);
        }
        number++;
        Logger.DEBUG.Log("Next number: " + number);
        return number;
    }

    /**
     * Reads the string till '#'. Which is encoded string of '/'
     * @param str   Playlist description
     * @return  Read number from description as string
     */
    private String readString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c == '&') && (str.charAt(i+1) == '#')) { // assuming '/' is encoded to '&#x2F;'
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}