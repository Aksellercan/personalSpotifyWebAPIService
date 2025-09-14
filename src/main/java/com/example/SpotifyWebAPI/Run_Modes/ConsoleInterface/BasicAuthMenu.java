package com.example.SpotifyWebAPI.Run_Modes.ConsoleInterface;

import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Tools.Logger.ConsoleColours;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;

/**
 * CLI Menu for Client Access Token requests
 */
public class BasicAuthMenu extends HelperFunctions{
    /**
     * Menu entries
     */
    public void Basic_auth_Functions() {
        Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();
        while (true) {
            clearScreen();
            System.out.println("Basic Auth Functions");
            System.out.println("1. Get Playlist by ID");
            System.out.println("2. Get Track by ID");
            System.out.println("0. Go Back");
            Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token();
            clientCredentialsToken.post_Access_Request();
            switch (scanner.nextLine()) {
                case "1":
                    while (checkIfNullOrEmpty(spotifySession.getPlaylist_id())) {
                        System.out.println("Enter Playlist ID:");
                        spotifySession.setPlaylist_id(scanner.nextLine().trim());
                    }
                    System.out.println("API Response: ");
                    clientCredentialsRequest.getPlaylist(spotifySession.getPlaylist_id());
                    break;
                case "2":
                    String track_uri = "";
                    while (checkIfNullOrEmpty(track_uri)) {
                        System.out.println("Enter Track ID:");
                        track_uri = scanner.nextLine().trim();
                    }
                    System.out.println("API Response: ");
                    clientCredentialsRequest.getTrackInformation(track_uri);
                    break;
                case "0":
                    return;
                default:
                    System.out.println(ConsoleColours.RED + "Invalid input" + ConsoleColours.RESET);
                    break;
            }
        }
    }
}
