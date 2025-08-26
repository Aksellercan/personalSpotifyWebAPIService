package com.example.SpotifyWebAPI.Run_Modes;

import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import java.util.Scanner;

/**
 * @deprecated
 * Old CLI Interface, won't be used
 */
public class CLI_Interface {
    private SpotifySession spotify_session = SpotifySession.getInstance();
    private String user_id;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String refresh_token;
    private String code;
    private String playlist_id;
    private final Scanner scanner = new Scanner(System.in);
    private boolean auto_mode = false;
    private boolean changesSaved = true;

    private void setConfigs() {
        playlist_id = spotify_session.getPlaylist_id();
        user_id = spotify_session.getUser_id();
        client_id = spotify_session.getClient_id();
        client_secret = spotify_session.getClient_secret();
        redirect_uri = spotify_session.getRedirect_uri();
        refresh_token = spotify_session.getRefresh_token();
    }

    public void initSession() {
        setConfigs();
        while ((client_id == null || client_id.isEmpty()) || (client_secret == null || client_secret.isEmpty())) {
            changesSaved = false;
            System.out.println("Spotify Web API needed details to setup Session: ");
            if (client_id == null || client_id.isEmpty()) {
                System.out.println("Question 1/2: Enter Spotify client_id");
                client_id = scanner.nextLine().trim();
                spotify_session.setClient_id(client_id);
                continue;
            }
            if (client_secret == null || client_secret.isEmpty()) {
                System.out.println("Question 2/2: Enter Spotify client_secret");
                client_secret = scanner.nextLine().trim();
                spotify_session.setClient_secret(client_secret);
            }
            if ((client_id != null && !client_id.isEmpty()) && (client_secret != null && !client_secret.isEmpty())) {
                System.out.println("Done");
                break;
            }
        }
        userInterface();
    }

    private void userInterface() {
        while (true) {
            clearScreen();
            System.out.println("Spotify Web API CLI Interface [ DEPRECATED ]");
            System.out.println("1. Basic auth Functions");
            System.out.println("2. Oauth2 Functions");
            System.out.println("3. Set Http Debug Output" + (Logger.getDebugOutput() ? " - Debug Output Enabled" : ""));
            System.out.println("4. Set Auto Mode" + (auto_mode ? " - Auto Mode Enabled, Program won't launch to CLI on next run" : ""));
            System.out.println("5. Save Config");
            System.out.println("0. Exit the program" + (changesSaved ? "" : " - Changes not saved"));
            switch (scanner.nextLine()) {
                case "1":
                    Basic_auth_Functions();
                    break;
                case "2":
                    Oauth2_Functions();
                    break;
                case "3":
                    System.out.println("Set Http Debug Output:\nCurrent State is " + Logger.getDebugOutput() +
                            "\nPress y to enable debug output\nPress n to disable debug output");
                    if (scanner.nextLine().equals("y")) {
                        if (!Logger.getDebugOutput()) {
                            changesSaved = false;
                        }
                        Logger.setDebugOutput(true);
                        System.out.println("Http Debug Output set to true");
                    } else {
                        Logger.setDebugOutput(false);
                        System.out.println("Http Debug Output set to false");
                        changesSaved = false;
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
                (client_secret == null || client_secret.isEmpty())) || (playlist_id == null || playlist_id.isEmpty()) || (user_id == null || user_id.isEmpty())) {
            changesSaved = false;
            if (user_id == null || user_id.isEmpty()) {
                System.out.println("Enter User ID:");
                user_id = scanner.nextLine().trim();
                continue;
            }
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
            }
        }
        spotify_session.setUser_id(user_id);
        spotify_session.setRefresh_token(refresh_token);
        spotify_session.setRedirect_uri(redirect_uri);
        spotify_session.setClient_id(client_id);
        spotify_session.setClient_secret(client_secret);
        saveConfig();
        System.out.println("Done");
    }

    private void saveConfig() {
        FileUtil.writeConfig(
                "client_id", client_id,
                "client_secret", client_secret,
                "redirect_uri", redirect_uri,
                "refresh_token", refresh_token,
                "playlist_id", playlist_id,
                "auto_mode", Boolean.toString(auto_mode),
                "output_debug", Boolean.toString(Logger.getDebugOutput()),
                "user_id",user_id
        );
        changesSaved = true;
    }

