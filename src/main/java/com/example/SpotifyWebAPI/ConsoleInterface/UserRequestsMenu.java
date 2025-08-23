package com.example.SpotifyWebAPI.ConsoleInterface;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.Tools.ConsoleColours;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.User_Request;

/**
 * CLI Menu for User Access Token requests
 */
public class UserRequestsMenu extends HelperFunctions {
    /**
     * Menu Entries
     */
    public void Oauth2_Functions() {
        while (true) {
            clearScreen();
            System.out.println("User Requests Menu" + (checkIfNullOrEmpty(spotifySession.getRefresh_token()) ? " - No Refresh Token. Get Token First" : ""));
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
                    String code = null;
                    while (checkIfNullOrEmpty(code) || (checkIfNullOrEmpty(spotifySession.getRedirect_uri()))) {
                        ProgramOptions.setChangesSaved(false);
                        if (checkIfNullOrEmpty(code)) {
                            System.out.println("Enter Spotify code:");
                            code = scanner.nextLine().trim();
                            spotifySession.setCode(code);
                            continue;
                        }
                        if (checkIfNullOrEmpty(spotifySession.getRedirect_uri())) {
                            System.out.println("Enter Redirect URI:");
                            String redirect_uri = scanner.nextLine().trim();
                            spotifySession.setRedirect_uri(redirect_uri);
                        }
                    }
                    userAccessToken.get_Refresh_Token();
                    System.out.println("Save the refresh token to config?");
                    if (scanner.nextLine().equals("y")) {
                        FileUtil.WriteConfig();
                        Logger.INFO.Log("Saved Config successfully!");
                    }
                    break;
                case "2":
                    while (checkIfNullOrEmpty(spotifySession.getRefresh_token()) || (checkIfNullOrEmpty(spotifySession.getRedirect_uri()))) {
                        ProgramOptions.setChangesSaved(false);
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
                        while (checkIfNullOrEmpty(chosenPlaylist)) {
                            System.out.println("Enter Playlist ID:");
                            chosenPlaylist = scanner.nextLine().trim();
                        }
                        System.out.println("Adding to playlist: " + chosenPlaylist);
                        position = setPosition();
                        userRequest.addPlaylistItems(chosenPlaylist, position, addTrackUri(), false);
                        break;
                    }
                    System.out.println("Adding to playlist: " + spotifySession.getPlaylist_id());
                    position = setPosition();
                    userRequest.addPlaylistItems(spotifySession.getPlaylist_id(), position, addTrackUri(), false);
                    break;
                case "6":
                    createPlaylistDetails(userRequest);
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
