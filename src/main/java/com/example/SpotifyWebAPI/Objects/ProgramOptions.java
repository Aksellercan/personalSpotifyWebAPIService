package com.example.SpotifyWebAPI.Objects;

/**
 * Object to hold Program settings
 */
public final class ProgramOptions {
    private static ProgramOptions instance;
    private boolean AUTO_MODE;
    private boolean CHANGES_SAVED = true;
    private boolean LAUNCH_GUI = true;

    private ProgramOptions() {}

    public static ProgramOptions getInstance() {
        if (instance == null) {
            instance = new ProgramOptions();
        }
        return instance;
    }

    public boolean LAUNCH_GUI() {
        return LAUNCH_GUI;
    }
    public boolean isAutoMode() {
        return AUTO_MODE;
    }
    public boolean isChangesSaved() {
        return CHANGES_SAVED;
    }

    public void setAutoMode(boolean autoMode) {
        this.AUTO_MODE = autoMode;
    }
    public void setChangesSaved(boolean changesSaved) {
        this.CHANGES_SAVED = changesSaved;
    }
    public void setLAUNCH_GUI(boolean LAUNCH_GUI) {
        this.LAUNCH_GUI = LAUNCH_GUI;
    }
}
