package com.example.SpotifyWebAPI.JavaFXInterface;

import javafx.stage.Stage;

public final class SavedScene {
    private static SavedScene instance;
    private Stage scene;

    private SavedScene() {}

    public static SavedScene getInstance() {
        if (instance == null) {
            instance = new SavedScene();
        }
        return instance;
    }

    //set
    public void setScene(Stage scene) {
        this.scene = scene;
    }

    public Stage getScene() {
        return scene;
    }
}
