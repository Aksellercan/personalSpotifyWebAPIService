package com.example.SpotifyWebAPI.HTTP;

import com.example.SpotifyWebAPI.HTTP.Enumerators.ContentType;
import com.example.SpotifyWebAPI.HTTP.Enumerators.StatusCode;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;

/**
 * Starts and manages the HTTP Server
 * Runs asynchronously
 */
public class HTTPServer extends Thread {
    protected static class ServerStatus {
        public volatile boolean isRunning = false;
    }

    private final ServerStatus serverStatus = new ServerStatus();
    public static Thread thread;
    private final int port;
    private final int backlog;
    private File sourceFolder = new File("Pages" + File.separator + "Fallback");
    private File indexFile = new File(sourceFolder + File.separator + "index.html");
    private ServerSocket socket;
    private final String serverName = "Spotify Web API HTTP Server";

    /**
     * Server constructor
     *
     * @param port    Set the port the server will run on
     * @param backlog Set the limit of connections for the socket
     */
    public HTTPServer(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
    }

    /**
     * Returns the Server status
     *
     * @return Server Status
     */
    public boolean getServerStatus() {
        return serverStatus.isRunning;
    }

    public ServerSocket GetServerSocket() {
        return socket;
    }

    /**
     * Stops the Server.
     *
     * @return returns true if it succeeds
     */
    public boolean StopServer() {
        try {
            if (thread == null) {
                throw new NullPointerException("Server is already stopped or not started");
            }
            serverStatus.isRunning = false;
            thread.interrupt();
            if (thread.isInterrupted()) {
                Logger.INFO.Log("Server Stopped.");
            } else {
                throw new InterruptedException("Failed to interrupt the thread.");
            }
            Logger.INFO.Log("Stopped Thread name: " + thread.getName() + " | Status = " + thread.getState() + " | isAlive = " + thread.isAlive());
            return true;
        } catch (Exception ex) {
            Logger.CRITICAL.LogException(ex, "Cannot close socket");
            return false;
        }
    }

    /**
     * Starts server on new thread
     */
    @Override
    public void run() {
        thread = Thread.currentThread();
        thread.setName(thread.getName() + "-HTTPServer");
        Logger.DEBUG.Log("Thread name = " + thread.getName() + " | Status = " + thread.getState() + " | isAlive = " + thread.isAlive());
        StartListening();
    }

