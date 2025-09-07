package com.example.SpotifyWebAPI.HTTP;

import com.example.SpotifyWebAPI.Objects.HTTPState;
import java.util.ArrayList;

public final class SaveHTTPState {
    private static final ArrayList<HTTPState> serverList = new ArrayList<>();

    private SaveHTTPState() {}

    /**
     * Returns the size of HashMap
     * @return  Amount of servers registered on the HashMap
     */
    public static int getHashMapSize() {
        return serverList.size();
    }

    public static boolean ContainsServer(String serverName) {
        for (HTTPState httpState : serverList) {
            if (httpState.getServerName().equals(serverName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the server with the name given
     * @param serverName    Name of the server to return
     * @return  Found server
     */
    public static HTTPServer getServer(String serverName) {
        for (HTTPState httpState : serverList) {
            if (httpState.getServerName().equals(serverName)) {
                return httpState.getServer();
            }
        }
        return null;
    }

    /**
     * Removes the server with given name from the HashMap
     * @param serverName    Name of the server to remove
     */
    public static void removeServer(String serverName) {
        for (int i = 0; i < serverList.size(); i++) {
            if (serverList.get(i).getServerName().equals(serverName)) {
                serverList.remove(i);
                break;
            }
        }
    }

    /**
     * Adds Server to the HashMap with given name
     * @param serverName    Name of the Server to add
     * @param httpServer    Server to save state of
     */
    public static void addHTTPServerToHashMap(String serverName, HTTPServer httpServer) {
        HTTPState state = new HTTPState(serverName, httpServer);
        serverList.add(state);
    }
}
