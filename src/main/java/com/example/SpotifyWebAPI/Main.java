package com.example.SpotifyWebAPI;

import com.example.SpotifyWebAPI.WebRequest.*;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Spotify Web API needed details: ");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Question 1/3: Enter Spotify client_id");
        String client_id = scanner.nextLine();
        System.out.println("Question 2/3: Enter Spotify client_secret");
        String client_secret = scanner.nextLine();
        System.out.println("Question 3/3: Enter Spotify playlist_id");
        String playlist_id = scanner.nextLine();
        System.out.println("Done");

        Authentication httpConnect = new Authentication();
        httpConnect.post_Access_Request(client_id, client_secret);


        while (true) {
            System.out.println("1. Get Playlist by ID");
            System.out.println("2. Set Playlist Description");
            System.out.println("0. Exit");
            if (httpConnect.getSuccess()) {
                switch (scanner.nextLine()) {
                    case "1":
                        System.out.println("API Response: ");
                        System.out.println("Get Playlist Details for " + playlist_id + ":\n");
                        Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request(httpConnect);
                        clientCredentialsRequest.getPlaylist(playlist_id);
                        break;
                    case "2":
                        System.out.println("API Response: ");
                        System.out.println("Set Playlist Description: \n");
                        User_Request userRequest = new User_Request(httpConnect);
                        userRequest.setPlaylistDescription(playlist_id, "Testing Java API");
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
