package com.example.SpotifyWebAPI.HTTP;

/*
Saves HTTP Server state allowing creation of multiple instances of HTTP Servers
Adding a Hashmap with port/id binding, a hashset or a simple array to store all instances in one object
Would allow to run multiple instances and have them shared between JavaFX scenes and controllers easily

Singleton HTTPServer = simple, one instance limitation
Saving state to an object with array = Maybe more complex, allows multiple instances
*/
public final class SaveHTTPState {
    private static HTTPServer server;

    private SaveHTTPState() {}

    public static HTTPServer getServer() {
        return server;
    }

    public static void setServer(HTTPServer server) {
        SaveHTTPState.server = server;
    }
}
