package com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Views;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.HTTP.SaveHTTPState;
import com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.Tokens.Client_Credentials_Token;
import com.example.SpotifyWebAPI.HTTP.HTTPServer;
import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.example.SpotifyWebAPI.Tools.Files.Parsers.JSONParser;
import com.example.SpotifyWebAPI.Tools.Files.Parsers.YAMLParser;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.HttpURLConnection;

/**
 * JavaFX GUI Class
 */
public class GUI extends Application {
    private final SpotifySession spotifySession = SpotifySession.getInstance();
    private final Client_Credentials_Token client_credentials_token = new Client_Credentials_Token();
    private final Client_Credentials_Request clientCredentials_Request = new Client_Credentials_Request();
    private HTTPServer httpServer;
    @FXML
    private TextArea responseTextArea;
    @FXML
    protected TextField pageSearchField;

    /**
     * Sets up the primary stage
     *
     * @param primaryStage JavaFX stage object
     */
    @Override
    public void start(Stage primaryStage) {
        SceneActions.LoadPagesToArray();
        primaryStage.setTitle("Spotify Web API GUI alpha");
        primaryStage.setResizable(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            YAMLParser.MapAndWriteConfig();
//            JSONParser.MapAndWriteConfig();
            if (SceneActions.StopBackgroundHTTPThread()) {
                Logger.INFO.Log("Closed Session. Reason: " + event.getEventType());
                System.exit(0);
            }
            Logger.ERROR.Log("Failed to close session.");
            System.exit(1);
        });
        try {
            /*
            Sets the icon of the program, commented out because icon is not available
             */
            primaryStage.getIcons().add(new Image("/Icons/appicon.jpg"));
            SceneActions.SetDefaultStylesheet("PrimaryPage");
            SceneActions.SetCurrentStage(primaryStage);
            SceneActions.ChangeScene("PrimaryPage");
            primaryStage.show();
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Cannot start JavaFX GUI");
        }
    }

    @FXML
    protected void OnChangeSceneButton(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.ChangeScene("Webview");
    }

    @FXML
    protected void OnPageSearchButton(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.SearchTermSelector(pageSearchField, pageSearchField.getText());
    }

    @FXML
    protected void GoToSettingsScene(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.ChangeScene("SettingsPage");
    }

    @FXML
    protected void OnStartServerButtonClick(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (!SaveHTTPState.ContainsServer("Fallback")) {
            httpServer = new HTTPServer(0, 10);
            httpServer.start();
            SaveHTTPState.addHTTPServerToHashMap("Fallback", httpServer);
            Thread checkLoop = new Thread(() -> {
                try {
                    for (int i = 1; i <= 10; i++) {
                        if (httpServer.GetServerSocket() != null) {
                            responseTextArea.setText("Available at http://127.0.0.1:" + SaveHTTPState.getServer("Fallback").GetServerSocket().getLocalPort());
                            break;
                        }
                        Logger.DEBUG.Log("Checked " + i + (i > 1 ? " times" : " time"), false);
                        Thread.sleep(2500);

                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            checkLoop.start();
            return;
        }
        Logger.DEBUG.Log("Fallback page already started");
        responseTextArea.setText("Available at http://127.0.0.1:" + SaveHTTPState.getServer("Fallback").GetServerSocket().getLocalPort());
        Logger.DEBUG.Log("Active Thread Count: " + Thread.activeCount());
    }

    @FXML
    protected void OnStopServerButtonClick(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (httpServer == null) {
            responseTextArea.setText("HTTP Server is null");
        }
        try {
            httpServer = SaveHTTPState.getServer("Fallback");
            if (httpServer != null && httpServer.StopServer()) {
                responseTextArea.setText("HTTP Server Stopped");
                HttpURLConnection http = HTTPConnection.connectHTTP("http://127.0.0.1:" + SaveHTTPState.getServer("Fallback").GetServerSocket().getLocalPort(), "GET");
                http.connect();
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
    protected void GoToAddItemScene(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.ChangeScene("AddItemPage");
    }

    @FXML
    protected void OnGetPlaylistDataButtonClick(ActionEvent event) {
        if (spotifySession.getAccess_token() == null) {
            responseTextArea.setText("Access token is null");
            return;
        }
        clientCredentials_Request.getPlaylist(spotifySession.getPlaylist_id());
        Logger.DEBUG.Log("Event: " + event.toString());
        responseTextArea.setText("Playlist:\n" + "Name: " + clientCredentials_Request.getPlaylist().getName() +
                "\nDescription: " + clientCredentials_Request.getPlaylist().getDescription() +
                "\nSize: " + clientCredentials_Request.getPlaylist().getTotalItems());
    }
}
