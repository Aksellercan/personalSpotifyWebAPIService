package com.example.SpotifyWebAPI.JavaFXInterface;

import com.example.SpotifyWebAPI.Connection.Client_Credentials_Token;
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
    @FXML private TextArea responseTextArea;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spotify Web API GUI alpha");
        primaryStage.setResizable(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        Parent root = setFXMLFile("PrimaryPage");

        Scene mainWindow = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        setStyleSheet(mainWindow, "PrimaryPage");
        primaryStage.setScene(mainWindow);
        primaryStage.show();
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
            return FXMLLoader.load(getClass().getResource("/Layouts/" + fxmlFilename +".fxml"));
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Cannot load FXML");
            return null;
        }
    }

    @FXML protected void OnGetTokenButtonClick(ActionEvent event) {
        client_credentials_token.post_Access_Request();
        Logger.DEBUG.Log("Event: " + event.toString());
        responseTextArea.setText("Access token: " + spotifySession.getAccess_token());
    }

    @FXML protected void OnGetPlaylistDataButtonClick(ActionEvent event) {
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
