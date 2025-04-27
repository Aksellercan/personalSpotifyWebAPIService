package com.example.SpotifyWebAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Authentication {
    private String token;
    private boolean success = false;
    private boolean debugOutput = false;

    public String getToken() {
        return token;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setDebugOutput(boolean debugOutput) {
        this.debugOutput = debugOutput;
    }

    public boolean getDebugOutput() {
        return debugOutput;
    }

    public HttpURLConnection connectHTTP(String requestURL, String postType, String... Headers) {
        try {
            if (debugOutput)
                System.out.println("requestURL: " + requestURL + "\n" + "postType: " + postType + "\n" + "Headers: ");
            URL url = new URL(requestURL);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(postType);
            if (Headers.length == 2) {
                if (debugOutput) {
                    System.out.println(Headers[0]);
                    System.out.println(Headers[1]);
                }
                http.setRequestProperty(Headers[0], Headers[1]);
            } else {
                for (int i = 0; i < Headers.length; i += 2) {
                    if (debugOutput) {
                        System.out.println(Headers[i]);
                        System.out.println(Headers[i + 1]);
                    }
                    http.setRequestProperty(Headers[i], Headers[i + 1]);
                }
            }
            return http;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public void post_Access_Request(String client_id, String client_secret) {
        try {
            HttpURLConnection http = connectHTTP("https://accounts.spotify.com/api/token", "POST", "Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.connect();
            String postBody = "grant_type=client_credentials&client_id=" + client_id + "&client_secret=" + client_secret;
            try (OutputStream os = http.getOutputStream()) {
                os.write(postBody.getBytes());
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            token = node.get("access_token").asText();
            System.out.println("Token: " + token + " Success: " + (success = true));
        } catch (Exception e) {
            System.out.println("Error requesting access: " + e.getMessage());
        }
        if (token == null) {
            System.out.println("Token couldn't be acquired." + " Success: " + success);
        }
    }
}