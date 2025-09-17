package com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Functions;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.HTTP.HTTPServer;
import com.example.SpotifyWebAPI.HTTP.SaveHTTPState;
import com.example.SpotifyWebAPI.Tools.Files.Objects.FileSearch;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Shared functions used by JavaFX Controllers
 */
public final class SceneActions {
    private static Stage currentStage;
    private static String defaultStylesheet;
    private static ArrayList<FileSearch> FXMLPages = new ArrayList<>();

    private SceneActions() {}

    public static void LoadPagesToArray() {
        File getStyleSheet = new File(Scene.class.getResource("/Layouts/").getPath());
        for (File addToArray : getStyleSheet.listFiles()) {
            FXMLPages.add(new FileSearch(addToArray.getName(), addToArray.getPath()));
        }
        removeExtension();
        Logger.DEBUG.Log("File: " + FXMLPages.toString());
    }

    private static void removeExtension() {
        StringBuilder sb = new StringBuilder();
        for (FileSearch file : FXMLPages) {
            String name = file.getFileName();
            for (int i = 0; i < name.length(); i++) {
                if (name.charAt(i) == '.') {
                    file.setFileName(sb.toString());
                    sb.setLength(0);
                    break;
                }
                sb.append(name.charAt(i));
            }
        }
    }

    public static Stage GetCurrentStage() {
        return currentStage;
    }

    /**
     * Set Stage object
     * @param stage JavaFX Stage Object
     */
    public static void SetCurrentStage(Stage stage) {
        currentStage = stage;
    }

    /**
     * Set stylesheet to use for every Scene
     * @param stylesheet    Stylesheet filename
     */
    public static void SetDefaultStylesheet(String stylesheet) {
        defaultStylesheet = stylesheet;
    }

    /**
     * Change Scene with just FXML filename. Uses default stylesheet.
     * @param sceneName FXML filename
     */
    public static void ChangeScene(String sceneName) {
        if (defaultStylesheet == null){
            Logger.ERROR.Log("Default Stylesheet not set!");
            return;
        }
        ChangeScene(sceneName, defaultStylesheet);
    }

    /**
     * Change Scene
     * @param sceneName FXML filename
     * @param sceneStylesheet   Stylesheet filename
     */
    public static void ChangeScene(String sceneName, String sceneStylesheet) {
        try {
            if (currentStage == null) {
                Logger.CRITICAL.Log("Current scene is null");
                return;
            }
            Parent root = setFXMLFile(sceneName);
            if (root == null) {
                Logger.CRITICAL.Log("FXML root is null");
                return;
            }
            Scene window = new Scene(root, currentStage.getWidth(), currentStage.getHeight());
            setStyleSheet(window, sceneStylesheet);
            currentStage.setScene(window);
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Failed to change scene");
        }
    }

    /**
     * Sets stylesheet for the scene
     * @param scene Scene to apply stylesheet for
     * @param styleSheetName    Name of the stylesheet
     */
    public static void setStyleSheet(Scene scene, String styleSheetName) throws NullPointerException {
        URL getStyleSheet = Scene.class.getResource("/Styles/" + styleSheetName + ".css");
        if (getStyleSheet == null) {
            throw new NullPointerException("Style sheet resource " + styleSheetName + ".css not found");
        }
        scene.getStylesheets().add(getStyleSheet.toExternalForm());
    }

    /**
     * Sets FXML file for a scene
     * @param fxmlFilename  FXML File name
     * @return  Loaded FXML file as Parent object, if it fails to find file returns null
     */
    public static Parent setFXMLFile(String fxmlFilename) throws Exception {
        URL getFXML = Scene.class.getResource("/Layouts/" + fxmlFilename + ".fxml");
        if (getFXML == null) {
            throw new NullPointerException("FXML file " + fxmlFilename + ".fxml not found");
        }
        return FXMLLoader.load(getFXML);
    }

    public static boolean StopBackgroundHTTPThread() {
        try {
            HTTPServer httpServer = SaveHTTPState.getServer("Fallback");
            if (httpServer != null && httpServer.StopServer()) {
                HttpURLConnection http = HTTPConnection.connectHTTP("http://127.0.0.1:" + SaveHTTPState.getServer("Fallback").GetServerSocket().getLocalPort(), "GET");
                http.connect();
            }
            return true;
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Failed to stop HTTP thread");
            return false;
        }
    }

    public static void SearchPage(String searchTerm) {
        if (searchTerm.isEmpty()) return;
        ChangeScene(SearchAlgorithm(searchTerm)[0]);
        for (FileSearch fileSearch : FXMLPages) {
            fileSearch.setCorrectChars(0);
        }
    }

    private static String[] SearchAlgorithm(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        for (FileSearch files : FXMLPages) {
            String pages =  files.getFileName().toLowerCase();
            for (int i = 0; i < pages.length(); i++) {
                if (i == searchTerm.length()) break;
                if (searchTerm.charAt(i) == pages.charAt(i)) {
                    files.setCorrectChars(files.getCorrectChars()+1);
                } else {
                    break;
                }
            }
            Logger.DEBUG.Log("Correct char count: " + files.getCorrectChars() + "\nstring = " + files.toString());
        }
        int highestCorrectCount = 0;
        for (FileSearch file : FXMLPages) {
            if (highestCorrectCount < file.getCorrectChars()) {
                Logger.DEBUG.Log("Most likely contestant: " + file.getFileName());
                return new String[] {file.getFileName()};
            }
            highestCorrectCount = file.getCorrectChars();
        }
        return new String[] {};
    }
}