    private void Basic_auth_Functions() {
        clearScreen();
        System.out.println("1. Get Playlist by ID");
        System.out.println("0. Go Back");
        Client_Credentials_Token clientCredentialsToken = new Client_Credentials_Token();
        clientCredentialsToken.post_Access_Request();
        switch (scanner.nextLine()) {
            case "1":
                while (playlist_id == null || playlist_id.isEmpty()) {
                    System.out.println("Enter Playlist ID:");
                    playlist_id = scanner.nextLine().trim();
                }
                System.out.println("API Response: ");
                Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();
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
        System.out.println("4. Get Playlist items");
        System.out.println("5. add song to Playlist");
        System.out.println("6. Create Playlist");
        System.out.println("0. Go Back");
        User_Access_Token userAccessToken = new User_Access_Token();
        User_Request userRequest = new User_Request();
        if (Logger.getDebugOutput()) userAccessToken.printData();
        switch (scanner.nextLine()) {
            case "1":
                code= null;
                while ((code == null || code.isEmpty()) || (redirect_uri == null || redirect_uri.isEmpty())) {
                    changesSaved = false;
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
                System.out.println("Save the refresh token to config?");
                if (scanner.nextLine().equals("y")) {
                    refresh_token = spotify_session.getRefresh_token();
                    saveConfig();
                    Logger.INFO.Log("Saved Config successfully!");
                }
                break;
            case "2":
                while ((refresh_token == null || refresh_token.isEmpty()) || (redirect_uri == null || redirect_uri.isEmpty())) {
                    changesSaved = false;
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
                setPlaylistDetails(userRequest);
                break;
            case "4":
                getPlaylistItems(userRequest);
                break;
            case "5":
                System.out.println("Add to new playlist? (y/n)");
                String chosenPlaylist = null;
                int position;
                if (scanner.nextLine().equals("y")) {
                    while (chosenPlaylist == null || chosenPlaylist.isEmpty()) {
                        System.out.println("Enter Playlist ID:");
                        chosenPlaylist = scanner.nextLine().trim();
                    }
                    System.out.println("Adding to playlist: " + chosenPlaylist);
                    position = setPosition();
                    userRequest.addPlaylistItems(chosenPlaylist,position,addTrackUri(), false);
                    break;
                }
                System.out.println("Adding to playlist: " + playlist_id);
                position = setPosition();
                userRequest.addPlaylistItems(playlist_id,position,addTrackUri(), false);
                break;
            case "6":
                createPlaylistDetails(userRequest);
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid input. Please try again.");
                break;
        }
    }

    private void getPlaylistItems(User_Request userRequest) {
        int offset = 0;
        int limit = 10;
        while (playlist_id == null || playlist_id.isEmpty()) {
            System.out.println("Enter Playlist ID:");
            playlist_id = scanner.nextLine().trim();
            changesSaved = false;
        }
        System.out.println("Change playlist ID? (y/n)");
        if (scanner.nextLine().equals("y")) {
            System.out.println("Enter new Playlist ID:");
            playlist_id = scanner.nextLine();
            changesSaved = false;
        }
        System.out.println("Enter offset (default 0):");
        String offsetInput = scanner.nextLine().trim();
        if (!offsetInput.isEmpty()) {
            offset = Integer.parseInt(offsetInput);
        }
        System.out.println("Enter limit (default 10):");
        String limitInput = scanner.nextLine().trim();
        if (!limitInput.isEmpty()) {
            limit = Integer.parseInt(limitInput);
        }
        userRequest.getPlaylistItems(playlist_id,offset, limit);
    }

    private int setPosition() {
        int position;
        System.out.println("Enter Position to insert the track:");
        position = scanner.nextInt();
        return position;
    }

    private String addTrackUri() {
        String track_uri = null;
        while (track_uri == null || track_uri.isEmpty()) {
            System.out.println("Enter Track URI:");
            track_uri = scanner.nextLine().trim();
        }
        System.out.println("Adding track: " + track_uri);
        return track_uri;
    }

    private void createPlaylistDetails(User_Request userRequest) {
        String createName = null;
        String createDescription = null;
        while ((user_id == null || user_id.isEmpty()) || (createName == null || createName.isEmpty()) ||
                (createDescription == null || createDescription.isEmpty())) {
            if (user_id == null || user_id.isEmpty()) {
                System.out.println("Enter User ID:");
                user_id = scanner.nextLine().trim();
                spotify_session.setUser_id(user_id);
                changesSaved = false;
                continue;
            }
            if (createName == null || createName.isEmpty()) {
                System.out.println("Enter Playlist Name:");
                createName = scanner.nextLine().trim();
                continue;
            }
            if (createDescription == null || createDescription.isEmpty()) {
                System.out.println("Enter Playlist Description:");
                createDescription = scanner.nextLine().trim();
            }
        }
        userRequest.createPlaylist(user_id,createName,createDescription);
    }

    private void setPlaylistDetails(User_Request userRequest) {
        while (playlist_id == null || playlist_id.isEmpty()) {
            System.out.println("Enter Playlist ID:");
            playlist_id = scanner.nextLine().trim();
            changesSaved = false;
        }
        String newName = null;
        String newDescription = null;
        boolean isPublic = false;
        boolean isCollaborative = false;
        while ((newName == null || newName.isEmpty()) || (newDescription == null || newDescription.isEmpty())) {
            if ((newName == null || newName.isEmpty())) {
                System.out.println("Enter new Name:");
                newName = scanner.nextLine();
                continue;
            }
            if ((newDescription == null || newDescription.isEmpty())) {
                System.out.println("Enter new Description:");
                newDescription = scanner.nextLine();
            }
        }
        System.out.println("Is the playlist public? (y/n)");
        if (scanner.nextLine().equals("y")) {
            isPublic = true;
        }
        System.out.println("Is the playlist collaborative? (y/n)");
        if (scanner.nextLine().equals("y")) {
            isCollaborative = true;
        }
        System.out.println("New Name: " + newName);
        System.out.println("New Description: " + newDescription);
        System.out.println("Is the playlist public? " + isPublic);
        System.out.println("Is the playlist collaborative? " + isCollaborative);
        userRequest.setPlaylistDetails(playlist_id, newName, newDescription, isPublic, isCollaborative);
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
            Logger.ERROR.LogSilently("Error clearing screen, Operating System: " + getOS + ", " + e.getMessage());
        }
    }
}