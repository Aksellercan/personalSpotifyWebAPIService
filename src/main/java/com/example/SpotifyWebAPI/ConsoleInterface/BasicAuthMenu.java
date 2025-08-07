package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import java.util.Scanner;

public class BasicAuthMenu {
    private final Scanner scanner;
    private String playlist_id;
    private final HelperFunctions helperFunctions;

    public BasicAuthMenu(Scanner scanner) {
        this.scanner = scanner;
        this.helperFunctions = new HelperFunctions(scanner);
    }
    public void Basic_auth_Functions() {
        while (true) {
            helperFunctions.clearScreen();
            System.out.println("Basic Auth Functions");
            System.out.println("1. Get Playlist by ID");
            System.out.println("0. Go Back");
            Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token();
            clientCredentialsToken.post_Access_Request();
            switch (scanner.nextLine()) {
                case "1":
                    playlist_id = SpotifySession.getInstance().getPlaylist_id();
                    while (playlist_id == null || playlist_id.isEmpty()) {
                        System.out.println("Enter Playlist ID:");
                        playlist_id = scanner.nextLine().trim();
                    }
                    System.out.println("API Response: ");
                    Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();
                    clientCredentialsRequest.getPlaylist(playlist_id);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid input. Please try again.");
                    break;
            }
        }
    }
}
