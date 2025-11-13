package com.example.SpotifyWebAPI.Run_Modes.GraphicalInterface.Functions;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.HTTP.HTTPServer;
import com.example.SpotifyWebAPI.HTTP.SaveHTTPState;
import com.example.SpotifyWebAPI.Tokens.User_Access_Token;
import com.example.SpotifyWebAPI.Tools.Files.Objects.SearchItem;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import com.example.SpotifyWebAPI.Tools.Logger.LoggerSettings;
import javafx.event.EventHandler;
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
    /**
     * Saved stage
     */
    private static Stage currentStage;
    /**
     * Default stylesheet to use
     */
    private static String defaultStylesheet;
    /**
     * FXML Files list
     */
    private static final ArrayList<SearchItem> FXMLPages = new ArrayList<>();
    private static AtomicInteger handlerCount = new AtomicInteger(0);

    private SceneActions() {
    }

    /**
     * Searches inside Resources/Layouts for fxml files and puts it in List
     */
    /*
    Only works when run from IDE at the moment
     */
    public static void LoadPagesToArray() {
        try {
            URL getFolder = Scene.class.getResource("/Layouts/");
            if (getFolder == null) throw new NullPointerException("Folder can't be read");
            BufferedReader br = new BufferedReader(new InputStreamReader(getFolder.openStream()));
            File getFilesInLayouts = new File(getFolder.getPath());
            Logger.INFO.Log("Reading file contents...");
            for (File addToArray : Objects.requireNonNull(getFilesInLayouts.listFiles())) {
                FXMLPages.add(new SearchItem(addToArray.getName(), addToArray.getPath()));
            }
            removeExtension();
            AddCommands();
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Can't read pages");
        }
    }

    private static void AddCommands() {
        FXMLPages.add(new SearchItem("quit", true));
        FXMLPages.add(new SearchItem("token", true));
        FXMLPages.add(new SearchItem("debug", true));
        FXMLPages.add(new SearchItem("quiet", true));
        FXMLPages.add(new SearchItem("main", true));
    }

    /**
     * Removes file extension from file
     */
    private static void removeExtension() {
        StringBuilder sb = new StringBuilder();
        for (SearchItem file : FXMLPages) {
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

    /**
     * Gets current stage
     *
     * @return current stage
     */
    public static Stage GetCurrentStage() {
        return currentStage;
    }

    /**
     * Set Stage object
     *
     * @param stage JavaFX Stage Object
     */
    public static void SetCurrentStage(Stage stage) {
        currentStage = stage;
    }

    /**
     * Set stylesheet to use for every Scene
     *
     * @param stylesheet Stylesheet filename
     */
    public static void SetDefaultStylesheet(String stylesheet) {
        defaultStylesheet = stylesheet;
    }

    /**
     * Change Scene with just FXML filename. Uses default stylesheet.
     *
     * @param sceneName FXML filename
     */
    public static void ChangeScene(String sceneName) {
        if (defaultStylesheet == null) {
            Logger.ERROR.Log("Default Stylesheet not set!");
            return;
        }
        ChangeScene(sceneName, defaultStylesheet);
    }

    /**
     * Change Scene
     *
     * @param sceneName       FXML filename
     * @param sceneStylesheet Stylesheet filename
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
     *
     * @param scene          Scene to apply stylesheet for
     * @param styleSheetName Name of the stylesheet
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
     *
     * @param fxmlFilename FXML File name
     * @return Loaded FXML file as Parent object, if it fails to find file returns null
     */
    public static Parent setFXMLFile(String fxmlFilename) throws Exception {
        URL getFXML = Scene.class.getResource("/Layouts/" + fxmlFilename + ".fxml");
        if (getFXML == null) {
            throw new NullPointerException("FXML file " + fxmlFilename + ".fxml not found");
        }
        return FXMLLoader.load(getFXML);
    }

    /**
     * Stops the background HTTP server thread by changing volatile boolean and interrupting thread
     *
     * @return success as boolean
     */
    public static boolean StopBackgroundHTTPThread() {
        try {
            HTTPServer httpServer = SaveHTTPState.getServer("Fallback");
            if (httpServer != null && httpServer.StopServer()) {
                HTTPConnection httpConnection = new HTTPConnection();
                HttpURLConnection http = httpConnection.connectHTTP("http://127.0.0.1:" + SaveHTTPState.getServer("Fallback").GetServerSocket().getLocalPort(), "GET");
                http.connect();
            }
            return true;
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Failed to stop HTTP thread");
            return false;
        }
    }

    /**
     * Search handler, if there are more than 1 result it creates an event handler to choose one of the results. Up key to go up the array, Down key to go down the array, Enter key to choose and change scene and Backspace key to clear search field and stop the event handler
     *
     * @param pageSearchField JavaFX TextField object
     * @param searchTerm      Search term
     */
    public static void SearchTermSelector(TextField pageSearchField, String searchTerm) {
        Logger.DEBUG.Log("Enter search handler", false, true);
        if (searchTerm.isEmpty()) {
            Logger.DEBUG.Log("Search field is empty", false);
            return;
        }
        if (handlerCount.getAndIncrement() <= 1) {
            SearchItem[] returnedList = SearchAlgorithm(searchTerm);
            Logger.DEBUG.Log("Found " + returnedList.length + (returnedList.length > 1 ? " items" : " item"));
            AtomicInteger index = new AtomicInteger();
            AtomicBoolean ignoreFirstEnter = new AtomicBoolean();
            ignoreFirstEnter.set(true);
            index.set(0);
            if (returnedList.length > 1) {
                pageSearchField.setText(returnedList.length + " results - " + returnedList[0]);
            }
            if (returnedList.length != 0) {
                EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {
                        switch (keyEvent.getCode()) {
                            case UP:
                                if (index.get() == returnedList.length) index.set(0);
                                if (index.get() == 0) break;
                                index.set(index.get() - 1);
                                pageSearchField.setText(returnedList[index.get()].getFileName());
                                Logger.DEBUG.Log("Index = " + index.get() + " Item = " + returnedList[index.get()]);
                                break;
                            case DOWN:
                                if (index.get() == returnedList.length - 1) break;
                                index.set(index.get() + 1);
                                pageSearchField.setText(returnedList[index.get()].getFileName());
                                Logger.DEBUG.Log("Index = " + index.get() + " Item = " + returnedList[index.get()]);
                                break;
                            case BACK_SPACE:
                                Logger.DEBUG.Log("Backspace pressed, leaving handler...");
                                pageSearchField.setText("");
                                pageSearchField.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                                ClearSearch();
                                return;
                            case ENTER:
                                Logger.DEBUG.Log("Enter pressed, leaving handler...");
                                if (!ignoreFirstEnter.get()) {
                                    Logger.DEBUG.Log("Chosen " + returnedList[index.get()]);
                                    if (!returnedList[index.get()].isCommand())
                                        ChangeScene(returnedList[index.get()].getFileName());
                                    else
                                        ExecuteCommands(returnedList[index.get()].getFileName());
                                    pageSearchField.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                                    pageSearchField.setText("");
                                    ClearSearch();
                                }
                                if (returnedList.length == 1) {
                                    Logger.DEBUG.Log("Changing to " + returnedList[index.get()]);
                                    if (!returnedList[index.get()].isCommand())
                                        ChangeScene(returnedList[index.get()].getFileName());
                                    else
                                        ExecuteCommands(returnedList[index.get()].getFileName());
                                    pageSearchField.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                                    pageSearchField.setText("");
                                    ClearSearch();
                                }
                                ignoreFirstEnter.set(!ignoreFirstEnter.get());
                        }
                    }
                };
                pageSearchField.addEventHandler(KeyEvent.KEY_PRESSED, handler);
            }
        }
    }

    private static void ExecuteCommands(String command) {
        Logger.DEBUG.Log(String.format("execute called %d times", handlerCount.get()), false, true);
        switch (command) {
            case "quit":
                System.exit(0);
                break;
            case "token":
                User_Access_Token userAccessToken = new User_Access_Token();
                userAccessToken.refresh_token_with_User_Token();
                break;
            case "debug":
                LoggerSettings.setDebugOutput(!LoggerSettings.getDebugOutput());
                break;
            case "quiet":
                LoggerSettings.setQuiet(!LoggerSettings.getQuiet());
                Logger.INFO.Log("quiet set to " + LoggerSettings.getQuiet(), false, true);
                break;
            case "main":
                ChangeScene("PrimaryPage");
                break;
            default:
                Logger.DEBUG.Log("Unknown command");
        }
    }

    /**
     * Clears "correctChars" attribute from SearchItem object
     */
    private static void ClearSearch() {
        Logger.DEBUG.Log("Clearing search ratings...");
        for (SearchItem searchItem : FXMLPages) {
            searchItem.setCorrectChars(0);
        }
    }

    /**
     * Searches entered term in SearchItem type array by incrementing "correctChars" attribute for Strings that match the characters in search term
     *
     * @param searchTerm Search term
     * @return String array of matching Files as Strings
     */
    private static SearchItem[] SearchAlgorithm(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        List<SearchItem> results = new ArrayList<>();
        for (SearchItem files : FXMLPages) {
            String pages = files.getFileName().toLowerCase();
            for (int i = 0; i < pages.length(); i++) {
                if (i == searchTerm.length()) break;
                if (searchTerm.charAt(i) == pages.charAt(i)) {
                    files.setCorrectChars(files.getCorrectChars() + 1);
                } else {
                    break;
                }
            }
            Logger.DEBUG.Log("Correct char count: " + files.getCorrectChars() + " :: " + files);
            if (files.getCorrectChars() != 0) {
                Logger.DEBUG.Log("found " + files.getFileName());
                results.add(files);
            }
        }
        if (results.size() == 2) {
            SearchItem previous = null;
            for (SearchItem found : results) {
                if (previous != null) {
                    if (previous.getCorrectChars() == found.getCorrectChars()) {
                        Logger.DEBUG.Log("Two most likely results: " + previous.getFileName() + " and " + found.getFileName());
                        return new SearchItem[]{ previous, found };
                    }
                    SearchItem mostLikely = (previous.getCorrectChars() > found.getCorrectChars()) ? previous : found;
                    Logger.DEBUG.Log("Most likely result: " + mostLikely.getFileName());
                    return new SearchItem[]{ mostLikely };
                } else {
                    previous = found;
                }
            }
        }
        if (results.size() > 2) {
            SearchItem[] returnArray = new SearchItem[results.size()];
            int index = 0;
            for (SearchItem file : results) {
                returnArray[index] = file;
                index++;
            }
            return returnArray;
        }
        return new SearchItem[]{ results.get(0) };
    }
}
