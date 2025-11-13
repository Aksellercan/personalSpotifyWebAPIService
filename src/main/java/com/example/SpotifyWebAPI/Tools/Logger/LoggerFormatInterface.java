package com.example.SpotifyWebAPI.Tools.Logger;

public interface LoggerFormatInterface {
    String dateSeverityFormat(LogObject log);
    void colourOutput(LogObject log, String fullMessage);
}
