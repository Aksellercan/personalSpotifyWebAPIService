package com.example.SpotifyWebAPI.Tools.Files.Parsers;

import com.example.SpotifyWebAPI.Objects.ProgramOptions;
import com.example.SpotifyWebAPI.Tools.Files.Objects.Token;
import com.example.SpotifyWebAPI.Tools.Files.Configuration;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSONParser inherits Configuration abstract class
 */
public final class JSONParser extends Configuration {

    /**
     * Private Constructor
     */
    private JSONParser() {}

    /**
     * Reads configuration file and applies settings for runtime
     */
    public static void ReadConfigAndMap() {
        try {
            Logger.INFO.Log("Using JSON Reader, using token type checker");
            tokenConfig = LoadKeys();
            ReadConfig();
            MapKeys(tokenConfig.length == 0);
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to read configuration");
        }
    }

    /**
     * Maps configuration in memory with the current values and writes them
     */
    public static void MapAndWriteConfig() {
        try {
            MapKeys(true);
            SetTokenTypes();
            WriteConfig();
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to write configuration to file");
        }
    }

    /**
     * Set Token types boolean/number
     */
    private static void SetTokenTypes() {
        for (int i = 0; i < tokenConfig.length; i++) {
            tokenConfig[i] = TokenTypeCheck(tokenConfig[i]);
            Logger.DEBUG.Log("After type check: " + tokenConfig[i].toString());
        }
    }

    /**
     * Writes configuration to file in JSON format (Arrays are not available currently)
     */
    private static void WriteConfig() {
        Logger.INFO.Log("Changes are " + (ProgramOptions.isChangesSaved() ? "already saved." : "not saved yet."));
        if (!ProgramOptions.isChangesSaved()) {
            try (FileWriter fw = new FileWriter(MkDirs("config.json"), false)) {
                fw.write("{\n");
                int length = tokenConfig.length;
                int currentElement = 0;
                for (Token current : tokenConfig) {
                    Logger.DEBUG.Log("Key \"" + current.getKey() + "\", Value \"" + current.getValue() + "\"");
                    if (!current.getValue().isEmpty()) {
                        if ((currentElement + 1) == length) {
                            fw.write(PrintCorrectType(current) + "\n");
                        } else {
                            fw.write(PrintCorrectType(current) + ",\n");
                            currentElement++;
                        }
                    } else {
                        Logger.DEBUG.Log("Key: \"" + current.getKey() + "\" is empty, skipping...");
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

    /**
     * Reads JSON format configuration file
     */
    private static void ReadConfig() {
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

    /**
     * Checks if current line is in Character Ignore List
     * @param line  Current line
     * @return  Whether to skip the line or not
     */
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
     * Removes Quotes from Token's key and value
     * @param key   Token key
     * @param value Token Value
     * @return  Key and value as array
     */
    private static String[] RemoveQuotes(String key, String value) {
        StringBuilder valueMutable = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) == '"') {
                continue;
            }
            valueMutable.append(key.charAt(i));
        }
        StringBuilder keyMutable = new StringBuilder();
        boolean openQuotes = false;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '"') {
                openQuotes = !openQuotes;
                continue;
            }
            if (!openQuotes) {
                if (value.charAt(i) == ',') {
                    continue;
                }
            }
            keyMutable.append(value.charAt(i));
        }
        return new String[] {valueMutable.toString(), keyMutable.toString()};
    }

    /**
     * Returns the correct line format
     * @param current   Current line token
     * @return  Correct print Format
     */
    private static String PrintCorrectType(Token current) {
        if (current.getIsNumber() || current.getIsBoolean()) {
            // 2 spaces instead of tabs
            return "\t\"" + current.getKey() + "\"" + ": " + current.getValue();
        }
        // 2 spaces instead of tabs
        return "\t\"" + current.getKey() + "\"" + ": \"" + current.getValue() + "\"";
    }

    /**
     * Checks if Token's value is boolean or not
     * @param value Token's value
     * @return  True/False
     */
    private static boolean CheckBoolean(String value) {
        return (
                value.replace(" ", "").equalsIgnoreCase("true")
                        ||
                        value.replace(" ", "").equalsIgnoreCase("false")
        );
    }

    /**
     * Checks Token's type Number/boolean
     * @param current   Current Token
     * @return  Token with type set
     */
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
