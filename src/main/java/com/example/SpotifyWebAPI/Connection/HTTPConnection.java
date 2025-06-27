package com.example.SpotifyWebAPI.Connection;

import com.example.SpotifyWebAPI.Tools.Logger;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class HTTPConnection {
    private boolean debugOutput;

    public void setDebugOutput(boolean debugOutput) {
        this.debugOutput = debugOutput;
    }
    public boolean getDebugOutput() {
        return debugOutput;
    }

    public HttpURLConnection connectHTTP(String requestURL, String postType, String... Headers) {
        try {
            if (debugOutput) {
                Logger.DEBUG.Log("requestURL: " + requestURL + "\n" + "postType: " + postType, false);
                Logger.DEBUG.Log("Headers: ", false);
            }
            URL url = new URL(requestURL);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(postType);
            if (Headers.length % 2 != 0) {
                throw new IllegalArgumentException("Invalid Headers");
            } else {
                for (int i = 0; i < Headers.length; i += 2) {
                    if (debugOutput) {
                        Logger.DEBUG.Log(Headers[i] + "\n" + Headers[i+1], false);
                    }
                    http.setRequestProperty(Headers[i], Headers[i + 1]);
                }
            }
            return http;
        } catch (IOException e) {
            Logger.ERROR.Log(e, "HTTP Connection Error", true);
            throw new RuntimeException("Connection failed: " + e.getMessage());
        }
    }

    public void postBody(HttpURLConnection http, String postBody) {
        try (OutputStream os = http.getOutputStream()) {
            if (debugOutput) Logger.DEBUG.Log("Full PostBody: " + postBody, false);
            os.write(postBody.getBytes());
        } catch (IOException e) {
            Logger.ERROR.Log(e.getMessage());
        }
    }

    public void readErrorStream(HttpURLConnection http, int responseCode, boolean equalandGreater) {
        try {
            if (debugOutput) Logger.DEBUG.Log("Response Code: " + http.getResponseCode(), false);
            if (equalandGreater) {
                if (http.getResponseCode() >= responseCode) {
                    InputStream error = http.getErrorStream();
                    String errorBody = new BufferedReader(new InputStreamReader(error)).lines().collect(Collectors.joining("\n"));
                    throw new Exception("POST error response: " + errorBody);
                }
            } else if (http.getResponseCode() > responseCode) {
                InputStream error = http.getErrorStream();
                String errorBody = new BufferedReader(new InputStreamReader(error)).lines().collect(Collectors.joining("\n"));
                throw new Exception("POST error response: " + errorBody);
            }
        } catch (Exception e) {
            Logger.ERROR.Log(e.getMessage());
        }
    }
}