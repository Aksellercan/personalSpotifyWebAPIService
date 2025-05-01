package com.example.SpotifyWebAPI.Connection;

import com.example.SpotifyWebAPI.Tools.Logger;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPConnection {
    private boolean debugOutput;
    private boolean writeDebugtoFile = false;

    public void setDebugOutput(boolean debugOutput) {
        this.debugOutput = debugOutput;
    }

    public boolean getDebugOutput() {
        return debugOutput;
    }
    public void setWriteDebugtoFile(boolean writeDebugtoFile) {
        this.writeDebugtoFile = writeDebugtoFile;
    }
    public boolean getWriteDebugtoFile() {
        return writeDebugtoFile;
    }

    public HttpURLConnection connectHTTP(String requestURL, String postType, String... Headers) {
        try {
            if (debugOutput) {
                Logger.DEBUG.Log("requestURL: " + requestURL + "\n" + "postType: " + postType, writeDebugtoFile);
                Logger.DEBUG.Log("Headers: ", writeDebugtoFile);
            }

            URL url = new URL(requestURL);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(postType);
            if (Headers.length % 2 != 0) {
                throw new IllegalArgumentException("Invalid Headers");
            } else {
                for (int i = 0; i < Headers.length; i += 2) {
                    if (debugOutput) {
                        Logger.DEBUG.Log(Headers[i] + "\n" + Headers[i+1], writeDebugtoFile);
                    }
                    http.setRequestProperty(Headers[i], Headers[i + 1]);
                }
            }
            return http;
        } catch (IOException e) {
            Logger.ERROR.Log(e.getMessage());
            return null;
        }
    }

    public void postBody(HttpURLConnection http, String postBody) {
        try (OutputStream os = http.getOutputStream()) {
            if (debugOutput) Logger.DEBUG.Log("Full PostBody: " + postBody, writeDebugtoFile);
            os.write(postBody.getBytes());
        } catch (IOException e) {
            Logger.ERROR.Log(e.getMessage());
        }
    }
}
