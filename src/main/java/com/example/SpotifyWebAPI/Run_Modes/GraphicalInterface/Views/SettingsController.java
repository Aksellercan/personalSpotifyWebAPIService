package com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Views;

import com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Functions.SceneActions;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.example.SpotifyWebAPI.Tools.Files.Parsers.YAMLParser;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Settings GUI
 */
public class SettingsController implements Initializable {
    @FXML
    private TextField inputUser_id;
    @FXML
    private TextField inputClient_id;
    @FXML
    private TextField inputClient_secret;
    @FXML
    private TextField inputRefresh_token;
    @FXML
    private TextField inputPlaylist_id;
    @FXML
    private TextField inputRedirect_uri;
    @FXML
    private ProgressBar SaveConfigProgressBar;
    @FXML
    private TextField pageSearchField;

    @FXML
    protected void MigrateToYAML(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SaveConfigProgressBar.setProgress(0);
        YAMLParser.MigrateToYAML();
        SaveConfigProgressBar.setProgress(100);
    }

    @FXML
    protected void ToggleVerboseLogFile(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        Logger.setVerboseLogFile(!Logger.getVerboseLogFile());
        Logger.INFO.Log("Logger: verbose_log_file=" + Logger.getVerboseLogFile(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML
    protected void ToggleCLI(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        ProgramOptions.setLAUNCH_GUI(!ProgramOptions.LAUNCH_GUI());
        Logger.INFO.Log("ProgramOptions: launch_gui=" + ProgramOptions.LAUNCH_GUI(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML
    protected void ToggleAutoMode(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        ProgramOptions.setAutoMode(!ProgramOptions.isAutoMode());
        Logger.INFO.Log("ProgramOptions: AutoMode=" + ProgramOptions.isAutoMode(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML
    protected void ToggleColouredOutput(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        Logger.setColouredOutput(!Logger.getColouredOutput());
        Logger.INFO.Log("Logger: ColouredOutput=" + Logger.getColouredOutput(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML
    protected void ToggleStackTraces(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        Logger.setEnableStackTraces(!Logger.getEnableStackTraces());
        Logger.INFO.Log("Logger: enable_stack_traces=" + Logger.getEnableStackTraces(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML
    protected void ToggleDebugOutput(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        Logger.setDebugOutput(!Logger.getDebugOutput());
        Logger.INFO.Log("Logger: DebugOutput=" + Logger.getDebugOutput(), false);
        ProgramOptions.setChangesSaved(false);
    }

    @FXML
    protected void SaveConfig(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        if (CheckSettingsInput()) {
            SaveConfigProgressBar.setProgress(75);
            ProgramOptions.setChangesSaved(false);
            SaveConfigProgressBar.setProgress(75);
        }
        YAMLParser.MapAndWriteConfig();
        SaveConfigProgressBar.setProgress(100);
        inputUser_id.setText("");
        inputClient_id.setText("");
        inputClient_secret.setText("");
        inputRedirect_uri.setText("");
        inputRefresh_token.setText("");
        inputPlaylist_id.setText("");
    }

    @FXML
    protected void GoToMainMenu(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        ProgramOptions.setChangesSaved(false);
        YAMLParser.MapAndWriteConfig();
//        JSONParser.MapAndWriteConfig();
        SceneActions.ChangeScene("PrimaryPage");
    }

    private boolean CheckSettingsInput() {
        boolean change = false;
        if (!inputUser_id.getText().isEmpty()) {
            SpotifySession.getInstance().setUser_id(inputUser_id.getText());
            change = true;
        }
        if (!inputClient_id.getText().isEmpty()) {
            SpotifySession.getInstance().setClient_id(inputClient_id.getText());
            change = true;
        }
        if (!inputClient_secret.getText().isEmpty()) {
            SpotifySession.getInstance().setClient_secret(inputClient_secret.getText());
            change = true;
        }
        if (!inputRefresh_token.getText().isEmpty()) {
            SpotifySession.getInstance().setRefresh_token(inputRefresh_token.getText());
            change = true;
        }
        if (!inputPlaylist_id.getText().isEmpty()) {
            SpotifySession.getInstance().setPlaylist_id(inputPlaylist_id.getText());
            change = true;
        }
        if (!inputRedirect_uri.getText().isEmpty()) {
            SpotifySession.getInstance().setRedirect_uri(inputRedirect_uri.getText());
            change = true;
        }
        return change;
    }

    @FXML
    protected void OnPageSearchButton(ActionEvent event) {
        Logger.DEBUG.Log("Event: " + event.toString());
        SceneActions.SearchTermSelector(pageSearchField, pageSearchField.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SceneActions.GetCurrentStage().setOnCloseRequest(e -> {
            ProgramOptions.setChangesSaved(false);
            YAMLParser.MapAndWriteConfig();
            if (SceneActions.StopBackgroundHTTPThread()) {
                Logger.INFO.Log("Closing from settings page... Reason: " + e.getEventType());
                System.exit(0);
            }
            Logger.ERROR.Log("Failed to close session.");
            System.exit(1);
        });
    }
}
