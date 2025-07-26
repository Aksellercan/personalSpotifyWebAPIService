package com.example.SpotifyWebAPI.HTTP;

import com.example.SpotifyWebAPI.HTTP.Enumerators.ContentType;
import com.example.SpotifyWebAPI.HTTP.Enumerators.StatusCode;
import com.example.SpotifyWebAPI.Tools.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;

public class HTTPServer {
    protected static class ServerStatus {
        public volatile boolean isRunning = false;
    }

    ServerStatus serverStatus = new ServerStatus();
    public static Thread thread;
    private final int port;
    private final int backlog;
    private Socket currentSocket;
    private final File sourceFolder = new File("Fallback");
    private final File indexFile = new File(sourceFolder + File.separator + "index.html");
    private ServerSocket socket = null;
    private final String serverName = "Spotify Web API HTTP Server";

    public HTTPServer(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
    }

    public boolean getServerStatus() {
        return serverStatus.isRunning;
    }

    public boolean StopServer() throws InterruptedException {
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
            Logger.INFO.Log("Stopped Thread Info: " + thread.getName() + " | " + thread.getState() + " | " + thread.isAlive());
            return true;
        } catch (Exception ex) {
            Logger.CRITICAL.LogException(ex, "Cannot close socket");
            return false;
        }
    }

    public Socket getSocket() {
        return this.currentSocket;
    }

    private void setSocket(Socket socket) {
        this.currentSocket = socket;
    }

    public void StartServer() {
        thread = new Thread(this::StartListening);
        thread.start();
        thread.setName("HTTPServerProcess");
        Logger.DEBUG.Log("Thread Info: " + thread.getName() + " | " + thread.getState() + " | " + thread.isAlive());
    }

    private String ResponseBody(File file, Charset encoding) throws IOException {
        if (file == null) {
            return "";
        }
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    private File GetRequestedFile(String fileNameWithExtension) {
        File requestedFilePath = new File(sourceFolder + File.separator + fileNameWithExtension);
        if (!requestedFilePath.exists()) {
            Logger.WARN.Log("File " + fileNameWithExtension + " not found!");
            return null;
        }
        Logger.INFO.Log("File " + fileNameWithExtension + " found.");
        return requestedFilePath;
    }

    private File GetIndexPage() {
        if (!indexFile.exists()) {
            Logger.WARN.Log("File " + indexFile.getName() + " not found!");
            return null;
        }
        return indexFile;
    }

    private void StartListening() {
        try {
            serverStatus.isRunning = true;
            Logger.INFO.Log("Starting Server on port " + port);
            socket = new ServerSocket(port, backlog);
            Logger.DEBUG.Log("Socket local address: " + socket.getLocalSocketAddress() + " InetAddress: " + socket.getInetAddress());
            Logger.DEBUG.Log("Socket local port: " + socket.getLocalPort());
            while (serverStatus.isRunning) {
                Socket clientSocket = socket.accept();
                if (thread.isInterrupted() || !serverStatus.isRunning) {
                    break;
                }
                setSocket(clientSocket);
                Logger.INFO.Log("Waiting for requests... Listening on Port: " + clientSocket.getLocalPort() + " at address: " + clientSocket.getLocalSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String requestType = in.readLine();
                Logger.INFO.Log("Request: " + requestType);

                String body = "";
                StringBuilder requestedFile = new StringBuilder();
                if (requestType != null) {
                    if (requestType.isEmpty()) {
                        break;
                    }
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
                        if (!(requestedFile.length() == 0)) {
                            body = ResponseBody(GetRequestedFile(requestedFile.toString()), StandardCharsets.UTF_8);
                        }
                    }
                }

                String clientInputLine;
                while ((clientInputLine = in.readLine()) != null) {
                    if (clientInputLine.isEmpty()) {
                        break;
                    }
                    Logger.INFO.Log("Request Headers: " + clientInputLine);
                }

                if (requestedFile.toString().contains("css"))
                    PostResponse(out, body, ContentType.StyleSheet.getContentType(), StatusCode.Accepted.getStatusCode());
                if (requestedFile.toString().trim().isEmpty()) {
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
                if (requestedFile.toString().contains("html"))
                    PostResponse(out, body, ContentType.HTML.getContentType(), StatusCode.Accepted.getStatusCode());
                if (requestedFile.toString().contains("js"))
                    PostResponse(out, body, ContentType.JavaScript.getContentType(), StatusCode.Accepted.getStatusCode());
                if (requestedFile.toString().contains("ico"))
                    PostResponse(out, body, ContentType.ImageXIcon.getContentType(), StatusCode.Accepted.getStatusCode());
            }
        } catch (Exception ex) {
            Logger.CRITICAL.LogException(ex, "Port " + port + " backlog limit: " + 10);
        } finally {
            try {
                if (socket == null) {
                    throw new NullPointerException("Socket Object is NULL");
                }
                socket.close();
                System.gc();
                Logger.INFO.Log("Socket Closed and cleared resources.");
            } catch (Exception ex) {
                Logger.CRITICAL.LogException(ex, "Cannot close socket");
            }
        }
    }

    private void PostResponse(BufferedWriter out, String body, String contentType, String statusCode) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 "+ statusCode +"\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: " + serverName + "\r\n");
        out.write("Content-Type: " + contentType + "\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }
}
