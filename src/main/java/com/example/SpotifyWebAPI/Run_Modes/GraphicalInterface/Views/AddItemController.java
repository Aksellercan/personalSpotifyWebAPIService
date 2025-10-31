package com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Views;

import com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.System.HTTPWorker;
import com.example.SpotifyWebAPI.System.children.GetPlaylistWorker;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request;
import com.example.SpotifyWebAPI.WebRequest.User_Request;
import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;


public class AddItemController implements Initializable {
    @FXML
    private TextField searchTrackField;
    @FXML
    private TextArea foundTrackArea;
    @FXML
    private Label showPlaylistDetails;
    @FXML
    private TextField pageSearchField;
    private final Client_Credentials_Request clientCredentialsRequest = new Client_Credentials_Request();

    @FXML
    protected void GoToMainMenu(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.ChangeScene("PrimaryPage");
    }

    @FXML
    protected void AddItemToPlaylist(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (searchTrackField.getText().isEmpty()) {
            foundTrackArea.setText("Track not found!");
            return;
        }
        User_Request userRequest = new User_Request();
        userRequest.addPlaylistItems(SpotifySession.getInstance().getPlaylist_id(), clientCredentialsRequest.getPlaylist().getTotalItems(), clientCredentialsRequest.getTrack().getId(), false);
        clientCredentialsRequest.getPlaylist(SpotifySession.getInstance().getPlaylist_id());
        showPlaylistDetails.setText("Current Playlist\n" + clientCredentialsRequest.getPlaylist().getName() +
                "\n" + clientCredentialsRequest.getPlaylist().getDescription() +
                "\nSize: " + clientCredentialsRequest.getPlaylist().getTotalItems());
    }

    @FXML
    protected void OnPageSearchButton(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.SearchTermSelector(pageSearchField, pageSearchField.getText());
    }

    private String ExtractURL(String track_uri) {
        if (track_uri.isEmpty()) return "";
        StringBuilder stringBuilder = new StringBuilder();
        boolean copy = false;
        for (int i = track_uri.length() - 1; i >= 0; i--) {
            if (track_uri.charAt(i) == '/') {
                copy = false;
            }
            if (copy) {
                stringBuilder.append(track_uri.charAt(i));
                continue;
            }
            if (track_uri.charAt(i) == '?') {
                copy = true;
            }
        }
        StringBuilder reverse = new StringBuilder();
        for (int i = stringBuilder.length() - 1; i >= 0; i--) {
            reverse.append(stringBuilder.charAt(i));
        }
        return reverse.toString();
    }

    @FXML
    protected void SearchItem(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (searchTrackField.getText().isEmpty()) {
            foundTrackArea.setText("Track not found!");
            return;
        }
        if (searchTrackField.getText().contains("https://")) {
            String decodedString = ExtractURL(searchTrackField.getText());
            Logger.INFO.Log("Decoded String: " + decodedString, false);
            clientCredentialsRequest.getTrackInformation(decodedString);
        } else {
            clientCredentialsRequest.getTrackInformation(searchTrackField.getText());
        }
        foundTrackArea.setText("Found\n" + clientCredentialsRequest.getTrack().getName() +
                "\nby " + clientCredentialsRequest.getTrack().getArtist());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logger.INFO.Log("Init");
        SceneActions.GetCurrentStage().setOnCloseRequest(e -> {
            if (SceneActions.StopBackgroundHTTPThread()) {
                Logger.INFO.Log("Closing from add item page... Reason: " + e.getEventType());
                System.exit(0);
            }
            Logger.ERROR.Log("Failed to close session.");
            System.exit(1);
        });
//        if (SpotifySession.getInstance().getAccess_token() == null) {
//            Logger.INFO.Log((SpotifySession.getInstance().getRefresh_token() == null ? "Refresh token is null." : "All OK!"));
//            if (SpotifySession.getInstance().getRefresh_token() != null) {
//                User_Access_Token userAccessToken = new User_Access_Token();
//                userAccessToken.refresh_token_with_User_Token();
//            }
//        }
//        if (SpotifySession.getInstance().getPlaylist_id() != null) {
//            clientCredentialsRequest.getPlaylist(SpotifySession.getInstance().getPlaylist_id());
//            showPlaylistDetails.setText("Current Playlist\n" + clientCredentialsRequest.getPlaylist().getName() +
//                    "\n" + clientCredentialsRequest.getPlaylist().getDescription() +
//                    "\nSize: " + clientCredentialsRequest.getPlaylist().getTotalItems());
//        }
        try {
            Thread token = new Thread(new HTTPWorker());
            token.start();
            GetPlaylistWorker getPlaylistWorker = new GetPlaylistWorker();
            Thread playlistThread = new Thread(getPlaylistWorker);
            playlistThread.start();
            //TODO avoid join instead use notification service to update text
//            playlistThread.join();

            showPlaylistDetails.setText("Current Playlist\n" + getPlaylistWorker.getPlaylist().getName() +
                    "\n" + getPlaylistWorker.getPlaylist().getDescription() +
                    "\nSize: " + getPlaylistWorker.getPlaylist().getTotalItems());
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }
}
