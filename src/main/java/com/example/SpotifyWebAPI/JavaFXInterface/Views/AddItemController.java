package com.example.SpotifyWebAPI.JavaFXInterface.Views;

import com.example.SpotifyWebAPI.JavaFXInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AddItemController {

    @FXML
    protected void GoToMainMenu(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.ChangeScene("PrimaryPage");
    }
}
