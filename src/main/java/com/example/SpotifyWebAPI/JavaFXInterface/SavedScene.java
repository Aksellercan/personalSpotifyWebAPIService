package com.example.SpotifyWebAPI.JavaFXInterface;

import javafx.stage.Stage;

/**
 * @deprecated
 * Saves the state of the scene
 */
public final class SavedScene {
    private static SavedScene instance;
    private Stage scene;

    private SavedScene() {}

    /**
     * Returns the instance of the object
     * @return  Scene instance
     */
    public static SavedScene getInstance() {
        if (instance == null) {
            instance = new SavedScene();
        }
        return instance;
    }

    /**
     * Saves the scene
     * @param scene Scene to be saved
     */
    public void setScene(Stage scene) {
        this.scene = scene;
    }

    /**
     * Returns the saved scene
     * @return  Saved scene
     */
    public Stage getScene() {
        return scene;
    }
}
