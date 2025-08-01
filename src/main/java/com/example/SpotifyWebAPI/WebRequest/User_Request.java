package com.example.SpotifyWebAPI.WebRequest;

import com.example.SpotifyWebAPI.HTTP.HTTPConnection;
import com.example.SpotifyWebAPI.Tools.Logger;
import com.example.SpotifyWebAPI.Objects.Playlist;
import com.example.SpotifyWebAPI.Objects.SpotifySession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.HttpURLConnection;
import java.util.HashMap;

public class User_Request {
    private final HTTPConnection httpConnection = HTTPConnection.getInstance();
    private final SpotifySession spotifySession = SpotifySession.getInstance();
    private Playlist playlist;
    private final HashMap<String, String> songs = new HashMap<>();

    public Playlist getPlaylist() {
        if (playlist == null) {
            Logger.WARN.LogSilently("Playlist is null");
            return null;
        }
        return playlist;
    }

    //Needs playlist-modify-public Scope
    public void setPlaylistDetails(String playlist_id, String name, String description, boolean publicPlaylist, boolean collaborative) {
        try {
            String fullURL = "https://api.spotify.com/v1/playlists/" + playlist_id;
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = httpConnection.connectHTTP(fullURL, "PUT", "Authorization", Bearer, "Content-Type", "application/json");
            http.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            Playlist playlist = new Playlist(name, description);
            playlist.setPublicPlaylist(publicPlaylist);
            playlist.setCollaborative(collaborative);
            playlist.setPlaylist_id(playlist_id);
            String postBody = mapper.writeValueAsString(playlist);
            Logger.DEBUG.Log("postBody: " + postBody);
            httpConnection.postBody(http, postBody);
            int responseCode = http.getResponseCode();
            if (responseCode == 200) {
                Logger.INFO.Log("Updated playlist description to " + description + ". HTTP Response Code " + responseCode);
            } else {
                JsonNode node = mapper.readTree(http.getInputStream());
                throw new Exception("Failed to update playlist description to " + description + ". HTTP Response Code " + responseCode + ", " + node.get("message").asText());
            }
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }

    private String buildURL(String fullURL, int offset, int limit) {
        StringBuilder url = new StringBuilder(fullURL);
        if (offset > 0) {
            url.append("offset=").append(offset).append("&limit=").append(limit);
        } else {
            url.append("limit=").append(limit);
        }
        return url.toString();
    }

    //Needs playlist-read-private Scope
    public void getPlaylistItems(String playlist_id, int offset, int limit) {
        try {
            String fullURL = "https://api.spotify.com/v1/playlists/"+ playlist_id + "/tracks?";
            fullURL = buildURL(fullURL, offset, limit);
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = httpConnection.connectHTTP(fullURL, "GET", "Authorization", Bearer);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(http.getInputStream());
            int duplicateCount = 0;
            for (JsonNode key : node.get("items")) {
                if (songs.containsKey(key.get("track").get("name").asText())) {
                    Logger.INFO.Log("Duplicate song found: " + key.get("track").get("name").asText() + " URI: " + key.get("track").get("uri").asText());
                    duplicateCount++;
                    songs.put(key.get("track").get("name").asText() + " duplicate " + duplicateCount, key.get("track").get("uri").asText());
                    continue;
                }
                songs.put(key.get("track").get("name").asText(), key.get("track").get("uri").asText());
                Logger.INFO.Log("Song: " + key.get("track").get("name").asText() + " URI: " + key.get("track").get("uri").asText());
            }
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }

    private ArrayNode addFromPreviousPlaylist(ObjectMapper mapper) {
        ArrayNode arrayNode = mapper.createArrayNode();
        for(String value : songs.values()) {
            arrayNode.add(value);
        }
        return arrayNode;
    }

    public void addPlaylistItems(String playlist_id, int position, String track_uri, boolean addFromPreviousPlaylist) {
        try {
            String fullURL = "https://api.spotify.com/v1/playlists/" + playlist_id + "/tracks";
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = httpConnection.connectHTTP(fullURL, "POST", "Authorization", Bearer, "Content-Type", "application/json");
            http.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            if (addFromPreviousPlaylist) {
                rootNode.set("uris", addFromPreviousPlaylist(mapper));
            } else {
                ArrayNode arrayNode = mapper.createArrayNode();
                arrayNode.add(track_uri);
                rootNode.set("uris", arrayNode);
            }
            rootNode.put("position", position);
            String postBody = mapper.writeValueAsString(rootNode);
            httpConnection.postBody(http, postBody);
            httpConnection.readErrorStream(http,400, true);
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }

    public void createPlaylist(String user_id, String playlistName, String playlistDescription) {
        try {
            String fullURL = "https://api.spotify.com/v1/users/" + user_id + "/playlists";
            String Bearer = "Bearer " + spotifySession.getAccess_token();
            HttpURLConnection http = httpConnection.connectHTTP(fullURL, "POST", "Authorization", Bearer, "Content-Type", "application/json");
            http.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();

            playlist = new Playlist(playlistName, playlistDescription);
            playlist.setPublicPlaylist(true);
            playlist.setCollaborative(false);
            String postBody = mapper.writeValueAsString(playlist);
            httpConnection.postBody(http, postBody);
            JsonNode node = mapper.readTree(http.getInputStream());
            httpConnection.postBody(http, postBody);
            String snapshot_id = node.get("snapshot_id").asText();
            String playlist_id = node.get("id").asText();
            String external_urls = node.get("external_urls").get("spotify").asText();
            String href = node.get("href").asText();
            Logger.INFO.Log("Playlist created with ID: " + playlist_id);
            Logger.INFO.Log("Playlist snapshot ID: " + snapshot_id);
            Logger.INFO.Log("Playlist external URL: " + external_urls);
            Logger.INFO.Log("Playlist href: " + href);
            Logger.INFO.Log("Playlist name: " + playlistName);
            Logger.INFO.Log("Playlist description: " + playlistDescription);
            Logger.INFO.Log("Playlist public: " + playlist.isPublicPlaylist());
            Logger.INFO.Log("Playlist collaborative: " + playlist.isCollaborative());
            playlist.setPlaylist_id(playlist_id);
        } catch (Exception e) {
            Logger.ERROR.LogException(e);
        }
    }
}