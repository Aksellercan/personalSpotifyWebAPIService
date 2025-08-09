package com.example.SpotifyWebAPI.HTTP.Enumerators;

/**
 * HTTP Content Types
 */
public enum ContentType {
    ImageXIcon("image/x-icon"),
    JavaScript("text/javascript"),
    HTML("text/html"),
    StyleSheet("text/css"),
    JSON("application/json"),
    AudioMpeg("audio/mpeg"),
    TextPlain("text/plain");

    private final String typeHeader;

    ContentType(String typeHeader) {
        this.typeHeader = typeHeader;
    }

    /**
     * Returns Content Type in string
     * @return  Content Type as string
     */
    public String getContentType() {
        return typeHeader;
    }
}
