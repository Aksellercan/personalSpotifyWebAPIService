package com.example.SpotifyWebAPI.Objects;

public class Playlist {
    private String name;
    private String description;
    private String imageUrl;
    private String playlist_id;
    private boolean publicPlaylist;
    private boolean collaborative;

    public Playlist(String name, String description) {
        this.name = name;
        this.description = description;
    }

    //getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPlaylist_id() {
        return playlist_id;
    }

    public boolean isPublicPlaylist() {
        return publicPlaylist;
    }

    public boolean isCollaborative() {
        return collaborative;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }

    public void setPublicPlaylist(boolean publicPlaylist) {
        this.publicPlaylist = publicPlaylist;
    }

    public void setCollaborative(boolean collaborative) {
        this.collaborative = collaborative;
    }

    //toString
    @Override
    public String toString() {
        return "com.example.SpotifyWebAPI.Objects.Playlist Details: " + "Name: " + name + ", Description: " + description;
    }
}