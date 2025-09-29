package com.example.SpotifyWebAPI.Tools.Files.Parsers;

import com.example.SpotifyWebAPI.Tools.Files.Configuration;
import com.example.SpotifyWebAPI.Tools.Files.Objects.Token;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSONParser inherits Configuration abstract class
 */
public class JSONParser extends Configuration implements Parsers {
    /**
     * Write JSON with formatting
     */
    private static boolean useFormatting = true;

    /**
     * Formatting value getter
     * @return  Formatting value
     */
    public static boolean getUseFormatting() {
        return useFormatting;
    }

    /**
     * Formatting value setter
     * @param setFormatting Update formatting value
     */
    public static void setUseFormatting(boolean setFormatting) {
        useFormatting = setFormatting;
    }

    /**
     * Reads configuration file and applies settings for runtime
     */
    public static void ReadConfigAndMap() {
        try {
            JSONParser jsonParser = new JSONParser();
            Logger.INFO.Log("Using JSON Reader with token type checker");
            tokenConfig = jsonParser.LoadKeys();
            jsonParser.ReadConfig();
            jsonParser.SetTokenTypes();
            jsonParser.MapKeys(tokenConfig.length == 0);
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to read configuration");
        }
    }

    /**
     * Maps configuration in memory with the current values and writes them
     */
    public static void MapAndWriteConfig() {
        try {
            JSONParser jsonParser = new JSONParser();
            Logger.INFO.Log("Using JSON Writer with token type checker");
            jsonParser.MapKeys(true);
            jsonParser.SetTokenTypes();
            if (JSONParser.getUseFormatting())
                jsonParser.WriteConfig();
            else
                jsonParser.WriteAsSingleLine();
        } catch (Exception e) {
            Logger.CRITICAL.LogException(e, "Unable to write configuration to file");
        }
    }

    /**
     * Set Token types boolean/number
     */
    private void SetTokenTypes() {
        for (int i = 0; i < tokenConfig.length; i++) {
            tokenConfig[i] = TokenTypeCheck(tokenConfig[i]);
            Logger.DEBUG.Log("After type check: " + tokenConfig[i].toString());
        }
    }

    private void WriteAsSingleLine() {
        try (FileWriter fw = new FileWriter(MkDirs("config.json"), false)) {
            int length = tokenConfig.length;
            int currentElement = 0;
            fw.write("{");
            for (Token current : tokenConfig) {
                Logger.DEBUG.Log("Key \"" + current.getKey() + "\", Value \"" + current.getValue() + "\"");
                if (!current.getValue().isEmpty()) {
                    if ((currentElement + 1) == length) {
                        fw.write(PrintCorrectType(current) + "\t");
                    } else {
                        fw.write(PrintCorrectType(current) + ",");
                        currentElement++;
                    }
                } else {
                    Logger.WARN.Log("Key: \"" + current.getKey() + "\" is empty, skipping...");
                }
            }
            fw.write("}");
        } catch (IOException e) {
            Logger.ERROR.LogException(e, "IO error");
        }
    }

    /**
     * Writes configuration to file in JSON format (Arrays are not available currently)
     */
    public void WriteConfig() {
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
                    Logger.WARN.Log("Key: \"" + current.getKey() + "\" is empty, skipping...");
                }
            }
            fw.write("}");
            Logger.INFO.Log("Saved changes.");
        } catch (IOException e) {
            Logger.ERROR.LogException(e);
        }
    }

    /**
     * Reads JSON format configuration file
     */
    public void ReadConfig() {
        try (BufferedReader br = new BufferedReader(new FileReader(MkDirs("config.json")))) {
            String line;
            StringBuilder fullText = new StringBuilder();
            while ((line = br.readLine()) != null) {
                fullText.append(line);
            }
            Tokenizer(fullText.toString());
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }

    private void Tokenizer(String line) throws Exception {
        boolean openBrackets = false;
        boolean openSeparator = false;
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (JSONSymbols.OPEN_BRACKETS.equals(line.charAt(i))) {
                openBrackets = true;
                continue;
            }
            if (JSONSymbols.SEPARATOR.equals(line.charAt(i))) {
                //move to another method to remove quotes and type check
                ApplyToken(token.toString());
                Logger.DEBUG.Log("Token value set: " + token);
                token.setLength(0);
                openSeparator = true;
                continue;
            }
            if (JSONSymbols.CLOSE_BRACKETS.equals(line.charAt(i))) {
                ApplyToken(token.toString());
                Logger.DEBUG.Log("Token value set: " + token);
                token.setLength(0);
                openBrackets = false;
                break;
            }
            token.append(line.charAt(i));
            openSeparator = false;
        }
        if (openSeparator) {
            throw new Exception("Expected another value! Error reading...");
        }
        if (openBrackets) {
            throw new Exception("Object not closed! Error reading...");
        }
    }

    private void ApplyToken(String pair) throws Exception {
        String[] splitLine = SplitToken(pair);
        if (splitLine == null) return;
        Logger.DEBUG.Log("Before check: key =" + splitLine[0].trim() + " value =" +splitLine[1].trim());
        FindAndSetToken(splitLine[0].trim(), splitLine[1].trim());
    }

    private String[] SplitToken(String tokenPair) throws Exception {
        Logger.DEBUG.Log("split line " + tokenPair);
        String[] splitLine = tokenPair.split(JSONSymbols.SPLIT.toString(),2);
        Logger.DEBUG.Log((splitLine.length == 2) ? "go to remove quotes" : "null return");
        return (splitLine.length == 2) ? RemoveQuotes(splitLine[0], splitLine[1]) : null;
    }

    /**
     * Removes Quotes from Token's key and value
     * @param key   Token key
     * @param value Token Value
     * @return  Key and value as array
     */
    private String[] RemoveQuotes(String key, String value) throws Exception {
        StringBuilder valueMutable = new StringBuilder();
        int quotesCount = 0;
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) == '"') {
                quotesCount++;
                continue;
            }
            valueMutable.append(key.charAt(i));
        }
        if (quotesCount != 2) {
            throw new Exception("Key is missing quotes");
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
    private String PrintCorrectType(Token current) {
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
    private boolean CheckBoolean(String value) {
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
    private Token TokenTypeCheck(Token current) {
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
