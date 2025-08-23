package com.example.SpotifyWebAPI.JavaFXInterface.Functions;

import com.example.SpotifyWebAPI.Tools.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

/**
 * Shared functions used by JavaFX Controllers
 */
public final class SceneActions {
    private static Stage currentStage;
    private static String defaultStylesheet;

    private SceneActions() {}

    public static void SetCurrentStage(Stage stage) {
        currentStage = stage;
    }

    public static void SetDefaultStylesheet(String stylesheet) {
        defaultStylesheet = stylesheet;
    }

    public static void ChangeScene(String sceneName) {
        if (defaultStylesheet.isEmpty()){
            Logger.WARN.Log("Default Stylesheet not set!");
            return;
        }
        ChangeScene(sceneName, defaultStylesheet);
    }

        public static void ChangeScene(String sceneName, String sceneStylesheet) {
        if (currentStage == null) {
            Logger.CRITICAL.Log("Current scene is null");
            return;
        }
        Parent root = SceneActions.setFXMLFile(sceneName);
        if (root == null) {
            Logger.CRITICAL.Log("FXML root is null");
            return;
        }
        Scene window = new Scene(root, currentStage.getWidth(), currentStage.getHeight());
        SceneActions.setStyleSheet(window, sceneStylesheet);
        currentStage.setScene(window);
    }
    /**
     * Sets stylesheet for the scene
     * @param scene Scene to apply stylesheet for
     * @param styleSheetName    Name of the stylesheet
     */
    public static void setStyleSheet(Scene scene, String styleSheetName) {
        try {
            URL getStyleSheet = Scene.class.getResource("/Styles/" + styleSheetName + ".css");
            if (getStyleSheet == null) {
                throw new NullPointerException("Style sheet resource " + styleSheetName + ".css not found");
            }
            scene.getStylesheets().add(getStyleSheet.toExternalForm());
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Cannot load stylesheet");
        }
    }

    /**
     * Sets FXML file for a scene
     * @param fxmlFilename  FXML File name
     * @return  Loaded FXML file as Parent object, if it fails to find file returns null
     */
    public static Parent setFXMLFile(String fxmlFilename) {
        try {
            URL getFXML = Scene.class.getResource("/Layouts/" + fxmlFilename + ".fxml");
            if (getFXML == null) {
                throw  new NullPointerException("FXML file " + fxmlFilename + ".fxml not found");
            }
            return FXMLLoader.load(getFXML);
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Cannot load FXML");
            return null;
        }
    }
}
