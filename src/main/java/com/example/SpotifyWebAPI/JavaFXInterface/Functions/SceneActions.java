package com.example.SpotifyWebAPI.JavaFXInterface.Functions;

import com.example.SpotifyWebAPI.Tools.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public final class SceneActions {
    private SceneActions() {}

    public static void setStyleSheet(Scene scene, String styleSheetName) {
        try {
            scene.getStylesheets().add(Scene.class.getResource("/Styles/" + styleSheetName + ".css").toExternalForm());
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Cannot load stylesheet");
        }
    }

    public static Parent setFXMLFile(String fxmlFilename) {
        try {
            return FXMLLoader.load(Scene.class.getResource("/Layouts/" + fxmlFilename + ".fxml"));
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Cannot load FXML");
            return null;
        }
    }
}
