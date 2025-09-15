# Spotify Web API Client

## Overview

This project provides a modular and extendable interface to Spotify’s Web API using Java. It supports authorization, playlist creation, modification, and track management with clear logging and error handling.

----------

## Note

Running JavaFX GUI requires JDK 24! Fortunately rest of the program can run using JDK 21.

----------

## Features

-   **OAuth2 Client Credentials and Refresh Token flow** for secure Spotify API access.
    
-   **Playlist Management:** create, update, retrieve playlists.
    
-   **Track Handling:** add songs, detect duplicates, and fetch playlist tracks with pagination support.
    
-   **Config-driven:** all secrets and options are configurable via external config files.
    
-   **Flexible Modes:** supports automated batch operations (AutoMode) and manual CLI or GUI interaction.
    
-   **Comprehensive Logging:** file-based and console logs with multiple severity levels and debug mode.
    

----------

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) 24 or higher
    
-   Maven or Gradle for dependency management (if applicable)
    
-   Spotify Developer account with registered app for `client_id`, `client_secret`, and necessary scopes
    
## Usage
All Commands and their use
```bash
Usage: program [OPTION]

Options:
  --req                                         Run requirement checks and exit
  --auto-mode                                   Run auto mode functions
  --gui                                         Launch GUI interface
  --cli                                         Launch CLI interface normal mode
  --migrate                                     Migrates configuration from "config.txt" to "config.yaml"

Usage: program set [CONFIGURATION] <value> [CONFIGURATION] <value>...

Configuration Options:
  --playlist-id <id>                            Set default playlist ID
  --userid <id>                                 Set Spotify user ID
  --client-id <id>                              Set Spotify client ID
  --client-secret <secret>                      Set Spotify client secret
  --redirect-uri <uri>                          Set Spotify redirect URI
  --refresh-token <token>                       Set refresh token for session

Other:
  --help                                        Display this help menu
  --get-refresh-token <code> <redirect uri>     get refresh token
```

## How to Automate

1.  Get your playlist ID from the URL, for example:  
    `https://open.spotify.com/playlist/**playlist_id**?si=user_id`
    
2.  Configure your service or cron job to run the program at set intervals:  
    Recommended schedule — three times per day:
    
    -   12:00 PM
        
    -   6:00 PM
        
    -   12:00 AM
        
3.  Each run checks the playlist size and updates the description if needed. Six-hour gaps help avoid hitting Spotify API rate limits.

## Error Handling & Logging

This project features a **custom logger** with multiple log levels: `INFO`, `WARN`, `ERROR`, `CRITICAL`, and `DEBUG`. Logs are timestamped and stored daily in `/Logs` directory with clear, human-readable messages.

The logger helps track all critical operations, API responses, and error cases — invaluable for debugging and monitoring automated playlist updates.

Logger Example Output:
```pgsql
14/09/2025 16:46:13 [ DEBUG ] Current: Key: coloured_output, Value: true. States: isBoolean: false, isNumber: false
14/09/2025 16:46:13 [ DEBUG ] value=true
14/09/2025 16:46:13 [ DEBUG ] Using YAML Reader, with no token type checker
14/09/2025 16:46:13 [ INFO ] Everything is set up correctly, client_id and client_secret are not null or empty.
14/09/2025 16:46:21 [ INFO ] Returned Track: com.example.SpotifyWebAPI.Objects.Track@5e7cd6cc
14/09/2025 16:48:10 [ DEBUG ] Current: Key: coloured_output, Value: true. States: isBoolean: false, isNumber: false
14/09/2025 16:48:10 [ DEBUG ] value=true
14/09/2025 16:48:10 [ DEBUG ] Using YAML Reader, with no token type checker
14/09/2025 16:48:10 [ INFO ] Everything is set up correctly, client_id and client_secret are not null or empty.
14/09/2025 16:48:19 [ INFO ] Returned Track: Track name: Distortion!!, Track number: 3, Id: 3l8rIBKJUDQFqQfKvcpQ1w and Type: track
14/09/2025 16:50:11 [ DEBUG ] Current: Key: coloured_output, Value: true. States: isBoolean: false, isNumber: false
14/09/2025 16:50:11 [ DEBUG ] value=true
14/09/2025 16:50:11 [ DEBUG ] Using YAML Reader, with no token type checker
14/09/2025 16:50:11 [ INFO ] Everything is set up correctly, client_id and client_secret are not null or empty.
14/09/2025 16:50:20 [ ERROR ] Cannot invoke "com.fasterxml.jackson.databind.JsonNode.get(String)" because the return value of "com.fasterxml.jackson.databind.JsonNode.get(String)" is null
	com.example.SpotifyWebAPI.WebRequest.Client_Credentials_Request.getTrackInformation(Client_Credentials_Request.java:64)
	com.example.SpotifyWebAPI.Run_Modes.ConsoleInterface.BasicAuthMenu.Basic_auth_Functions(BasicAuthMenu.java:40)
	com.example.SpotifyWebAPI.Run_Modes.ConsoleInterface.MainMenu.userInterface(MainMenu.java:26)
	com.example.SpotifyWebAPI.Main.main(Main.java:97)
14/09/2025 16:50:40 [ DEBUG ] Current: Key: coloured_output, Value: true. States: isBoolean: false, isNumber: false
14/09/2025 16:50:40 [ DEBUG ] value=true
14/09/2025 16:50:40 [ DEBUG ] Using YAML Reader, with no token type checker
14/09/2025 16:50:40 [ INFO ] Everything is set up correctly, client_id and client_secret are not null or empty.
```
## Issues
1. HTTP requests are single threaded and hold the program when switching scenes
2. When changing scenes window is maximized window resolution will go back to 800x600
3. UI cleanup