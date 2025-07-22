package com.example.SpotifyWebAPI.JavaFXInterface;

import com.example.SpotifyWebAPI.Concurrency.ProcessThread;
import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.HTTP.HTTPServer;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class GUI extends Application {
    private final SpotifySession spotifySession = SpotifySession.getInstance();
    private final ProgramOptions programOptions = ProgramOptions.getInstance();
    private final Client_Credentials_Token client_credentials_token = new Client_Credentials_Token();
    private final Client_Credentials_Request clientCredentials_Request = new Client_Credentials_Request();
    private HTTPServer httpServer = new HTTPServer(8080, 10);
    private SavedScene stage = SavedScene.getInstance();
    @FXML
    private TextArea responseTextArea;
    private final ProcessThread processThread = new ProcessThread();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spotify Web API GUI alpha");
        primaryStage.setResizable(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        Logger.DEBUG.Log("Is Stage NULL? " + ((stage == null) ? "Yes" : "No") + ".");

        Parent root = setFXMLFile("PrimaryPage");

        Scene window = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        setStyleSheet(window, "PrimaryPage");
        primaryStage.setScene(window);
        primaryStage.show();
        stage.setScene(primaryStage);
    }

    private void setStyleSheet(Scene scene, String styleSheetName) {
        try {
            scene.getStylesheets().add(getClass().getResource("/Styles/" + styleSheetName + ".css").toExternalForm());
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Cannot load stylesheet");
        }
    }

    private Parent setFXMLFile(String fxmlFilename) {
        try {
            return FXMLLoader.load(getClass().getResource("/Layouts/" + fxmlFilename + ".fxml"));
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Cannot load FXML");
            return null;
        }
    }

    @FXML
    protected void OnChangeWindowButtonClick(ActionEvent event) {
//        stage = SavedScene.getInstance();
//        if (stage.getScene() == null) { return;}
        Logger.DEBUG.Log("Event: " + event.toString());
//        Parent root = setFXMLFile("SecondPage");
//        Scene window = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
//        setStyleSheet(window, "PrimaryPage");
//        stage.getScene().setScene(window);
        if (httpServer == null) {
            Logger.DEBUG.Log("httpServer is null");
            httpServer = new HTTPServer(8080, 10);
        }
        httpServer.StartServer();

        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    if (httpServer.getSocket() != null) {
                        responseTextArea.setText("Waiting for requests... Listening on Port: " + httpServer.getSocket().getLocalPort() + "\non Address: " + httpServer.getSocket().getInetAddress().getHostAddress());
                        break;
                    }
                    Logger.DEBUG.Log("Refreshed " + (i+1) + (i+1 > 1 ? " times" : " time") + "...");
                    Thread.sleep(2500);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Logger.DEBUG.Log("Test: " + httpServer.isServerRunning());
        }).start();
    }

    @FXML
    protected void OnStopServerButtonClick(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        Logger.INFO.Log("Attempting to stop the server...");
        if (httpServer == null) {
            responseTextArea.setText("HTTP Server is null");
        }
        try {
            if (httpServer.StopServer()) {
                responseTextArea.setText("HTTP Server Stopped");
            } else {
                responseTextArea.setText("Cannot stop the server");
            }
        } catch (InterruptedException e) {
            Logger.ERROR.LogException(e, "Exception in StopServerTask");
        }
    }

    @FXML
    protected void OnGetTokenButtonClick(ActionEvent event) {
        client_credentials_token.post_Access_Request();
        Logger.DEBUG.Log("Event: " + event.toString());
        responseTextArea.setText("Access token: " + spotifySession.getAccess_token());
    }

    @FXML
    protected void OnGetPlaylistDataButtonClick(ActionEvent event) {
        if (spotifySession.getAccess_token() == null) {
            responseTextArea.setText("Access token is null");
            return;
        }
        clientCredentials_Request.getPlaylist(programOptions.getPlaylist_id());
        Logger.DEBUG.Log("Event: " + event.toString());
        responseTextArea.setText("Playlist:\n" + "Name: " + clientCredentials_Request.getplaylistName() +
                "\nDescription: " + clientCredentials_Request.getplaylistDescription() + "\nSize: " + clientCredentials_Request.getplaylistSize());
    }
}