    /**
     * Gets the file and encodes it with specified Charset encoding
     *
     * @param file     File to be encoded
     * @param encoding Encoding to use
     * @return Encoded file in string
     * @throws IOException If the file cannot be read
     */
    private String ResponseBody(File file, Charset encoding) throws IOException {
        if (!file.exists()) {
            return "";
        }
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    /**
     * Gets files in folders by building the String path as File object
     *
     * @param requestedFile Requested file
     * @return Entire file path. If the string ends without any file extensions it returns index.html
     */
    private File GetFolders(String requestedFile) {
        StringBuilder folders = new StringBuilder();
        StringBuilder folderNames = new StringBuilder();
        for (int i = 5; i < requestedFile.length(); i++) {
            if (requestedFile.charAt(i) == ' ') {
                break;
            }
            if (requestedFile.charAt(i) == '/') {
                folders.append(folderNames).append(File.separator);
                folderNames.setLength(0);
                continue;
            }
            folderNames.append(requestedFile.charAt(i));
        }
        if (folderNames.toString().isEmpty()) {
            return GetIndexPage();
        }
        return new File(sourceFolder.getAbsolutePath() + File.separator + folders + folderNames);
    }

    /**
     * Gets the requested file name with extension
     *
     * @param requestType Requested file
     * @return Name of the file with extension
     */
    private String GetFilename(String requestType) {
        StringBuilder requestedFile = new StringBuilder();
        if (requestType.contains("GET")) {
            for (int i = 5; i < requestType.length(); i++) {
                if (requestType.charAt(i) == '/') {
                    requestedFile.setLength(0);
                    continue;
                }
                if (requestType.charAt(i) == ' ') {
                    break;
                }
                requestedFile.append(requestType.charAt(i));
            }
        }
        return requestedFile.toString();
    }

    /**
     * Gets the index.html from parent directory
     *
     * @return Returns index.html
     */
    private File GetIndexPage() {
        if (!indexFile.exists()) {
            Logger.WARN.Log("File " + indexFile.getName() + " not found!");
            return null;
        }
        return indexFile;
    }

    /**
     * Starts listening on the specified socket.
     * It reads HTTP requests and tries to get the requested files
     * Depending on the file extension sets the "Content-Type"
     */
    private void StartListening() {
        try {
            Logger.INFO.Log("Starting Server on port " + port);
            socket = new ServerSocket(port, backlog);
            serverStatus.isRunning = true;
            Logger.DEBUG.Log("Socket local address: " + socket.getLocalSocketAddress() + " InetAddress: " + socket.getInetAddress());
            Logger.DEBUG.Log("Socket local port: " + socket.getLocalPort());
            while (serverStatus.isRunning) {
                Socket clientSocket = socket.accept();
                if (thread.isInterrupted() || !serverStatus.isRunning) {
                    thread = null;
                    break;
                }
                Logger.INFO.Log("Waiting for requests... Listening on Port: " + clientSocket.getLocalPort() + " at address: " + clientSocket.getLocalSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String requestType = in.readLine();
                Logger.INFO.Log("Request: " + requestType);

                String body = "";
                String requestedFile = null;
                if (requestType != null) {
                    if (requestType.isEmpty()) {
                        break;
                    }
                    requestedFile = GetFilename(requestType);
                    if (!(requestedFile.isEmpty())) {
                        Logger.DEBUG.Log("Requested file: " + requestedFile);
                        body = ResponseBody(GetFolders(requestType), StandardCharsets.UTF_8);
                    }
                }

                String clientInputLine;
                while ((clientInputLine = in.readLine()) != null) {
                    if (clientInputLine.isEmpty()) {
                        break;
                    }
                    Logger.INFO.Log("Request Headers: " + clientInputLine);
                }

                if (requestedFile != null) {
                    if (requestedFile.contains("css"))
                        PostResponse(out, body, ContentType.StyleSheet.getContentType(), StatusCode.Accepted.getStatusCode());
                    if (requestedFile.trim().isEmpty()) {
                        File htmlFile = GetIndexPage();
                        String htmlBody;
                        String statusCode;
                        String contentType;
                        if (htmlFile != null) {
                            contentType = ContentType.HTML.getContentType();
                            statusCode = StatusCode.OK.getStatusCode();
                            htmlBody = ResponseBody(htmlFile, StandardCharsets.UTF_8);
                        } else {
                            contentType = ContentType.TextPlain.getContentType();
                            statusCode = StatusCode.NotFound.getStatusCode();
                            htmlBody = "404 Not Found";
                        }
                        PostResponse(out, htmlBody, contentType, statusCode);
                    }
                    if (requestedFile.contains("html"))
                        PostResponse(out, body, ContentType.HTML.getContentType(), StatusCode.Accepted.getStatusCode());
                    if (requestedFile.contains("js"))
                        PostResponse(out, body, ContentType.JavaScript.getContentType(), StatusCode.Accepted.getStatusCode());
                    if (requestedFile.contains("ico"))
                        PostResponse(out, body, ContentType.ImageXIcon.getContentType(), StatusCode.Accepted.getStatusCode());
                }
            }
        } catch (Exception ex) {
            Logger.CRITICAL.LogException(ex, "Port " + port + " backlog limit: " + 10);
        } finally {
            try {
                if (socket == null) {
                    throw new NullPointerException("Socket Object is NULL");
                }
                socket.close();
                Logger.INFO.Log("Closed Socket.");
            } catch (Exception ex) {
                Logger.CRITICAL.LogException(ex, "Cannot close socket");
            }
        }
    }

    /**
     * Sends out the response body with headers
     *
     * @param out         BufferedWriter object
     * @param body        Body of the response
     * @param contentType Content type of the response
     * @param statusCode  Status code of the response
     * @throws IOException If the writer is unsuccessful
     */
    private void PostResponse(BufferedWriter out, String body, String contentType, String statusCode) throws IOException {
        int bodyLength = body.length();
        LocalDateTime now = LocalDateTime.now();
        Logger.DEBUG.Log("HTTP/1.0 " + statusCode);
        Logger.DEBUG.Log("Date: " + now);
        Logger.DEBUG.Log("Server: " + serverName);
        Logger.DEBUG.Log("Content-type: " + contentType);
        Logger.DEBUG.Log("Content-Length: " + bodyLength);
        out.write("HTTP/1.0 " + statusCode + "\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: " + serverName + "\r\n");
        out.write("Content-Type: " + contentType + "\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }
}
