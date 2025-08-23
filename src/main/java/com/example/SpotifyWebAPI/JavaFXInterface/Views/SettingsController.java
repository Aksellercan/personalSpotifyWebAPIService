package com.example.SpotifyWebAPI.JavaFXInterface.Views;

import com.example.SpotifyWebAPI.JavaFXInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.FileUtil;
import com.example.SpotifyWebAPI.Tools.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Settings GUI
 */
public class SettingsController {

    @FXML
    protected void MigrateToYAML(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        FileUtil.MigrateToYAML();
    }

    @FXML
    protected void ToggleColouredOutput(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (Logger.getColouredOutput()) {
            Logger.setColouredOutput(false);
        } else {
            Logger.setColouredOutput(true);
        }
        Logger.INFO.Log("Logger: ColouredOutput="+Logger.getColouredOutput(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML
    protected void ToggleDebugOutput(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (Logger.getDebugOutput()) {
            Logger.setDebugOutput(false);
        } else {
            Logger.setDebugOutput(true);
        }
        Logger.INFO.Log("Logger: DebugOutput="+Logger.getDebugOutput(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML
    protected void SaveConfig(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        FileUtil.WriteConfig();
    }

    @FXML
    protected void GoToMainMenu(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.ChangeScene("PrimaryPage");
    }
}
