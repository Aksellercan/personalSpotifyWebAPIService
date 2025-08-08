package com.example.SpotifyWebAPI.JavaFXInterface;

import com.example.SpotifyWebAPI.HTTP.SaveHTTPState;
import com.example.SpotifyWebAPI.JavaFXInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.HTTP.HTTPServer;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX GUI Class
 */
public class GUI extends Application implements Initializable {
    private final SpotifySession spotifySession = SpotifySession.getInstance();
    private final Client_Credentials_Token client_credentials_token = new Client_Credentials_Token();
    private final Client_Credentials_Request clientCredentials_Request = new Client_Credentials_Request();
    private HTTPServer httpServer = new HTTPServer(8080, 10);
    private final Thread thread = new Thread(httpServer);
    private SavedScene stage = SavedScene.getInstance();
    @FXML
    private TextArea responseTextArea;
    @FXML
    private WebView webviewBox;

    /**
     * Sets up the primary stage
     * @param primaryStage  JavaFX stage object
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spotify Web API GUI alpha");
        primaryStage.setResizable(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setOnCloseRequest(event -> {
            Logger.INFO.Log("Closed Session.");
            System.exit(0);
        });
        primaryStage.getIcons().add(new Image("/Icons/appicon1.jpg"));


        Logger.DEBUG.Log("Is Stage NULL? " + ((stage == null) ? "Yes" : "No") + ".");

        Parent root = SceneActions.setFXMLFile("PrimaryPage");

        Scene window = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        SceneActions.setStyleSheet(window, "PrimaryPage");
        primaryStage.setScene(window);
        primaryStage.show();
        stage.setScene(primaryStage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        if (httpServer == null) {
//            Logger.DEBUG.Log("httpServer is null");
//            httpServer = new HTTPServer(8080, 10);
//            SaveHTTPState.addHTTPServerToHashMap("playback", httpServer);
//        }
//        if (!httpServer.getServerStatus()) {
//            thread.start();
//        }
//        WebEngine e = webviewBox.getEngine();
//        e.load("http://localhost:8080");//placeholder
//        Logger.DEBUG.Log(e.getLocation());
    }

    @FXML
    protected void OnChangeSceneButton(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        stage = SavedScene.getInstance();
        if (stage.getScene() == null) {
            return;
        }
        Parent root = SceneActions.setFXMLFile("SecondPage");
        if (root == null) {
            Logger.CRITICAL.Log("FXML root is null");
            return;
        }
        Scene window = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        SceneActions.setStyleSheet(window, "PrimaryPage");
        stage.getScene().setScene(window);
    }

    @FXML
    protected void OnStartServerButtonClick(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (SaveHTTPState.getHashMapSize() != 0) {
            Logger.INFO.Log("Server already initialized");
            httpServer = SaveHTTPState.getServer("Fallback");
            if (httpServer.getSocket() != null)
                responseTextArea.setText("Waiting for requests... Listening on Port: " + httpServer.getSocket().getLocalPort() + "\non Address: " + httpServer.getSocket().getInetAddress().getHostAddress());
            return;
        }
        if (httpServer == null) {
            Logger.DEBUG.Log("httpServer is null");
            httpServer = new HTTPServer(0, 10);
            SaveHTTPState.addHTTPServerToHashMap("Fallback", httpServer);
        }
        if (!httpServer.getServerStatus()) {
            thread.start();
        }
        SaveHTTPState.addHTTPServerToHashMap("Fallback", httpServer);
        responseTextArea.setText("Server Status: Running");
        Thread t = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    if (httpServer.getSocket() != null) {
                        responseTextArea.setText("Waiting for requests... Listening on Port: " + httpServer.getSocket().getLocalPort() + "\non Address: " + httpServer.getSocket().getInetAddress().getHostAddress());
                        break;
                    }
                    Logger.DEBUG.Log("Refreshed " + (i + 1) + (i + 1 > 1 ? " times" : " time") + "...");
                    Thread.sleep(2500);
                }
                Logger.DEBUG.Log("Server Status: " + (httpServer.getServerStatus() ? "Running" : "Stopped"));
                SaveHTTPState.addHTTPServerToHashMap("Fallback", httpServer);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();

        Logger.DEBUG.Log("Active Thread Count: " + Thread.activeCount());
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
                SaveHTTPState.removeServer("Fallback");
            } else {
                responseTextArea.setText("Cannot stop the server");
            }
        } catch (Exception e) {
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
        clientCredentials_Request.getPlaylist(spotifySession.getPlaylist_id());
        Logger.DEBUG.Log("Event: " + event.toString());
        responseTextArea.setText("Playlist:\n" + "Name: " + clientCredentials_Request.getplaylistName() +
                "\nDescription: " + clientCredentials_Request.getplaylistDescription() + "\nSize: " + clientCredentials_Request.getplaylistSize());
    }
}
