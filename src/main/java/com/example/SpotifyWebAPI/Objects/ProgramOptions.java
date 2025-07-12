package com.example.SpotifyWebAPI.Objects;

public final class ProgramOptions {
    private static ProgramOptions instance;
    private boolean AUTO_MODE;
    private boolean DEBUG_MODE;
    private boolean CHANGES_SAVED = true;
    private String playlist_id;//test

    private ProgramOptions() {}

    public static ProgramOptions getInstance() {
        if (instance == null) {
            instance = new ProgramOptions();
        }
        return instance;
    }

    public boolean isAutoMode() {
        return AUTO_MODE;
    }
    public boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public boolean isChangesSaved() {
        return CHANGES_SAVED;
    }
    public String getPlaylist_id() {
        return playlist_id;
    }

    public void setAutoMode(boolean autoMode) {
        this.AUTO_MODE = autoMode;
    }
    public void setDebugMode(boolean debugMode) {
        this.DEBUG_MODE = debugMode;
    }
    public void setChangesSaved(boolean changesSaved) {
        this.CHANGES_SAVED = changesSaved;
    }
    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }
}
