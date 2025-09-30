package com.example.SpotifyWebAPI.Objects;

/**
 * Object to hold Program settings
 */
public final class ProgramOptions {
    private static boolean AUTO_MODE;
    private static boolean CHANGES_SAVED = true;
    private static boolean LAUNCH_GUI = true;
    private static int playlist_limit = 120;

    private ProgramOptions() {}

    public static boolean LAUNCH_GUI() {
        return LAUNCH_GUI;
    }
    public static int getPlaylist_limit() {
        return playlist_limit;
    }
    public static boolean isAutoMode() {
        return AUTO_MODE;
    }
    public static boolean isChangesSaved() {
        return CHANGES_SAVED;
    }

    public static void setAutoMode(boolean autoMode) {
        AUTO_MODE = autoMode;
    }
    public static void setPlaylist_limit(int newLimit) {
        playlist_limit = newLimit;
    }
    public static void setChangesSaved(boolean changesSaved) {
        CHANGES_SAVED = changesSaved;
    }
    public static void setLAUNCH_GUI(boolean launch_guic) {
        LAUNCH_GUI = launch_guic;
    }
}
