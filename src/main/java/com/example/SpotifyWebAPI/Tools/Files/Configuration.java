package com.example.SpotifyWebAPI.Tools.Files;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Objects.Token;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import java.io.File;
import java.io.IOException;

/**
 * Abstract class to share same token array
 */
abstract class Configuration {
    /**
     * Saved Tokens array
     */
    protected static Token[] tokenConfig;
    /**
     * Configuration folder path
     */
    private static final File folderPath = new File("Config");
    /**
     * Specify keys to write when config file is not present (first time launch).
     * Add Entries here to be loaded when config file is non-existent or empty
     */
    protected static Token[] LoadKeys() {
        String[] keys = {
                "client_id",
                "client_secret",
                "redirect_uri",
                "refresh_token",
                "playlist_id",
                "output_debug",
                "auto_mode",
                "user_id",
                "launch_gui",
                "verbose_log_file",
                "coloured_output"
        };
        tokenConfig = new Token[keys.length];
        for (int i = 0; i < tokenConfig.length; i++) {
            tokenConfig[i] = new Token(keys[i], "");
            Logger.DEBUG.Log(tokenConfig[i].toString());
        }
        return tokenConfig;
    }

    /**
     * Map current values with token array to be written or set values using tokenConfig array
     * Add new entries here, with update and normal behaviour
     */
    protected static void MapKeys(boolean update) {
        SpotifySession spotifySession = SpotifySession.getInstance();
        for (Token token : tokenConfig) {
            Logger.DEBUG.Log("Current: " + token.toString());
            switch (token.getKey().replace("\t", "")) {
                case "client_id":
                    if (update) {
                        token.setValue(spotifySession.getClient_id());
                        break;
                    }
                    spotifySession.setClient_id(token.getValue());
                    break;
                case "client_secret":
                    if (update) {
                        token.setValue(spotifySession.getClient_secret());
                        break;
                    }
                    spotifySession.setClient_secret(token.getValue());
                    break;
                case "refresh_token":
                    if (update) {
                        token.setValue(spotifySession.getRefresh_token());
                        break;
                    }
                    spotifySession.setRefresh_token(token.getValue());
                    break;
                case "user_id":
                    if (update) {
                        token.setValue(spotifySession.getUser_id());
                        break;
                    }
                    spotifySession.setUser_id(token.getValue());
                    break;
                case "redirect_uri":
                    if (update) {
                        token.setValue(spotifySession.getRedirect_uri());
                        break;
                    }
                    spotifySession.setRedirect_uri(token.getValue());
                    break;
                case "playlist_id":
                    if (update) {
                        token.setValue(spotifySession.getPlaylist_id());
                        break;
                    }
                    spotifySession.setPlaylist_id(token.getValue());
                    break;
                case "output_debug":
                    if (update) {
                        token.setValue(String.valueOf(Logger.getDebugOutput()));
                        break;
                    }
                    Logger.setDebugOutput(BooleanParse(token.getValue(), false));
                    break;
                case "verbose_log_file":
                    if (update) {
                        token.setValue(String.valueOf(Logger.getVerboseLogFile()));
                        break;
                    }
                    Logger.setVerboseLogFile(BooleanParse(token.getValue(), false));
                    break;
                case "launch_gui":
                    if (update) {
                        token.setValue(String.valueOf(ProgramOptions.LAUNCH_GUI()));
                        break;
                    }
                    ProgramOptions.setLAUNCH_GUI(BooleanParse(token.getValue(), true));
                    break;
                case "auto_mode":
                    if (update) {
                        token.setValue(String.valueOf(ProgramOptions.isAutoMode()));
                        break;
                    }
                    ProgramOptions.setAutoMode(BooleanParse(token.getValue(), false));
                    break;
                case "coloured_output":
                    if (update) {
                        token.setValue(String.valueOf(Logger.getColouredOutput()));
                        break;
                    }
                    Logger.setColouredOutput(BooleanParse(token.getValue(), false));
                    break;
                default:
                    Logger.ERROR.LogSilently("Key \"" + token.getKey() + "\" is invalid");
            }
        }
    }

    /**
     * Checks if directory exists, if it doesn't it creates it and returns the file path
     * @param fileNameWithExtension Name of file to check and create
     * @return  Full path of the file
     * @throws IOException  If creating folder fails throws IOException
     */
    protected static File MkDirs(String fileNameWithExtension) throws IOException {
        if (!folderPath.exists()) {
            boolean status = folderPath.mkdir();
            if (status) {
                throw new IOException("Failed to create config directory");
            }
            Logger.INFO.LogSilently("Created config directory");
        }
        File filePath = new File(folderPath + File.separator + fileNameWithExtension);
        if (!filePath.exists()) {
            boolean status = filePath.createNewFile();
            if (!status) {
                throw new IOException("Failed to create config file");
            }
            Logger.INFO.LogSilently("Created config file");
        }
        return filePath;
    }

    /**
     * BooleanParse reimplementation but with default return value in case string is invalid
     * @param value String value to parse as boolean
     * @param returnValue   Default return value if string is invalid
     * @return  default value or boolean parsed
     */
    private static boolean BooleanParse(String value, boolean returnValue) {
        value = value.replace(" ", "");
        Logger.DEBUG.Log("value=" + value);
        if (value.equals("true") || value.equals("false")) {
            return value.equals("true");
        }
        if (!value.isEmpty()) Logger.ERROR.LogSilently("Key value \"" + value +"\" is not valid. Expected \"true\" or \"false\".");
        return returnValue;
    }
}
