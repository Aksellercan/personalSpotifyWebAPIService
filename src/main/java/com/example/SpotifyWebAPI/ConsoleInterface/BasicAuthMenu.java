package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Connection.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;

import java.util.Scanner;

public class BasicAuthMenu {
    private SpotifySession spotifySession;
    private HTTPConnection httpConnection = new HTTPConnection();
    private Scanner scanner;
    private String playlist_id;
    private HelperFunctions helperFunctions;
    private ProgramOptions programOptions;


    public BasicAuthMenu(SpotifySession spotifySession, Scanner scanner, ProgramOptions programOptions) {
        this.spotifySession = spotifySession;
        this.scanner = scanner;
        this.programOptions = programOptions;
        this.helperFunctions = new HelperFunctions(programOptions,spotifySession, scanner);
    }
    public void Basic_auth_Functions() {
        while (true) {
            helperFunctions.clearScreen();
            System.out.println("Basic Auth Functions");
            System.out.println("1. Get Playlist by ID");
            System.out.println("0. Go Back");
            Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token(httpConnection, spotifySession);
            clientCredentialsToken.post_Access_Request();
            switch (scanner.nextLine()) {
                case "1":
                    while (playlist_id == null || playlist_id.isEmpty()) {
                        System.out.println("Enter Playlist ID:");
                        playlist_id = scanner.nextLine().trim();
                    }
                    System.out.println("API Response: ");
                    Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request(httpConnection, spotifySession);
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
