package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Connection.HTTPConnection;
import com.example.SpotifyWebAPI.Connection.User_Access_Token;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import java.util.Scanner;


public class UserRequestsMenu {
    private final SpotifySession spotify_session;
    private final HTTPConnection httpConnection = new HTTPConnection();
    private final Scanner scanner;
    private final ProgramOptions programOptions;
    private final HelperFunctions helperFunctions;
    private final FileUtil fileUtil;

    public UserRequestsMenu(SpotifySession spotify_session, ProgramOptions programOptions, Scanner scanner, FileUtil fileUtil) {
        this.spotify_session = spotify_session;
        this.programOptions = programOptions;
        this.scanner = scanner;
        this.fileUtil = fileUtil;
        this.helperFunctions = new HelperFunctions(programOptions,spotify_session, scanner);
    }

    public void Oauth2_Functions() {
        helperFunctions.setFileUtil(fileUtil);
        while (true) {
            helperFunctions.clearScreen();
            System.out.println("User Requests Menu" + (helperFunctions.checkIfNullOrEmpty(spotify_session.getRefresh_token()) ? " - No Refresh Token. Get Token First" : ""));
            System.out.println("1. Get Refresh Token to access User Requests");
            System.out.println("2. Refresh access token using refresh token");
            System.out.println("3. Edit Details of a Playlist");
            System.out.println("4. Get Playlist items");
            System.out.println("5. add song to Playlist");
            System.out.println("6. Create Playlist");
            System.out.println("0. Go Back");
            User_Access_Token userAccessToken = new User_Access_Token(httpConnection, spotify_session);
            User_Request userRequest = new User_Request(httpConnection, spotify_session);
            if (httpConnection.getDebugOutput()) userAccessToken.printData();
            switch (scanner.nextLine()) {
                case "1":
                    String code = null;
                    while (helperFunctions.checkIfNullOrEmpty(code) || (helperFunctions.checkIfNullOrEmpty(spotify_session.getRedirect_uri()))) {
                        programOptions.setChangesSaved(false);
                        if (helperFunctions.checkIfNullOrEmpty(code)) {
                            System.out.println("Enter Spotify code:");
                            code = scanner.nextLine().trim();
                            spotify_session.setCode(code);
                            continue;
                        }
                        if (helperFunctions.checkIfNullOrEmpty(spotify_session.getRedirect_uri())) {
                            System.out.println("Enter Redirect URI:");
                            String redirect_uri = scanner.nextLine().trim();
                            spotify_session.setRedirect_uri(redirect_uri);
                        }
                    }
                    userAccessToken.get_Refresh_Token();
                    System.out.println("Save the refresh token to config?");
                    if (scanner.nextLine().equals("y")) {
                        helperFunctions.saveConfig();
                        Logger.INFO.Log("Saved Config successfully!");
                    }
                    break;
                case "2":
                    while (helperFunctions.checkIfNullOrEmpty(spotify_session.getRefresh_token()) || (helperFunctions.checkIfNullOrEmpty(spotify_session.getRedirect_uri()))) {
                        programOptions.setChangesSaved(false);
                        if (helperFunctions.checkIfNullOrEmpty(spotify_session.getRefresh_token())) {
                            System.out.println("Enter Refresh Token:");
                            String refresh_token = scanner.nextLine().trim();
                            spotify_session.setRefresh_token(refresh_token);
                            continue;
                        }
                        if (helperFunctions.checkIfNullOrEmpty(spotify_session.getRedirect_uri())) {
                            System.out.println("Enter Redirect URI:");
                            String redirect_uri = scanner.nextLine().trim();
                            spotify_session.setRedirect_uri(redirect_uri);
                        }
                    }
                    userAccessToken.refresh_token_with_User_Token();
                    break;
                case "3":
                    helperFunctions.setPlaylistDetails(userRequest);
                    break;
                case "4":
                    helperFunctions.getPlaylistItems(userRequest);
                    break;
                case "5":
                    System.out.println("Add to new playlist? (y/n)");
                    String chosenPlaylist = null;
                    int position;
                    if (scanner.nextLine().equals("y")) {
                        while (helperFunctions.checkIfNullOrEmpty(chosenPlaylist)) {
                            System.out.println("Enter Playlist ID:");
                            chosenPlaylist = scanner.nextLine().trim();
                        }
                        System.out.println("Adding to playlist: " + chosenPlaylist);
                        position = helperFunctions.setPosition();
                        userRequest.addPlaylistItems(chosenPlaylist, position, helperFunctions.addTrackUri(), false);
                        break;
                    }
                    System.out.println("Adding to playlist: " + programOptions.getPlaylist_id());
                    position = helperFunctions.setPosition();
                    userRequest.addPlaylistItems(programOptions.getPlaylist_id(), position, helperFunctions.addTrackUri(), false);
                    break;
                case "6":
                    helperFunctions.createPlaylistDetails(userRequest);
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
