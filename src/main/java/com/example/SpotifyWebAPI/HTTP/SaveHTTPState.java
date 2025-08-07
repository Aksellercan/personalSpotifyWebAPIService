package com.example.SpotifyWebAPI.HTTP;

import java.util.HashMap;

/*
Saves HTTP Server state allowing creation of multiple instances of HTTP Servers
Adding a Hashmap with port/id binding, a hashset or a simple array to store all instances in one object
Would allow to run multiple instances and have them shared between JavaFX scenes and controllers easily

Singleton HTTPServer = simple, one instance limitation
Saving state to an object with array = Maybe more complex, allows multiple instances
*/
public final class SaveHTTPState {
    private static final HashMap<String,HTTPServer> stringHTTPServerHashSet = new HashMap<>();

    private SaveHTTPState() {}

    public static int getHashMapSize() {
        return stringHTTPServerHashSet.size();
    }

    public static HTTPServer getServer(String serverName) {
        return stringHTTPServerHashSet.get(serverName);
    }

    public static void removeServer(String serverName) {
        stringHTTPServerHashSet.remove(serverName);
    }

    public static void addHTTPServerToHashMap(String serverName, HTTPServer httpServer) {
        SaveHTTPState.stringHTTPServerHashSet.put(serverName, httpServer);
    }

    public static HashMap<String, HTTPServer> getStringHTTPServerHashSet() {
        return stringHTTPServerHashSet;
    }
}