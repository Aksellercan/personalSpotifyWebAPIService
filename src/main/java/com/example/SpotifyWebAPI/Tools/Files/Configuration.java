package com.example.SpotifyWebAPI.Tools.Files;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.example.SpotifyWebAPI.Tools.Files.Objects.Token;
import com.example.SpotifyWebAPI.Tools.Files.Parsers.BasicParser;
import com.example.SpotifyWebAPI.Tools.Files.Parsers.JSONParser;
import com.example.SpotifyWebAPI.Tools.Files.Parsers.YAMLParser;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import java.io.File;
import java.io.IOException;

/**
 * Abstract class to share same token array
 */
public abstract class Configuration {
    /**
     * Saved Tokens array
     */
    protected static Token[] tokenConfig;
    /**
     * Configuration folder path
     */
    private final File folderPath = new File("config");

    /**
     * Specify keys to write when config file is not present (first time launch).
     * Add Entries here to be loaded when config file is non-existent or empty
     */
    protected Token[] LoadKeys() {
        String[] keys = {
                "client_id",
                "client_secret",
                "user_id",
                "playlist_id",
                "redirect_uri",
                "refresh_token",
                "auto_mode",
                "launch_gui",
                "playlist_limit",
                "output_debug",
                "coloured_output",
                "enable_stack_traces",
                "verbose_log_file",
                "quiet",
                "use_formatting"
        };
        tokenConfig = new Token[keys.length];
        for (int i = 0; i < tokenConfig.length; i++) {
            tokenConfig[i] = new Token(keys[i]);
        }
        return tokenConfig;
    }

    /**
     * Map current values with token array to be written or set values using tokenConfig array
     * Add new entries here, with update and normal behaviour
     */
    protected void MapKeys(boolean update) {
        SpotifySession spotifySession = SpotifySession.getInstance();
        for (Token token : tokenConfig) {
            switch (token.getKey().replace("\t", "")) {
                case "client_id":
                    if (update) {
                        token.setValue(spotifySession.getClient_id());
                        token.setCategoryType("User Details");
                        token.setSensitiveInfo(true);
                        break;
                    }
                    spotifySession.setClient_id(token.getValue());
                    break;
                case "client_secret":
                    if (update) {
                        token.setValue(spotifySession.getClient_secret());
                        token.setCategoryType("User Details");
                        token.setSensitiveInfo(true);
                        break;
                    }
                    spotifySession.setClient_secret(token.getValue());
                    break;
                case "refresh_token":
                    if (update) {
                        token.setValue(spotifySession.getRefresh_token());
                        token.setCategoryType("User Details");
                        token.setSensitiveInfo(true);
                        break;
                    }
                    spotifySession.setRefresh_token(token.getValue());
                    break;
                case "user_id":
                    if (update) {
                        token.setValue(spotifySession.getUser_id());
                        token.setCategoryType("User Details");
                        token.setSensitiveInfo(true);
                        break;
                    }
                    spotifySession.setUser_id(token.getValue());
                    break;
                case "redirect_uri":
                    if (update) {
                        token.setValue(spotifySession.getRedirect_uri());
                        token.setCategoryType("User Details");
                        token.setSensitiveInfo(true);
                        break;
                    }
                    spotifySession.setRedirect_uri(token.getValue());
                    break;
                case "playlist_id":
                    if (update) {
                        token.setValue(spotifySession.getPlaylist_id());
                        token.setCategoryType("User Details");
                        token.setSensitiveInfo(true);
                        break;
                    }
                    spotifySession.setPlaylist_id(token.getValue());
                    break;
                case "output_debug":
                    if (update) {
                        token.setValue(String.valueOf(Logger.getDebugOutput()));
                        token.setCategoryType("Logger Options");
                        break;
                    }
                    Logger.setDebugOutput(BooleanParse(token.getValue(), false));
                    break;
                case "verbose_log_file":
                    if (update) {
                        token.setValue(String.valueOf(Logger.getVerboseLogFile()));
                        token.setCategoryType("Logger Options");
                        break;
                    }
                    Logger.setVerboseLogFile(BooleanParse(token.getValue(), false));
                    break;
                case "enable_stack_traces":
                    if (update) {
                        token.setValue(String.valueOf(Logger.getEnableStackTraces()));
                        token.setCategoryType("Logger Options");
                        break;
                    }
                    Logger.setEnableStackTraces(BooleanParse(token.getValue(), false));
                    break;
                case "quiet":
                    if (update) {
                        token.setValue(String.valueOf(Logger.getQuiet()));
                        token.setCategoryType("Logger Options");
                        break;
                    }
                    Logger.setQuiet(BooleanParse(token.getValue(), false));
                    break;
                case "playlist_limit":
                    if (update) {
                        token.setValue(String.valueOf(ProgramOptions.getPlaylist_limit()));
                        token.setCategoryType("User Settings");
                        break;
                    }
                    if (token.getValue().isEmpty()) token.setValue(String.valueOf(ProgramOptions.getPlaylist_limit()));
                    ProgramOptions.setPlaylist_limit(Integer.parseInt(token.getValue()));
                    break;
                case "launch_gui":
                    if (update) {
                        token.setValue(String.valueOf(ProgramOptions.LAUNCH_GUI()));
                        token.setCategoryType("Program Options");
                        break;
                    }
                    ProgramOptions.setLAUNCH_GUI(BooleanParse(token.getValue(), true));
                    break;
                case "auto_mode":
                    if (update) {
                        token.setValue(String.valueOf(ProgramOptions.isAutoMode()));
                        token.setCategoryType("Program Options");
                        break;
                    }
                    ProgramOptions.setAutoMode(BooleanParse(token.getValue(), false));
                    break;
                case "coloured_output":
                    if (update) {
                        token.setValue(String.valueOf(Logger.getColouredOutput()));
                        token.setCategoryType("Logger Options");
                        break;
                    }
                    Logger.setColouredOutput(BooleanParse(token.getValue(), false));
                    break;
                case "use_formatting":
                    if (update) {
                        token.setValue(String.valueOf(JSONParser.getUseFormatting()));
                        token.setCategoryType("JSON Options");
                        break;
                    }
                    JSONParser.setUseFormatting(BooleanParse(token.getValue(), true));
                    break;
                default:
                    Logger.WARN.LogSilently("Key \"" + token.getKey() + "\" is invalid");
            }
        }
    }

