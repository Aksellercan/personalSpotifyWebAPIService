package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.Connection.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Connection.User_Access_Token;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import java.util.Scanner;

public class CLI_Interface {
    private SpotifySession spotify_session;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String refresh_token;
    private String code;
    private boolean hasAccessToken = false;
    private String playlist_id;
    private HTTPConnection httpConnection = new HTTPConnection();
    private final Scanner scanner = new Scanner(System.in);

    //Set these to skip questions
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    public void setredirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public void initSession() {
        while ((client_id == null || client_id.isEmpty()) || (client_secret == null || client_secret.isEmpty())) {
            System.out.println("Spotify Web API needed details to setup Session: ");
            if (client_id == null || client_id.isEmpty()) {
                System.out.println("Question 1/2: Enter Spotify client_id");
                client_id = scanner.nextLine();
            }
            if (client_secret == null || client_secret.isEmpty()) {
                System.out.println("Question 2/2: Enter Spotify client_secret");
                client_secret = scanner.nextLine();
                System.out.println("Done");
            }
        }
        spotify_session = new SpotifySession(client_id, client_secret);
        userInterface();
    }

    public void userInterface() {
        while (true) {
            System.out.println("1. Basic auth Functions");
            System.out.println("2. Oauth2 Functions");
            System.out.println("3. Set Http Debug Output" + (httpConnection.getDebugOutput() ? " - Debug Output Enabled" : ""));
            System.out.println("0. Exit the program");
            switch (scanner.nextLine()) {
                case "1":
                    Basic_auth_Functions();
                    break;
                case "2":
                    Oauth2_Functions();
                    break;
                case "3":
                    System.out.println("Set Http Debug Output:\nCurrent State is " + httpConnection.getDebugOutput() +
                            "\nPress y to enable debug output\nPress n to disable debug output");
                    if (scanner.nextLine().equals("y")) {
                        httpConnection.setDebugOutput(true);
                        System.out.println("Http Debug Output set to true");
                    } else {
                        httpConnection.setDebugOutput(false);
                        System.out.println("Http Debug Output set to false");
                    }
                    break;
                case "0":
                    System.out.println("Exiting the program...");
                    return;
                default:
                    System.out.println("Invalid input. Please try again.");
                    break;
            }
        }
    }

    public void Basic_auth_Functions() {
        System.out.println("1. Get Playlist by ID");
        System.out.println("0. Go Back");
        if (!hasAccessToken) {
            Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token(httpConnection, spotify_session);
            clientCredentialsToken.post_Access_Request();
        }
        switch (scanner.nextLine()) {
            case "1":
                if (playlist_id == null) {
                    System.out.println("Enter Playlist ID:");
                    playlist_id = scanner.nextLine();
                }
                System.out.println("API Response: ");
                Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request(httpConnection, spotify_session);
                clientCredentialsRequest.getPlaylist(playlist_id);
                return;
            case "0":
                return;
            default:
                System.out.println("Invalid input. Please try again.");
                break;
        }
    }

    public void Oauth2_Functions() {
        System.out.println("1. Get Refresh Token to access User Requests");
        System.out.println("2. Refresh access token using refresh token");
        System.out.println("3. Edit Details of a Playlist");
        System.out.println("0. Go Back");
        User_Access_Token userAccessToken = new User_Access_Token(httpConnection, spotify_session);
        spotify_session.setRefresh_token(refresh_token);
        spotify_session.setRedirect_uri(redirect_uri);
        userAccessToken.printData();
        switch (scanner.nextLine()) {
            case "1":
                if (code == null) {
                    System.out.println("Enter Spotify code:");
                    code = scanner.nextLine();
                }
                if (redirect_uri == null) {
                    System.out.println("Enter Redirect URI:");
                    redirect_uri = scanner.nextLine();
                }
                userAccessToken.get_Refresh_Token();
                break;
            case "2":
                if (refresh_token == null || redirect_uri == null) {
                    System.out.println("Enter Refresh Token:");
                    refresh_token = scanner.nextLine();
                    System.out.println("Enter Redirect URI:");
                    redirect_uri = scanner.nextLine();
                }
                userAccessToken.refresh_token_with_User_Token();
                break;
            case "3":
                setPlaylistDetails();
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid input. Please try again.");
                break;
        }
    }

    public void setPlaylistDetails() {
        User_Request userRequest = new User_Request(httpConnection, spotify_session);
        System.out.println("Edit details of a Playlist");
        System.out.println("Enter new Description:");
        String newDescription = scanner.nextLine();
        System.out.println("New Description: " + newDescription);
        userRequest.setPlaylistDescription(playlist_id, newDescription);
    }
}