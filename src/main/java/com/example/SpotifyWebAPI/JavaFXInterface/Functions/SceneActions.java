package com.example.SpotifyWebAPI.JavaFXInterface.Functions;

import com.example.SpotifyWebAPI.Tools.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.net.URL;

/**
 * Shared functions used by JavaFX Controllers
 */
public final class SceneActions {
    private SceneActions() {}

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
