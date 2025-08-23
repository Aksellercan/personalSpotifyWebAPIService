package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Tools.ConsoleColours;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;

/**
 * CLI Menu for Client Access Token requests
 */
public class BasicAuthMenu extends HelperFunctions{
    /**
     * Menu entries
     */
    public void Basic_auth_Functions() {
        while (true) {
            clearScreen();
            System.out.println("Basic Auth Functions");
            System.out.println("1. Get Playlist by ID");
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
                    Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();
                    clientCredentialsRequest.getPlaylist(spotifySession.getPlaylist_id());
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
