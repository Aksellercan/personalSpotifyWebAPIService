package com.example.SpotifyWebAPI.HTTP;

import com.example.SpotifyWebAPI.Tools.Logger;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
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
    private static Socket currentSocket;
    private final File bodyPath = new File("Fallback");
    private final File bodyFile = new File(bodyPath + File.separator + "fallback.html");
    private ServerSocket socket = null;

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
                throw new Exception("Failed to interrupt the thread.");
            }
            Logger.INFO.Log("Thread Info: " + thread.getName() + " | " + thread.getState() + " | " + thread.isAlive());
            return true;
        } catch (Exception ex) {
            Logger.CRITICAL.LogException(ex, "Cannot close socket");
            return false;
        }
    }

    public Socket getSocket() {
        return currentSocket;
    }

    private void setSocket(Socket socket) {
        currentSocket = socket;
    }

    public void StartServer() {
        thread = new Thread(this::StartListening);
        thread.start();
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
        File requestedFilePath = new File(bodyPath + File.separator + fileNameWithExtension);
        if (!requestedFilePath.exists()) {
            Logger.WARN.Log("File " + fileNameWithExtension + " not found!");
            return null;
        }
        Logger.INFO.Log("File " + fileNameWithExtension + " found.");
        return requestedFilePath;
    }

    private void StartListening() {
        try {
            serverStatus.isRunning = true;
            Logger.INFO.Log("Starting Server on port " + port);
            socket = new ServerSocket(port, backlog);
            SocketAddress socketAddress = new InetSocketAddress(port);
//            socket.bind(socketAddress);
            Logger.DEBUG.Log("Socket local address: " + socket.getLocalSocketAddress());
            Logger.DEBUG.Log("Socket local port: " + socket.getLocalPort());
            while (serverStatus.isRunning) {
                Socket clientSocket = socket.accept();
                if (!serverStatus.isRunning) {
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

                if (requestedFile.toString().contains("css")) SendStylesheet(out, body);
                if (requestedFile.toString().trim().isEmpty()) {
                    String htmlBody = ResponseBody(bodyFile, StandardCharsets.UTF_8);
                    SendHTML(out, htmlBody);
                }
                if (requestedFile.toString().contains("js")) SendScript(out, body);
                if (requestedFile.toString().contains("ico")) SendImage(out, body);
            }
        } catch (Exception ex) {
            Logger.ERROR.LogException(ex, "Port " + port + " backlog limit: " + 10);
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

    private void SendScript(BufferedWriter out, String body) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 200 OK\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: Custom Server\r\n");
        out.write("Content-Type: text/javascript\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }

    private void SendStylesheet(BufferedWriter out, String body) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 200 OK\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: Custom Server\r\n");
        out.write("Content-Type: text/css\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }

    private void SendHTML(BufferedWriter out, String body) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 200 OK\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: Custom Server\r\n");
        out.write("Content-Type: text/html\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }

    private void SendImage(BufferedWriter out, String body) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 200 OK\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: Custom Server\r\n");
        out.write("Content-Type: image/vnd.microsoft.icon\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }
}
