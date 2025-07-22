package com.example.SpotifyWebAPI.JavaFXInterface;

import com.example.SpotifyWebAPI.Tools.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class Controller {
    private SavedScene savedScene = SavedScene.getInstance();

    @FXML
    private void HandleButtonPress(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
    }
}
