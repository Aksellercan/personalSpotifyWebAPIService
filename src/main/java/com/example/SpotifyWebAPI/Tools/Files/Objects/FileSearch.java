package com.example.SpotifyWebAPI.Tools.Files.Objects;

public class FileSearch {
    private String fileName;
    private String filePath;
    private int correctChars;

    public FileSearch(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return this.fileName;
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
