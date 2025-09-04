package com.example.SpotifyWebAPI.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Objects.Token;

public final class Configuration {
    private static final File folderPath = new File("Config");
    private static final File filePath = new File(folderPath + File.separator + "config.yaml");
    private static Token[] tokenConfig;

    private Configuration() {}

    /*
    Interact with these only
     */
    public static void MapAndWriteConfig() {
        MapKeys(true);
        WriteConfig();
    }

    public static void ReadConfigAndMap() {
        int arraySize = getFileLength();
        if (arraySize == -1 || arraySize == 0) {
            tokenConfig = new Token[6];
        } else {
            tokenConfig = new Token[arraySize-2];
        }
        LoadKeys();
        ReadConfig();
        MapKeys(arraySize == -1 || arraySize == 0);
    }

    /*
    Add Entries here to be loaded when config file is non-existent or empty
     */
    private static void LoadKeys() {
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
        for (int i = 0; i < tokenConfig.length; i++) {
            tokenConfig[i] = new Token(keys[i], "");
        }
    }

    private static void MkDirs()  {
        try {
            if (!folderPath.exists()) {
                boolean status = folderPath.mkdir();
                if (status) {
                    Logger.INFO.LogSilently("Created config directory");
                }
            }
            if (!filePath.exists()) {
                boolean status = filePath.createNewFile();
                if (status) {
                    Logger.INFO.LogSilently("Created config file");
                }
            }
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }

    /*
    Add new entries here
     */
    private static void MapKeys(boolean update) {
        SpotifySession spotifySession = SpotifySession.getInstance();
        for (Token token : tokenConfig) {
            Logger.DEBUG.Log("Current: " + token.toString());
            switch (token.getKey().replace("\t", "")) {
                case "client_id":
                    if (update)  {
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

    private static int getFileLength() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int fileLength = 0;
            while (br.readLine() !=null ) {
                fileLength++;
            }
            return fileLength;
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to get the length of file");
            return -1;
        }
    }

    private static boolean BooleanParse(String value, boolean returnValue) {
        value = value.replace(" ", "");
        Logger.DEBUG.Log("value=" + value);
        if (value.equals("true") || value.equals("false")) {
            return value.equals("true");
        }
        if (!value.isEmpty()) Logger.ERROR.LogSilently("Key value \"" + value +"\" is not valid. Expected \"true\" or \"false\".");
        return returnValue;
    }

    private static void WriteConfig() {
        MkDirs();
        try(FileWriter fw = new FileWriter(filePath, false)) {
            for (Token current : tokenConfig) {
                Logger.DEBUG.Log("Writing key: value " + current.getKey() + ": " + current.getValue());
                fw.write(current.getKey() + ": " + current.getValue());
            }
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }

    private static void ReadConfig() {
        int lineCount = 0;
        int tokenCount = 0;
        MkDirs();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] splitLine;
            while ((line = br.readLine()) != null) {
                lineCount++;
                splitLine = line.split(":",2);
                if (splitLine.length == 2) {
                    tokenConfig[tokenCount] = new Token(splitLine[0].trim(), splitLine[1].trim());
                    Logger.DEBUG.Log(tokenConfig[tokenCount].toString());
                    tokenCount++;
                } else {
                    Logger.ERROR.LogSilently("Invalid line at " + lineCount + ": \"" + line + "\", expected format: \"key: value\". Continue reading the file.");
                }
            }
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }
}
