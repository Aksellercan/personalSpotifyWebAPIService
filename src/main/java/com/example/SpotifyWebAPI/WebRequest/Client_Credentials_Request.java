package com.example.SpotifyWebAPI.WebRequest;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.Objects.Spotify.Playlist;
import com.example.SpotifyWebAPI.Objects.Spotify.Track;
import com.example.SpotifyWebAPI.Tools.Logger.Logger;
import com.example.SpotifyWebAPI.Objects.Spotify.SpotifySession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;

/**
 * Class of requests that only require basic Access Token
 */

public class Client_Credentials_Request {
    private final SpotifySession spotifySession = SpotifySession.getInstance();
    private Track track;
    private Playlist playlist;

    public Playlist getPlaylist() {
        return playlist;
    }

    public Track getTrack() {
        return track;
    }

    /**
     * Sends a request to get Playlist details such as: Name, Description and Size
     * @param playlist_id   ID of Playlist to get details from
     */
    public void getPlaylist(String playlist_id) {
        try {
            String playlistURL = "https://api.spotify.com/v1/playlists/" + playlist_id;
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = HTTPConnection.connectHTTP(playlistURL, "GET", "Authorization", Bearer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            playlist = new Playlist(node.get("name").asText(), node.get("description").asText());
            playlist.setTotalItems(node.get("tracks").get("total").asInt());
            Logger.INFO.Log(playlist.toString(), false);
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }

    public void getTrackInformation(String track_uri) {
        try {
            String postURL = "https://api.spotify.com/v1/tracks/" + track_uri;
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = HTTPConnection.connectHTTP(postURL, "GET", "Authorization", Bearer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            track = new Track(node.get("name").asText(), node.get("id").asText());
            track.setTrackNumber(node.get("track_number").asInt());
            track.setType(node.get("type").asText());
            track.setPopularity(node.get("popularity").asInt());
            for (JsonNode key : node.get("artists")) {
                track.setArtist(key.get("name").asText());
            }
            Logger.INFO.Log(track.toString(), false);
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }
}