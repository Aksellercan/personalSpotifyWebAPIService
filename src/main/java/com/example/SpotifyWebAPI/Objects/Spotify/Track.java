package com.example.SpotifyWebAPI.Objects.Spotify;

public class Track {
    private String name;
    private String type;
    private String releaseDate;
    private int popularity;
    private String artist;
    private int trackNumber;
    private String id;

    public Track(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getArtist() {
        return artist;
    }

    public int getPopularity() {
        return popularity;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Track name: " + name + " by: " + artist + ". Ranking: " + (popularity > 50 ? "Popular" : "Fairly Popular") +
                ", Track number: " + trackNumber + ", Id: " + id + " and Type: " + type;
    }
}
