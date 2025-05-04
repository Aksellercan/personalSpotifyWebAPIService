package com.example.SpotifyWebAPI.Run_Modes;

import com.example.SpotifyWebAPI.Connection.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Connection.User_Access_Token;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.ConfigMaps;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
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
    private String playlist_id;
    private final HTTPConnection httpConnection = new HTTPConnection();
    private final Scanner scanner = new Scanner(System.in);
    private final FileUtil fileUtil;
    private final ConfigMaps configMaps;
    private boolean auto_mode = false;
    private boolean output_debug = false;

    public CLI_Interface(FileUtil fileUtil, ConfigMaps configMaps) {
        this.fileUtil = fileUtil;
        this.configMaps = configMaps;
    }

    //Set these to skip questions - Old
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

    private void setConfigs() {
        client_id = configMaps.getClient_id();
        client_secret = configMaps.getClient_secret();
        redirect_uri = configMaps.getRedirect_uri();
        refresh_token = configMaps.getRefresh_token();
        playlist_id = configMaps.getPlaylist_id();
        output_debug = configMaps.isOutputDebug();
        httpConnection.setDebugOutput(output_debug);
        auto_mode = configMaps.isAutoMode();
    }

    public void initSession() {
        setConfigs();
        while ((client_id == null || client_id.isEmpty()) || (client_secret == null || client_secret.isEmpty())) {
            System.out.println("Spotify Web API needed details to setup Session: ");
            if (client_id == null || client_id.isEmpty()) {
                System.out.println("Question 1/2: Enter Spotify client_id");
                client_id = scanner.nextLine().trim();
                continue;
            }
            if (client_secret == null || client_secret.isEmpty()) {
                System.out.println("Question 2/2: Enter Spotify client_secret");
                client_secret = scanner.nextLine().trim();
            }
        }
        System.out.println("Done");
        spotify_session = new SpotifySession(client_id, client_secret);
        userInterface();
    }

    private void userInterface() {
        while (true) {
            clearScreen();
            System.out.println("Spotify Web API CLI Interface");
            System.out.println("1. Basic auth Functions");
            System.out.println("2. Oauth2 Functions");
            System.out.println("3. Set Http Debug Output" + (httpConnection.getDebugOutput() ? " - Debug Output Enabled" : ""));
            System.out.println("4. Set Auto Mode" + (auto_mode ? " - Auto Mode Enabled, Program won't launch to CLI on next run" : ""));
            System.out.println("5. Save Config");
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
                case "4":
                    System.out.println("Set Auto Mode:\nCurrent State is " + auto_mode +
                            "\nPress y to enable Auto Mode\nPress n to disable Auto Mode");
                    if (scanner.nextLine().equals("y")) {
                        auto_mode = true;
                        Logger.INFO.Log("Auto Mode set to true");
                        setupAutoMode();
                    } else {
                        auto_mode = false;
                        System.out.println("Auto Mode set to false");
                    }
                    break;
                case "5":
                    System.out.println("Saving Config...");
                    saveConfig();
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

    private void setupAutoMode() {
        while (((refresh_token == null || refresh_token.isEmpty()) || (redirect_uri == null || redirect_uri.isEmpty()))
                || ((client_id == null || client_id.isEmpty()) ||
                (client_secret == null || client_secret.isEmpty())) || (playlist_id == null || playlist_id.isEmpty())) {
            if (client_id == null || client_id.isEmpty()) {
                System.out.println("Enter Spotify client_id:");
                client_id = scanner.nextLine().trim();
                continue;
            }
            if (client_secret == null || client_secret.isEmpty()) {
                System.out.println("Enter Spotify client_secret:");
                client_secret = scanner.nextLine().trim();
                continue;
            }
            if (refresh_token == null || refresh_token.isEmpty()) {
                System.out.println("Enter Refresh Token:");
                refresh_token = scanner.nextLine().trim();
                continue;
            }
            if (redirect_uri == null || redirect_uri.isEmpty()) {
                System.out.println("Enter Redirect URI:");
                redirect_uri = scanner.nextLine().trim();
                continue;
            }
            if (playlist_id == null || playlist_id.isEmpty()) {
                System.out.println("Enter Playlist ID:");
                playlist_id = scanner.nextLine().trim();
                continue;
            }
        }
        spotify_session.setRefresh_token(refresh_token);
        spotify_session.setRedirect_uri(redirect_uri);
        spotify_session.setClient_id(client_id);
        spotify_session.setClient_secret(client_secret);
        saveConfig();
        System.out.println("Done");
    }

    private void saveConfig() {
        fileUtil.writeConfig("client_id", client_id, "client_secret", client_secret, "redirect_uri",
                redirect_uri, "refresh_token", refresh_token, "playlist_id", playlist_id, "auto_mode",
                Boolean.toString(auto_mode), "output_debug", Boolean.toString(httpConnection.getDebugOutput()));
        Logger.INFO.Log("Saved Config successfully!");
    }

    private void Basic_auth_Functions() {
        clearScreen();
        System.out.println("1. Get Playlist by ID");
        System.out.println("0. Go Back");
        Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token(httpConnection, spotify_session);
        clientCredentialsToken.post_Access_Request();
        switch (scanner.nextLine()) {
            case "1":
                while (playlist_id == null || playlist_id.isEmpty()) {
                    System.out.println("Enter Playlist ID:");
                    playlist_id = scanner.nextLine().trim();
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

    private void Oauth2_Functions() {
        clearScreen();
        System.out.println("1. Get Refresh Token to access User Requests");
        System.out.println("2. Refresh access token using refresh token");
        System.out.println("3. Edit Details of a Playlist");
        System.out.println("0. Go Back");
        User_Access_Token userAccessToken = new User_Access_Token(httpConnection, spotify_session);
        if (httpConnection.getDebugOutput()) userAccessToken.printData();
        switch (scanner.nextLine()) {
            case "1":
                while ((code == null || code.isEmpty()) || (redirect_uri == null || redirect_uri.isEmpty())) {
                    if (code == null || code.isEmpty()) {
                        System.out.println("Enter Spotify code:");
                        code = scanner.nextLine().trim();
                        spotify_session.setCode(code);
                        continue;
                    }
                    if (redirect_uri == null || redirect_uri.isEmpty()) {
                        System.out.println("Enter Redirect URI:");
                        redirect_uri = scanner.nextLine().trim();
                        spotify_session.setRedirect_uri(redirect_uri);
                    }
                }
                userAccessToken.get_Refresh_Token();
                break;
            case "2":
                while ((refresh_token == null || refresh_token.isEmpty()) || (redirect_uri == null || redirect_uri.isEmpty())) {
                    if ((refresh_token == null || refresh_token.isEmpty())) {
                        System.out.println("Enter Refresh Token:");
                        refresh_token = scanner.nextLine().trim();
                        spotify_session.setRefresh_token(refresh_token);
                        continue;
                    }
                    if ((redirect_uri == null || redirect_uri.isEmpty())) {
                        System.out.println("Enter Redirect URI:");
                        redirect_uri = scanner.nextLine().trim();
                        spotify_session.setRedirect_uri(redirect_uri);
                    }
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

    private void setPlaylistDetails() {
        User_Request userRequest = new User_Request(httpConnection, spotify_session);
        while (playlist_id == null || playlist_id.isEmpty()) {
            System.out.println("Enter Playlist ID:");
            playlist_id = scanner.nextLine().trim();
        }
        System.out.println("Edit details of a Playlist");
        System.out.println("Enter new Description:");
        String newDescription = scanner.nextLine();
        System.out.println("New Description: " + newDescription);
        userRequest.setPlaylistDescription(playlist_id, newDescription);
    }

    private void clearScreen() {
        final String getOS = System.getProperty("os.name").toLowerCase();
        try {
            if (getOS.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            Logger.ERROR.Log("Error clearing screen, Operating System: " + getOS + ", " + e.getMessage());
        }
    }
}