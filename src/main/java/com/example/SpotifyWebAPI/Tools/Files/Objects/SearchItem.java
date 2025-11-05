package com.example.SpotifyWebAPI.Tools.Files.Objects;

public class SearchItem {
    private String fileName;
    private String filePath;
    private boolean command;
    private int correctChars;

    public SearchItem(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public SearchItem(String fileName, boolean command) {
        this.fileName = fileName;
        this.command = command;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isCommand() {
        return command;
    }

    public void setCommand(boolean command) {
        this.command = command;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public int getCorrectChars() {
        return this.correctChars;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setCorrectChars(int correctChars) {
        this.correctChars = correctChars;
    }

    @Override
    public String toString() {
        return "File name: " + this.fileName + " and File path: " + filePath;
    }
}