    /**
     * Migrates configuration from "config.txt" to "config.yaml"
     */
    public static void MigrateToYAML() {
        YAMLParser yamlParser = new YAMLParser();
        tokenConfig = yamlParser.LoadKeys();
        Logger.INFO.Log("Reading config...", false);
        BasicParser basicParser = new BasicParser();
        basicParser.ReadConfig();
        ProgramOptions.setChangesSaved(false);
        Logger.INFO.Log("Config read. Now writing it as YAML", false);
        yamlParser.WriteConfig();
        Logger.INFO.Log("Wrote config as YAML", false);
    }

    /**
     * Find and set Token value, additionally mark it as seen to avoid duplicates
     * @param key   Key to modify
     * @param value Token value
     */
    protected void FindAndSetToken(String key, String value) {
        for (Token token : tokenConfig) {
            if (!token.isSeen() && token.getKey().equals(key)) {
                token.setValue(value);
                token.markAsSeen();
                break;
            }
        }
    }

    /**
     * Checks if directory exists, if it doesn't it creates it and returns the file path
     * @param fileNameWithExtension Name of file to check and create
     * @return  Full path of the file
     * @throws IOException  If creating folder fails throws IOException
     */
    protected File MkDirs(String fileNameWithExtension) throws IOException {
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
    private boolean BooleanParse(String value, boolean returnValue) {
        value = value.replace(" ", "");
        if (value.equals("true") || value.equals("false")) {
            return value.equals("true");
        }
        if (!value.isEmpty()) Logger.ERROR.LogSilently("Key value \"" + value +"\" is not valid. Expected \"true\" or \"false\".");
        return returnValue;
    }
}
