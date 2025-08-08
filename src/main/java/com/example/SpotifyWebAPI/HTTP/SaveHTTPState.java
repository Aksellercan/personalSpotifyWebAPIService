package com.example.SpotifyWebAPI.HTTP;

import java.util.HashMap;

/*
Saves HTTP Server state allowing creation of multiple instances of HTTP Servers
Adding a Hashmap with port/id binding, a hashset or a simple array to store all instances in one object
Would allow to run multiple instances and have them shared between JavaFX scenes and controllers easily

Singleton HTTPServer = simple, one instance limitation
Saving state to an object with array = Maybe more complex, allows multiple instances
*/

/**
 * Saves the HTTP Server state in a Hashmap with its name to manage multiple servers at the same time on different ports
 */
public final class SaveHTTPState {
    private static final HashMap<String,HTTPServer> stringHTTPServerHashSet = new HashMap<>();

    private SaveHTTPState() {}

    /**
     * Returns the size of HashMap
     * @return  Amount of servers registered on the HashMap
     */
    public static int getHashMapSize() {
        return stringHTTPServerHashSet.size();
    }

    /**
     * Returns the server with the name given
     * @param serverName    Name of the server to return
     * @return  Found server
     */
    public static HTTPServer getServer(String serverName) {
        return stringHTTPServerHashSet.get(serverName);
    }

    /**
     * Removes the server with given name from the HashMap
     * @param serverName    Name of the server to remove
     */
    public static void removeServer(String serverName) {
        stringHTTPServerHashSet.remove(serverName);
    }

    /**
     * Adds Server to the HashMap with given name
     * @param serverName    Name of the Server to add
     * @param httpServer    Server to save state of
     */
    public static void addHTTPServerToHashMap(String serverName, HTTPServer httpServer) {
        SaveHTTPState.stringHTTPServerHashSet.put(serverName, httpServer);
    }

    /**
     * @deprecated
     * Returns the HashMap
     * @return  HashMap of the running servers
     */
    public static HashMap<String, HTTPServer> getStringHTTPServerHashSet() {
        return stringHTTPServerHashSet;
    }
}