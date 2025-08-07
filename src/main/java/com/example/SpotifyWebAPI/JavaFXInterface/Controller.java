package com.example.SpotifyWebAPI.JavaFXInterface;

import com.example.SpotifyWebAPI.JavaFXInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.Tools.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private SavedScene savedScene = SavedScene.getInstance();
    @FXML private WebView webviewBox;

    @FXML
    private void HandleButtonPress(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        savedScene = SavedScene.getInstance();
        if (savedScene.getScene() == null) {
            return;
        }
        Parent root = SceneActions.setFXMLFile("PrimaryPage");
        if (root == null) {
            Logger.CRITICAL.Log("FXML root is null");
            return;
        }
        Scene window = new Scene(root, savedScene.getScene().getWidth(), savedScene.getScene().getHeight());
        SceneActions.setStyleSheet(window, "PrimaryPage");
        savedScene.getScene().setScene(window);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            WebEngine e = webviewBox.getEngine();
            webviewBox.setContextMenuEnabled(true);
            Logger.DEBUG.Log("Webkit, is Javascript enabled? " + (e.isJavaScriptEnabled() ? "Yes": "No"));
            e.load("http://127.0.0.1:8080"); //placeholder
        } catch (Exception ex) {
            Logger.CRITICAL.LogException(ex, "Webkit Error!");
        }
    }
}
