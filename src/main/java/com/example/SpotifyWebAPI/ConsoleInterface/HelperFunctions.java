package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.Configuration;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import java.util.Scanner;

/**
 * Shared Functions used by CLI Menus
 */
public class HelperFunctions {
    protected final SpotifySession spotifySession = SpotifySession.getInstance();
    protected final Scanner scanner = new Scanner(System.in);

    protected HelperFunctions() {}

    /**
     * Checks if the config is read properly. If not logs it to logfile
     */
    protected void checkClientCredentials() {
        while (checkIfNullOrEmpty(spotifySession.getClient_id()) || checkIfNullOrEmpty(spotifySession.getClient_secret())) {
            if (checkIfNullOrEmpty(spotifySession.getClient_id())) {
                System.out.println("Enter Spotify client_id:");
                String client_id = scanner.nextLine().trim();
                spotifySession.setClient_id(client_id);
            }
            if (checkIfNullOrEmpty(spotifySession.getClient_secret())) {
                System.out.println("Enter Spotify client_secret:");
                String client_secret = scanner.nextLine().trim();
                spotifySession.setClient_secret(client_secret);
            }
            ProgramOptions.setChangesSaved(false);
        }
        Logger.INFO.LogSilently("Everything is set up correctly, client_id and client_secret are not null or empty.");
    }

    /**
     * Checks if string is Null or empty
     * @param str   String to check
     * @return  Whether string is Null or empty
     */
    protected boolean checkIfNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Clears terminal screen
     */
    protected void clearScreen() {
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

    /**
     * Asks user to if they want to change playlist. Then asks where should the returned data start from and end where
     * @param userRequest   User Request Object
     */
    protected void getPlaylistItems(User_Request userRequest) {
        int offset = 0;
        int limit = 10;
        String playlist_id = spotifySession.getPlaylist_id();
        while (checkIfNullOrEmpty(playlist_id)) {
            System.out.println("Enter Playlist ID:");
            playlist_id = scanner.nextLine().trim();
            ProgramOptions.setChangesSaved(false);
        }
        System.out.println("Change playlist ID? (y/n)");
        if (scanner.nextLine().equals("y")) {
            System.out.println("Enter new Playlist ID:");
            playlist_id = scanner.nextLine();
            spotifySession.setPlaylist_id(playlist_id);
            ProgramOptions.setChangesSaved(false);
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

    /**
     * Asks user to set position to insert the track
     * @return  position form user input
     */
    protected int setPosition() {
        int position;
        System.out.println("Enter Position to insert the track:");
        position = scanner.nextInt();
        return position;
    }

    /**
     * Asks user to input Track URI
     * @return   track_uri from user input
     */
    protected String addTrackUri() {
        String track_uri = null;
        while (checkIfNullOrEmpty(track_uri)) {
            System.out.println("Enter Track URI:");
            track_uri = scanner.nextLine().trim();
        }
        System.out.println("Adding track: " + track_uri);
        return track_uri;
    }

    /**
     * Walks user through creating a Playlist
     * @param userRequest   User Request Object
     */
    protected void createPlaylistDetails(User_Request userRequest) {
        String createName = null;
        String createDescription = null;
        String user_id = spotifySession.getUser_id();
        while ((checkIfNullOrEmpty(user_id)) || (checkIfNullOrEmpty(createName)) ||
                (checkIfNullOrEmpty(createDescription))) {
            if (checkIfNullOrEmpty(user_id)) {
                System.out.println("Enter User ID:");
                user_id = scanner.nextLine().trim();
                spotifySession.setUser_id(user_id);
                ProgramOptions.setChangesSaved(false);
                continue;
            }
            if (checkIfNullOrEmpty(createName)) {
                System.out.println("Enter Playlist Name:");
                createName = scanner.nextLine().trim();
                continue;
            }
            if (checkIfNullOrEmpty(createDescription)) {
                System.out.println("Enter Playlist Description:");
                createDescription = scanner.nextLine().trim();
            }
        }
        userRequest.createPlaylist(user_id,createName,createDescription);
    }

    /**
     * Walks user through setting playlist details
     * @param userRequest   User Request Object
     */
    protected void setPlaylistDetails(User_Request userRequest) {
        String playlist_id = spotifySession.getPlaylist_id();
        while (checkIfNullOrEmpty(playlist_id)) {
            System.out.println("Enter Playlist ID:");
            playlist_id = scanner.nextLine().trim();
            spotifySession.setPlaylist_id(playlist_id);
            ProgramOptions.setChangesSaved(false);
        }
        String newName = null;
        String newDescription = null;
        boolean isPublic = false;
        boolean isCollaborative = false;
        while ((checkIfNullOrEmpty(newName)) || (checkIfNullOrEmpty(newDescription))) {
            if ((checkIfNullOrEmpty(newName))) {
                System.out.println("Enter new Name:");
                newName = scanner.nextLine();
                continue;
            }
            if ((checkIfNullOrEmpty(newDescription))) {
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

    /**
     * Walks user through setting up Auto Mode
     */
    protected void setupAutoMode() {
        ProgramOptions.setChangesSaved(false);
        while (((checkIfNullOrEmpty(spotifySession.getRefresh_token())) || (checkIfNullOrEmpty(spotifySession.getRedirect_uri())))
                || ((checkIfNullOrEmpty(spotifySession.getClient_id())) ||
                (checkIfNullOrEmpty(spotifySession.getClient_secret()))) || (checkIfNullOrEmpty(spotifySession.getPlaylist_id())) ||
                (checkIfNullOrEmpty(spotifySession.getUser_id()))) {
            if (checkIfNullOrEmpty(spotifySession.getUser_id())) {
                System.out.println("Enter User ID:");
                String user_id = scanner.nextLine().trim();
                spotifySession.setUser_id(user_id);
                continue;
            }
            if (checkIfNullOrEmpty(spotifySession.getClient_id())) {
                System.out.println("Enter Spotify client_id:");
                String client_id = scanner.nextLine().trim();
                spotifySession.setClient_id(client_id);
                continue;
            }
            if (checkIfNullOrEmpty(spotifySession.getClient_secret())) {
                System.out.println("Enter Spotify client_secret:");
                String client_secret = scanner.nextLine().trim();
                spotifySession.setClient_secret(client_secret);
                continue;
            }
            if (checkIfNullOrEmpty(spotifySession.getRefresh_token())) {
                System.out.println("Enter Refresh Token:");
                String refresh_token = scanner.nextLine().trim();
                spotifySession.setRefresh_token(refresh_token);
                continue;
            }
            if (checkIfNullOrEmpty(spotifySession.getRedirect_uri())) {
                System.out.println("Enter Redirect URI:");
                String redirect_uri = scanner.nextLine().trim();
                spotifySession.setRedirect_uri(redirect_uri);
                continue;
            }
            if (checkIfNullOrEmpty(spotifySession.getPlaylist_id())) {
                System.out.println("Enter Playlist ID:");
                String playlist_id = scanner.nextLine().trim();
                spotifySession.setPlaylist_id(playlist_id);
            }
        }
        Configuration.MapAndWriteConfig();
        System.out.println("Done");
    }
}