package com.example.SpotifyWebAPI.Tools;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.example.SpotifyWebAPI.Objects.Token;

/**
 * FileUtil rewrite without using HashMaps. Supports: "key=value", YAML format and JSON format (without arrays)
 * <br></br>
 * Interact with public methods only
 */
public final class Configuration {
    /**
     * Configuration folder path
     */
    private static final File folderPath = new File("Config");
    /**
     * Token Configuration Array. Stores "Token" type
     */
    private static Token[] tokenConfig;

    /**
     * Private constructor
     */
    private Configuration() {}

    /**
     * Maps configuration in memory with the current values and writes them
     */
    public static void MapAndWriteConfig() {
        try {
            MapKeys(true);
            WriteConfig();
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to write configuration to file");
        }
    }

    /**
     * Reads configuration file and applies settings for runtime
     */
    public static void ReadConfigAndMap() {
        try {
            tokenConfig = LoadKeys();
            ReadConfig();
            MapKeys(tokenConfig.length == 0);
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to read configuration");
        }
    }

    /**
     * Reads configuration file and applies settings for runtime
     */
    public static void ReadConfigAndMapJSON() {
        try {
            tokenConfig = LoadKeys();
            ReadConfigJSON();
            MapKeys(tokenConfig.length == 0);
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to read configuration");
        }
    }

    /**
     * Maps configuration in memory with the current values and writes them
     */
    public static void MapAndWriteConfigJSON() {
        try {
            MapKeys(true);
            WriteConfigJSON();
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to write configuration to file");
        }
    }

    /**
     * Migrates configuration from "config.txt" to "config.yaml"
     */
    public static void MigrateToYAML() {
        try {
            Logger.INFO.Log("Reading config...", false);
            OldConfigReader();
            ProgramOptions.setChangesSaved(false);
            Logger.INFO.Log("Config read. Now writing it as YAML", false);
            MapAndWriteConfig();
        } catch (Exception e) {
            Logger.ERROR.LogException(e, "Failed to copy contents of old configuration file");
        }
    }

    /**
     * Specify keys to write when config file is not present (first time launch).
     * <br></br>
     * Add Entries here to be loaded when config file is non-existent or empty
     */
    private static Token[] LoadKeys() throws IOException {
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
        int arraySize = getFileLength("config.yaml");
        if (arraySize == 0) {
            tokenConfig = new Token[11];
        } else {
            if ((arraySize < keys.length)) {
                tokenConfig = new Token[keys.length];
            } else {
                tokenConfig = new Token[arraySize];
            }
        }
        for (int i = 0; i < tokenConfig.length; i++) {
            tokenConfig[i] = new Token(keys[i], "");
            Logger.DEBUG.Log(tokenConfig[i].toString());
        }
        return tokenConfig;
    }

