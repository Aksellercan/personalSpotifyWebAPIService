package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import java.util.Scanner;

public class CLI_Interface {
    private String client_id;
    private String client_secret;
    private String playlist_id;
    private String code;
    private String redirect_url;

    //setters
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public void userInterface() {
        Scanner scanner = new Scanner(System.in);
        if (client_id == null || client_secret == null || playlist_id == null) {
            System.out.println("Spotify Web API needed details: ");
            System.out.println("Question 1/3: Enter Spotify client_id");
            client_id = scanner.nextLine();
            System.out.println("Question 2/3: Enter Spotify client_secret");
            client_secret = scanner.nextLine();
            System.out.println("Question 3/3: Enter Spotify playlist_id");
            playlist_id = scanner.nextLine();
            System.out.println("Done");
        }

        Authentication httpConnect = new Authentication();
        httpConnect.post_Access_Request(client_id, client_secret);

        if (httpConnect.getSuccess()) {
            while (true) {
                boolean isDebugEnabled = httpConnect.getDebugOutput();
                System.out.println("1. Get Playlist by ID");
                System.out.println("2. Set Playlist Description");
                System.out.println("3. Set Http Debug Output" + (isDebugEnabled ? " - Debug Output Enabled" : ""));
                System.out.println("0. Exit");
                switch (scanner.nextLine()) {
                    case "1":
                        System.out.println("API Response: ");
                        System.out.println("Get Playlist Details for " + playlist_id + ":");
                        Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request(httpConnect);
                        clientCredentialsRequest.getPlaylist(playlist_id);
                        break;
                    case "2":
                        //Massive refactor needed here
                        System.out.println("API Response: ");
                        System.out.println("Set Playlist Description: \n");
                        Authentication httpConnectRefresh = new Authentication();
                        httpConnectRefresh.refresh_with_User_Token(client_id, client_secret, code, redirect_url);
                        User_Request userRequest = new User_Request(httpConnectRefresh);
                        userRequest.setToken(httpConnectRefresh.getToken());
                        System.out.println("Get User Details for " + playlist_id + ":" + "token to be sent to the details: " +httpConnectRefresh.getToken());
                        if (httpConnectRefresh.getSuccess()) {
                            userRequest.setPlaylistDescription(playlist_id, "Testing Java API");
                            System.out.println("[ SUCCESS ]");
                            break;
                        }
                        System.out.println("[ FAIL ]");
                        break;
                    case "3":
                        System.out.println("Set Http Debug Output:\nCurrent State is " + isDebugEnabled + "\nPress y to enable debug output\nPress n to disable debug output");
                        if (scanner.nextLine().equals("y")) {
                            httpConnect.setDebugOutput(true);
                            System.out.println("Http Debug Output set to true");
                        } else {
                            httpConnect.setDebugOutput(false);
                            System.out.println("Http Debug Output set to false");
                        }
                        break;
                    case "0":
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
        }
    }
}