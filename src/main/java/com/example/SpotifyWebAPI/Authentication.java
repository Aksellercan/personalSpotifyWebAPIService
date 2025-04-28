package com.example.SpotifyWebAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

public class Authentication {
    private String token;
    private boolean success = false;
    private boolean debugOutput = true;
    private String refreshToken;

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
            if (Headers.length % 2 != 0) {
                throw new IllegalArgumentException("Invalid Headers");
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
            HttpURLConnection http = connectHTTP("https://accounts.spotify.com/api/token", "POST", "Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.connect();
            String postBody = "grant_type=client_credentials&client_id=" + client_id + "&client_secret=" + client_secret;
            try (OutputStream os = http.getOutputStream()) {
                os.write(postBody.getBytes());
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            token = node.get("access_token").asText();
            System.out.println("[ INFO ] Token: " + token + " Success: " + (success = true));
        } catch (Exception e) {
            System.out.println("[ ERROR ] Failed to request access: " + e.getMessage());
        }
        if (token == null) {
            System.out.println("[ INFO ] Token couldn't be acquired. Success: " + success);
        }
    }
    //refactor needed as i dont know how it works
    public void refresh_with_User_Token(String client_id, String client_secret, String code, String redirect_uri) {
        try {
            String encodeString = Base64.getEncoder().encodeToString((client_id + ":" + client_secret).getBytes());
            String Basic = "Basic " + encodeString;
            HttpURLConnection http = connectHTTP("https://accounts.spotify.com/api/token", "POST", "Content-Type","application/x-www-form-urlencoded", "Authorization", Basic);
            http.setDoOutput(true);
            http.connect();
            code = URLEncoder.encode(code, "UTF-8");
            redirect_uri = URLEncoder.encode(redirect_uri, "UTF-8");
            String grant_type = "authorization_code";
            String postBody = "grant_type=" + grant_type + "&code=" + code
                    + "&redirect_uri=" + redirect_uri;

            System.out.println("postBody: " + postBody);
            try (OutputStream os = http.getOutputStream()) {
                os.write(postBody.getBytes());
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            token = node.get("access_token").asText();
            System.out.println("Token: " + token + " after refresh");
            refreshToken = node.get("refresh_token").asText();
            System.out.println("Refresh Token: " + refreshToken);
            System.out.println("Refreshed token: " + token + " Success: " + (success = true));
        } catch (Exception e) {
            System.out.println("[ ERROR ] Failed to request refresh token: " + e.getMessage());
        }
        if (refreshToken == null) {
            System.out.println("[ INFO ] Refresh token couldn't be acquired. Success: " + (success = false));
        }
    }
}