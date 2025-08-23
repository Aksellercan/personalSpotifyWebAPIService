package com.example.SpotifyWebAPI.JavaFXInterface.Views;

import com.example.SpotifyWebAPI.JavaFXInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.JavaFXInterface.SavedScene;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import static com.example.SpotifyWebAPI.Main.fileUtil; //temp

public class SettingsController {
    private SavedScene stage = SavedScene.getInstance();
    @FXML private TextArea inputUser_Id;

    @FXML protected void MigrateToYAML(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        fileUtil.MigrateToYAML();
    }

    @FXML protected void ToggleColouredOutput(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (Logger.getColouredOutput()) {
            Logger.setColouredOutput(false);
        } else {
            Logger.setColouredOutput(true);
        }
        Logger.INFO.Log("Logger: ColouredOutput="+Logger.getColouredOutput(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML protected void ToggleDebugOutput(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (Logger.getDebugOutput()) {
            Logger.setDebugOutput(false);
        } else {
            Logger.setDebugOutput(true);
        }
        Logger.INFO.Log("Logger: DebugOutput="+Logger.getDebugOutput(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML protected void SaveConfig(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        fileUtil.WriteConfig();
    }

    @FXML protected void GoToMainMenu(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        stage = SavedScene.getInstance();
        if (stage.getScene() == null) {
            return;
        }
        Parent root = SceneActions.setFXMLFile("PrimaryPage");
        if (root == null) {
            Logger.CRITICAL.Log("FXML root is null");
            return;
        }
        Scene window = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        SceneActions.setStyleSheet(window, "PrimaryPage");
        stage.getScene().setScene(window);
    }
}
