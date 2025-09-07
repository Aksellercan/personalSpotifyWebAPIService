package com.example.SpotifyWebAPI.JavaFXInterface.Views;

import com.example.SpotifyWebAPI.HTTP.SaveHTTPState;
import com.example.SpotifyWebAPI.JavaFXInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Webview Controller
 */
public class WebviewController implements Initializable {
    @FXML
    private WebView webviewBox;

    /**
     * Changes scene to the previous scene
     * @param event ActionEvent
     */
    @FXML
    private void HandleButtonPress(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.ChangeScene("PrimaryPage");
    }

    /**
     * WebEngine setup
     * @param location  URL Location
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            WebEngine e = webviewBox.getEngine();
            webviewBox.setContextMenuEnabled(true);
            Logger.DEBUG.Log("Webkit, is Javascript enabled? " + (e.isJavaScriptEnabled() ? "Yes": "No"));
            if (SaveHTTPState.ContainsServer("Fallback")) {
                Logger.DEBUG.Log("Server link assigned: " + SaveHTTPState.getServer("Fallback").GetServerSocket().getLocalPort());
                e.load("http://127.0.0.1:" + SaveHTTPState.getServer("Fallback").GetServerSocket().getLocalPort());
            }
        } catch (Exception ex) {
            Logger.CRITICAL.LogException(ex, "Webview Error!");
        }
    }
}
