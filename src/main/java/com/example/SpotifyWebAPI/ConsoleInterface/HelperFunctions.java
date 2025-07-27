package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import java.util.Scanner;

public class HelperFunctions {

    private FileUtil fileUtil;
    private final ProgramOptions programOptions = ProgramOptions.getInstance();
    private final SpotifySession spotifySession = SpotifySession.getInstance();
    private final Scanner scanner;

    public HelperFunctions(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setFileUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public void checkClientCredentials() {
        while (checkIfNullOrEmpty(spotifySession.getClient_id()) || checkIfNullOrEmpty(spotifySession.getClient_secret())) {
            if (checkIfNullOrEmpty(spotifySession.getClient_id())) {
                System.out.println("Enter Spotify client_id:");
                String client_id = scanner.nextLine().trim();
                spotifySession.setClient_id(client_id);
                programOptions.setChangesSaved(false);
            }
            if (checkIfNullOrEmpty(spotifySession.getClient_secret())) {
                System.out.println("Enter Spotify client_secret:");
                String client_secret = scanner.nextLine().trim();
                spotifySession.setClient_secret(client_secret);
                programOptions.setChangesSaved(false);
            }
        }
        Logger.INFO.LogSilently("Everything is set up correctly, client_id and client_secret are not null or empty.");
    }

    public boolean checkIfNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public void clearScreen() {
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

    public void saveConfig() {
        fileUtil.writeConfig("client_id", spotifySession.getClient_id(), "client_secret", spotifySession.getClient_secret(), "redirect_uri",
                spotifySession.getRedirect_uri(), "refresh_token", spotifySession.getRefresh_token(), "playlist_id",spotifySession.getPlaylist_id(), "auto_mode",
                Boolean.toString(programOptions.isAutoMode()), "output_debug", Boolean.toString(programOptions.isDebugMode()),
                "user_id",spotifySession.getUser_id(),"launch_gui", Boolean.toString(programOptions.LAUNCH_GUI()));
        programOptions.setChangesSaved(true);
    }

    public void getPlaylistItems(User_Request userRequest) {
        int offset = 0;
        int limit = 10;
        String playlist_id = spotifySession.getPlaylist_id();
        while (checkIfNullOrEmpty(playlist_id)) {
            System.out.println("Enter Playlist ID:");
            playlist_id = scanner.nextLine().trim();
            programOptions.setChangesSaved(false);
        }
        System.out.println("Change playlist ID? (y/n)");
        if (scanner.nextLine().equals("y")) {
            System.out.println("Enter new Playlist ID:");
            playlist_id = scanner.nextLine();
            spotifySession.setPlaylist_id(playlist_id);
            programOptions.setChangesSaved(false);
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

    public int setPosition() {
        int position;
        System.out.println("Enter Position to insert the track:");
        position = scanner.nextInt();
        return position;
    }

    public String addTrackUri() {
        String track_uri = null;
        while (checkIfNullOrEmpty(track_uri)) {
            System.out.println("Enter Track URI:");
            track_uri = scanner.nextLine().trim();
        }
        System.out.println("Adding track: " + track_uri);
        return track_uri;
    }

    public void createPlaylistDetails(User_Request userRequest) {
        String createName = null;
        String createDescription = null;
        String user_id = spotifySession.getUser_id();
        while ((checkIfNullOrEmpty(user_id)) || (checkIfNullOrEmpty(createName)) ||
                (checkIfNullOrEmpty(createDescription))) {
            if (checkIfNullOrEmpty(user_id)) {
                System.out.println("Enter User ID:");
                user_id = scanner.nextLine().trim();
                spotifySession.setUser_id(user_id);
                programOptions.setChangesSaved(false);
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

    public void setPlaylistDetails(User_Request userRequest) {
        String playlist_id = spotifySession.getPlaylist_id();
        while (checkIfNullOrEmpty(playlist_id)) {
            System.out.println("Enter Playlist ID:");
            playlist_id = scanner.nextLine().trim();
            spotifySession.setPlaylist_id(playlist_id);
            programOptions.setChangesSaved(false);
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

    public void setupAutoMode() {
        while (((checkIfNullOrEmpty(spotifySession.getRefresh_token())) || (checkIfNullOrEmpty(spotifySession.getRedirect_uri())))
                || ((checkIfNullOrEmpty(spotifySession.getClient_id())) ||
                (checkIfNullOrEmpty(spotifySession.getClient_secret()))) || (checkIfNullOrEmpty(spotifySession.getPlaylist_id())) ||
                (checkIfNullOrEmpty(spotifySession.getUser_id()))) {
            programOptions.setChangesSaved(false);
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
        saveConfig();
        System.out.println("Done");
    }
}