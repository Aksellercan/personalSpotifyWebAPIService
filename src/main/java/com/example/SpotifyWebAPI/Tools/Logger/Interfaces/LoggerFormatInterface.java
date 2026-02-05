package com.example.SpotifyWebAPI.Tools.Logger.Interfaces;

import com.example.SpotifyWebAPI.Tools.Logger.Objects.LogObject;

public interface LoggerFormatInterface {
    String dateSeverityFormat(LogObject log);
    void colourOutput(LogObject log, String fullMessage);
}