    /**
     * Check if folder and the given filename with extension is present on system
     * @param fileWithExtension File to look for
     * @return  File found with its path
     * @throws IOException  IOException when an error occurs while creating files
     */
    private static File MkDirs(String fileWithExtension) throws IOException{
        if (!folderPath.exists()) {
            boolean status = folderPath.mkdir();
            if (status) {
                throw new IOException("Failed to create config directory");
            }
            Logger.INFO.LogSilently("Created config directory");
        }
        File filePath = new File(folderPath + File.separator + fileWithExtension);
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
     * Map current values with token array to be written or set values using tokenConfig array
     * <br></br>
     * Add new entries here, with update and normal behaviour
     */
    private static void MapKeys(boolean update) {
        SpotifySession spotifySession = SpotifySession.getInstance();
        for (int i = 0; i < tokenConfig.length; i++) {
            tokenConfig[i] = TokenTypeCheck(tokenConfig[i]);
            Logger.DEBUG.Log("Current: " + tokenConfig[i].toString());
            switch (tokenConfig[i].getKey().replace("\t", "")) {
                case "client_id":
                    if (update)  {
                        tokenConfig[i].setValue(spotifySession.getClient_id());
                        break;
                    }
                    spotifySession.setClient_id(tokenConfig[i].getValue());
                    break;
                case "client_secret":
                    if (update) {
                        tokenConfig[i].setValue(spotifySession.getClient_secret());
                        break;
                    }
                    spotifySession.setClient_secret(tokenConfig[i].getValue());
                    break;
                case "refresh_token":
                    if (update) {
                        tokenConfig[i].setValue(spotifySession.getRefresh_token());
                        break;
                    }
                    spotifySession.setRefresh_token(tokenConfig[i].getValue());
                    break;
                case "user_id":
                    if (update) {
                        tokenConfig[i].setValue(spotifySession.getUser_id());
                        break;
                    }
                    spotifySession.setUser_id(tokenConfig[i].getValue());
                    break;
                case "redirect_uri":
                    if (update) {
                        tokenConfig[i].setValue(spotifySession.getRedirect_uri());
                        break;
                    }
                    spotifySession.setRedirect_uri(tokenConfig[i].getValue());
                    break;
                case "playlist_id":
                    if (update) {
                        tokenConfig[i].setValue(spotifySession.getPlaylist_id());
                        break;
                    }
                    spotifySession.setPlaylist_id(tokenConfig[i].getValue());
                    break;
                case "output_debug":
                    if (update) {
                        tokenConfig[i].setValue(String.valueOf(Logger.getDebugOutput()));
                        break;
                    }
                    Logger.setDebugOutput(BooleanParse(tokenConfig[i].getValue(), false));
                    break;
                case "verbose_log_file":
                    if (update) {
                        tokenConfig[i].setValue(String.valueOf(Logger.getVerboseLogFile()));
                        break;
                    }
                    Logger.setVerboseLogFile(BooleanParse(tokenConfig[i].getValue(), false));
                    break;
                case "launch_gui":
                    if (update) {
                        tokenConfig[i].setValue(String.valueOf(ProgramOptions.LAUNCH_GUI()));
                        break;
                    }
                    ProgramOptions.setLAUNCH_GUI(BooleanParse(tokenConfig[i].getValue(), true));
                    break;
                case "auto_mode":
                    if (update) {
                        tokenConfig[i].setValue(String.valueOf(ProgramOptions.isAutoMode()));
                        break;
                    }
                    ProgramOptions.setAutoMode(BooleanParse(tokenConfig[i].getValue(), false));
                    break;
                case "coloured_output":
                    if (update) {
                        tokenConfig[i].setValue(String.valueOf(Logger.getColouredOutput()));
                        break;
                    }
                    Logger.setColouredOutput(BooleanParse(tokenConfig[i].getValue(), false));
                    break;
                default:
                    Logger.ERROR.LogSilently("Key \"" + tokenConfig[i].getKey() + "\" is invalid");
            }
            tokenConfig[i] = TokenTypeCheck(tokenConfig[i]);
            Logger.DEBUG.Log("After type check: " + tokenConfig[i].toString());
        }
    }

    /**
     * Read older configuration file format: "key=value"
     */
    private static void OldConfigReader() {
        int lineNumber = 0;
        int tokenCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(MkDirs("config.txt")))) {
            String line;
            String[] splitLine;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                splitLine = line.split("=", 2);
                if (splitLine.length == 2) {
                    tokenConfig[tokenCount] = new Token(splitLine[0].trim(), splitLine[1].trim());
                    Logger.DEBUG.Log(tokenConfig[tokenCount].toString());
                    tokenCount++;
                } else {
                    Logger.ERROR.LogSilently("Invalid line at " + lineNumber + ": \"" + line + "\", expected format: \"key=value\". Continue reading the file.");
                }
            }
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Failed to read old configuration");

        }
    }

    /**
     * Reads the configuration file and counts lines. This is used to dynamically scale tokenConfig array
     * @param checkLengthFilename   Name of file to check its length
     * @return  Line count
     */
    private static int getFileLength(String checkLengthFilename) {
        try (BufferedReader br = new BufferedReader(new FileReader(MkDirs(checkLengthFilename)))) {
            int fileLength = 0;
            String line;
            while ((line = br.readLine()) !=null ) {
                if (CheckCharacterIgnoreList(line)) {
                    continue;
                }
                fileLength++;
            }
            br.close();
            return fileLength;
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Failed to estimate file length");
            return 0;
        }
    }

    private static boolean CheckCharacterIgnoreList(String line) {
        String[] ignoreList = {"{", "}", "[", "]"};
        boolean skip = false;
        for (String ignore : ignoreList) {
            if (line.equals(ignore)) {
                skip = true;
                break;
            }
        }
        return skip;
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

    /**
     * Writes settings to configuration file. Skips if changes are already saved.
     */
    private static void WriteConfig() {
        Logger.INFO.Log("Changes are " + (ProgramOptions.isChangesSaved() ? "already saved." : "not saved yet."));
        if (!ProgramOptions.isChangesSaved()) {
            try (FileWriter fw = new FileWriter(MkDirs("config.yaml"), false)) {
                for (Token current : tokenConfig) {
                    Logger.DEBUG.Log("Writing key: value " + current.getKey() + ": " + current.getValue());
                    fw.write(current.getKey() + ": " + current.getValue() + "\n");
                }
                ProgramOptions.setChangesSaved(true);
                Logger.INFO.Log("Saved changes.");
            } catch (Exception e) {
                Logger.CRITICAL.LogException(e, "Failed to write configuration");
            }
        }
    }

    /**
     * Reads configuration file and saves it to tokenConfig array
     */
    private static void ReadConfig() {
        int lineCount = 0;
        int tokenCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(MkDirs("config.yaml")))) {
            String line;
            String[] splitLine;
            while ((line = br.readLine()) != null) {
                lineCount++;
                splitLine = line.split(":", 2);
                if (splitLine.length == 2) {
                    tokenConfig[tokenCount] = new Token(splitLine[0].trim(), splitLine[1].trim());
                    Logger.DEBUG.Log(tokenConfig[tokenCount].toString());
                    tokenCount++;
                } else {
                    Logger.ERROR.LogSilently("Invalid line at " + lineCount + ": \"" + line + "\", expected format: \"key: value\". Continue reading the file.");
                }
            }
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Failed to read configuration");
        }
    }

    private static void WriteConfigJSON() {
        Logger.INFO.Log("Changes are " + (ProgramOptions.isChangesSaved() ? "already saved." : "not saved yet."));
        if (!ProgramOptions.isChangesSaved()) {
            try (FileWriter fw = new FileWriter(MkDirs("config.json"), false)) {
                fw.write("{\n");
                int length = tokenConfig.length;
                int currentElement = 0;
                for (Token current : tokenConfig) {
                    Logger.DEBUG.Log("Key \"" + current.getKey() + "\", Value \"" + current.getValue() + "\"");
                    if (!current.getValue().equals("")) {
                        if ((currentElement + 1) == length) {
                            fw.write(PrintCorrectType(current) + "\n");
                        } else {
                            fw.write(PrintCorrectType(current) + ",\n");
                            currentElement++;
                        }
                    }
                }
                fw.write("}");
                ProgramOptions.setChangesSaved(true);
                Logger.INFO.Log("Saved changes.");
            } catch (Exception e) {
                Logger.ERROR.LogException(e);
            }
        }
    }

    private static void ReadConfigJSON() {
        int lineCount = 0;
        int tokenCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(MkDirs("config.json")))) {
            String line;
            String[] splitLine;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (CheckCharacterIgnoreList(line)) {
                    continue;
                }
                splitLine = line.split(":",2);
                if (splitLine.length == 2) {
                    splitLine = RemoveQuotes(splitLine[0], splitLine[1]);
                    Logger.DEBUG.Log("Before check: key =" + splitLine[0] + " value =" +splitLine[1]);
                    tokenConfig[tokenCount] = TokenTypeCheck(new Token(splitLine[0].trim(), splitLine[1].trim()));
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

    private static String[] RemoveQuotes(String key, String value) {
        StringBuilder valueMutable = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) == '"') {
                continue;
            }
            valueMutable.append(key.charAt(i));
        }
        StringBuilder keyMutable = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '"' || value.charAt(i) == ',') {
                continue;
            }
            keyMutable.append(value.charAt(i));
        }
        return new String[] {valueMutable.toString(), keyMutable.toString()};
    }

    private static String PrintCorrectType(Token current) {
        if (current.getIsNumber() || current.getIsBoolean()) {
            // 2 spaces instead of tabs
            return "  \"" + current.getKey() + "\"" + ": " + current.getValue();
        }
        // 2 spaces instead of tabs
        return "  \"" + current.getKey() + "\"" + ": \"" + current.getValue() + "\"";
    }


    private static boolean CheckBoolean(String value) {
        return (
                value.replace(" ", "").equalsIgnoreCase("true")
                        ||
                        value.replace(" ", "").equalsIgnoreCase("false")
        );
    }

    private static Token TokenTypeCheck(Token current) {
        if (CheckBoolean(current.getValue())) {
            current.setBoolean(true);
        }
        Pattern pattern= Pattern.compile("^\\d+$");
        Matcher matcher = pattern.matcher(current.getValue());
        if (matcher.find()) {
            current.setNumber(true);
        }
        Logger.DEBUG.Log("isBoolean is " + current.getIsBoolean() + " isNumber is " + current.getIsNumber());
        return current;
    }
}
