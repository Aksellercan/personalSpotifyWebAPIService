package com.example.SpotifyWebAPI.Objects;

public class Playlist {
    private String description;

    public Playlist(String description) {
        this.description = description;
    }

    //getters
    public String getDescription() {
        return description;
    }

    //setters
    public void setDescription(String description) {
        this.description = description;
    }

    //toString
    @Override
    public String toString() {
        return "com.example.SpotifyWebAPI.Objects.Playlist Description: " + description;
    }
}