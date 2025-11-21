package com.example.SpotifyWebAPI.Tools.Logger;

import java.io.File;
import java.io.IOException;

public interface LoggerBackendInterface extends LoggerQueueInterface, LoggerFormatInterface {
    void saveLog(String fullMessage);
    File getLogFile(String fileName) throws IOException;
    String generateFilename();
}
