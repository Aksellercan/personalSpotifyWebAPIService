package com.example.SpotifyWebAPI.HTTP;

import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Stateless Class. Used to make POST and GET requests in the program.
 * Throws Exceptions which should be handled in parent methods.
 */
public final class HTTPConnection {

    private HTTPConnection() {}

    /**
     * Establishes the http object then sends the request to the link provided
     * @param requestURL    URL link which the request will be made
     * @param postType  Type of request, POST or GET
     * @param Headers   Sets the request headers. Takes the headers in pairs of strings in an array
     * @return  Returns the established http object
     * @throws IOException  Exception IOException will be thrown by url.openConnection() if there is an error when connecting
     */
    public static HttpURLConnection connectHTTP(String requestURL, String postType, String... Headers) throws IOException{
        Logger.DEBUG.Log("requestURL: " + requestURL + "\n" + "postType: " + postType, false);
        Logger.DEBUG.Log("Headers: ", false);
        URL url = new URL(requestURL);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(postType);
        if (Headers.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid Headers");
        } else {
            for (int i = 0; i < Headers.length; i += 2) {
                Logger.DEBUG.Log(Headers[i] + "\n" + Headers[i+1], false);
                http.setRequestProperty(Headers[i], Headers[i + 1]);
            }
        }
        return http;
    }

    /**
     * Used to write the body of the request
     * @param http  Takes the http object returned by connectHTTP()
     * @param postBody  The body of the request
     * @throws IOException  http.getOutputStream() throws IOException when It can't read output stream
     */

    public static void postBody(HttpURLConnection http, String postBody) throws IOException {
        OutputStream os = http.getOutputStream();
        Logger.DEBUG.Log("Full PostBody: " + postBody, false);
        os.write(postBody.getBytes());
    }

    /**
     * Handles the response body in case of BadRequest
     * @param http  Takes the http object returned by connectHTTP()
     * @param responseCode  Response code returned by the server
     * @throws Exception    readErrorStream() will throw Exception to be logged to notify the user
     */
    public static void readErrorStream(HttpURLConnection http, int responseCode) throws Exception {
        Logger.DEBUG.Log("Response Code: " + http.getResponseCode(), false);
        if (http.getResponseCode() >= responseCode) {
            InputStream error = http.getErrorStream();
            String errorBody = new BufferedReader(new InputStreamReader(error)).lines().collect(Collectors.joining("\n"));
            throw new Exception("POST error response: " + errorBody);
        }
    }
}