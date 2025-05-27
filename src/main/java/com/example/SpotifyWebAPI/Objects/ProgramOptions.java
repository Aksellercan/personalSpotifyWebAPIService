package com.example.SpotifyWebAPI.Objects;

public class ProgramOptions {
    private boolean AUTO_MODE;
    private boolean DEBUG_MODE;
    private boolean CHANGES_SAVED = true;
    private boolean TEST_MODE;
    private String playlist_id;//test

    public ProgramOptions() {}

    public boolean isAutoMode() {
        return AUTO_MODE;
    }
    public boolean isDebugMode() {
        return DEBUG_MODE;
    }
    public boolean isTestMode() {
        return TEST_MODE;
    }
    public boolean isChangesSaved() {
        return CHANGES_SAVED;
    }
    public String getPlaylist_id() {
        return playlist_id;
    }

    public void setTestMode(boolean testMode) {
        this.TEST_MODE = testMode;
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
