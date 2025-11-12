package com.example.SpotifyWebAPI.Tools.Logger;

public interface LoggerFormatInterface {
    String DateSeverityFormat(Log log);
    void ColourOutput(Log log, String fullMessage);
}
