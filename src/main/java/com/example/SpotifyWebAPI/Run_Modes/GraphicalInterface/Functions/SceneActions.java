package com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Functions;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.HTTP.HTTPServer;
import com.example.SpotifyWebAPI.HTTP.SaveHTTPState;
import com.example.SpotifyWebAPI.Tools.Files.Objects.FileSearch;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared functions used by JavaFX Controllers
 */
public final class SceneActions {
    private static Stage currentStage;
    private static String defaultStylesheet;
    private static final ArrayList<FileSearch> FXMLPages = new ArrayList<>();

    private SceneActions() {}

    public static void LoadPagesToArray() {
        try {
            URL getFolder = Scene.class.getResource("/Layouts/");
            if (getFolder == null) throw new NullPointerException("Folder can't be read");
            BufferedReader br = new BufferedReader(new InputStreamReader(getFolder.openStream()));
            File getFilesInLayouts = new File(getFolder.getPath());
            Logger.INFO.Log("Reading file contents...");
            for (File addToArray : Objects.requireNonNull(getFilesInLayouts.listFiles())) {
                FXMLPages.add(new FileSearch(addToArray.getName(), addToArray.getPath()));
            }
            removeExtension();
            Logger.DEBUG.Log("File: " + FXMLPages.toString());
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Can't read pages");
        }
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

    public static void SearchTermSelector(TextField pageSearchField, String searchTerm) {
        if (searchTerm.isEmpty()) {
            Logger.DEBUG.Log("Search field is empty", false);
            return;
        }
        List<String> returnedList = SearchAlgorithm(searchTerm);
        AtomicInteger index = new AtomicInteger();
        AtomicBoolean ignoreFirstEnter = new AtomicBoolean();
        ignoreFirstEnter.set(true);
        index.set(0);
        if (returnedList.size() > 1) {
            pageSearchField.setText(returnedList.size() + " results");
        }
        if (!returnedList.isEmpty()) {
            pageSearchField.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                switch (e.getCode()) {
                    case UP:
                        if (index.get() == returnedList.size()) index.set(0);
                        if (index.get() == 0) break;
                        index.set(index.get() - 1);
                        break;
                    case DOWN:
                        if (index.get() == returnedList.size() - 1) break;
                        index.set(index.get() + 1);
                        break;
                    case ENTER:
                        if (!ignoreFirstEnter.get()) {
                            ChangeScene(returnedList.get(index.get()));
                        }
                        ignoreFirstEnter.set(!ignoreFirstEnter.get());
                        break;
                }
                pageSearchField.setText(returnedList.get(index.get()));
                Logger.DEBUG.Log("Index = " + index.get() + " Item = " + returnedList.get(index.get()));
            });
            pageSearchField.removeEventHandler(KeyEvent.KEY_PRESSED, e -> {
                Logger.INFO.Log("Closed event handler");
            });
            ClearSearch();
        }
    }

    private static void ClearSearch() {
        for (FileSearch fileSearch : FXMLPages) {
            fileSearch.setCorrectChars(0);
        }
    }

    private static List<String> SearchAlgorithm(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        List<String> results = new ArrayList<>();
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
            Logger.DEBUG.Log("Correct char count: " + files.getCorrectChars() + ", File name: " + files.getFileName());
        }
        for (FileSearch file : FXMLPages) {
            if (file.getCorrectChars() != 0) {
                Logger.DEBUG.Log("found " + file.getFileName());
                results.add(file.getFileName());
            }
        }
        Logger.DEBUG.Log("Found " + results.size() + (results.size() > 1 ? " items" : " item"));
        return results;
    }
}
