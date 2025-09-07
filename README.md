# Spotify Web API Client

A robust Java client library and CLI and GUI tool that interacts with Spotify’s Web API to manage playlists, retrieve user data, and automate playlist updates.

----------

## Overview

This project provides a modular and extendable interface to Spotify’s Web API using Java. It supports authorization, playlist creation, modification, and track management with clear logging and error handling.

Designed for both automated background execution and interactive command-line use, it’s perfect for music lovers, developers building Spotify integrations, or anyone looking to automate playlist curation.

----------

## Note

Running JavaFX GUI requires JDK 24! Fortunately rest of the program can run using JDK 21.

----------

## Features

-   **OAuth2 Client Credentials and Refresh Token flow** for secure Spotify API access.
    
-   **Playlist Management:** create, update, retrieve playlists.
    
-   **Track Handling:** add songs, detect duplicates, and fetch playlist tracks with pagination support.
    
-   **Config-driven:** all secrets and options are configurable via external config files.
    
-   **Flexible Modes:** supports automated batch operations (AutoMode) and manual CLI interaction.
    
-   **Comprehensive Logging:** file-based and console logs with multiple severity levels and debug mode.
    

----------

## Architecture & Design

-   **Java 17+** with clean separation of concerns.
    
-   `HTTPConnection` handles HTTP setup and requests.
    
-   `SpotifySession` encapsulates authentication tokens and user details.
    
-   `User_Request` and `Client_Credentials_Request` encapsulate Spotify API calls with Jackson JSON parsing.
    
-   `Logger` implemented as an enum for lightweight centralized logging.
    
-   Modular run modes with `AutoMode` and `CLI_Interface` allow flexible user experience.
    
-   Configuration management via `ConfigMaps` backed by external file I/O (`FileUtil`).
    

----------

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) 17 or higher
    
-   Maven or Gradle for dependency management (if applicable)
    
-   Spotify Developer account with registered app for `client_id`, `client_secret`, and necessary scopes
    
## Usage

-   Use the CLI interface to explore and manage playlists interactively.
    
-   Automate playlist updates and syncing with the AutoMode.
    
-   Customize logging output and debugging via config.

-   All Commands and their use
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

This project features a **custom lightweight logger** with multiple log levels: `INFO`, `WARN`, `ERROR`, and `DEBUG`. Logs are timestamped and stored daily in `/Logs` directory with clear, human-readable messages.

The logger helps track all critical operations, API responses, and error cases — invaluable for debugging and monitoring automated playlist updates.

Logger Example Output:
```pgsql
18/05/2025 00:26:27 [ INFO ] Updated playlist description to 26/120. HTTP Response Code 200
18/05/2025 00:53:51 [ INFO ] No refresh Token returned
18/05/2025 00:54:16 [ ERROR ] Cannot write output after reading input.
18/05/2025 00:54:16 [ INFO ] Playlist created with ID: 2d6BnX6iSNTWPlf6ZDgpmE
18/05/2025 00:54:16 [ INFO ] Playlist snapshot ID: AAAbreKTfDxtxugzq1+XoeE1TWvSbNpb
18/05/2025 00:54:16 [ INFO ] Playlist external URL: https://open.spotify.com/playlist/2d6BnXTiSNTWPlf6ZDgpmE
18/05/2025 00:54:16 [ INFO ] Playlist href: https://api.spotify.com/v1/playlists/2d6BnXuiSNTWPlf6ZDgpmE
18/05/2025 00:54:16 [ INFO ] Playlist name: testname
18/05/2025 00:54:16 [ INFO ] Playlist description: testdesc
18/05/2025 00:54:16 [ INFO ] Playlist public: true
18/05/2025 00:54:16 [ INFO ] Playlist collaborative: false
18/05/2025 01:05:41 [ INFO ] Auto Mode set to true
18/05/2025 01:07:50 [ INFO ] Starting AutoMode.runFunctions()
18/05/2025 01:07:50 [ INFO ] No refresh Token returned
18/05/2025 01:07:50 [ INFO ] Received the access token
18/05/2025 01:07:51 [ INFO ] Playlist size is already set to 26
18/05/2025 01:07:51 [ INFO ] Completed the automated run, no changes made
18/05/2025 01:08:06 [ INFO ] Auto Mode set to true
18/05/2025 01:08:10 [ INFO ] Saved Config successfully!
18/05/2025 01:08:13 [ INFO ] Saved Config successfully!
18/05/2025 01:09:00 [ WARN ] Credential auto_mode not found in config map
```

## Issues
1. none
----------

## Why This Project?

This project showcases the ability to design clean, maintainable Java code that interacts with real-world APIs. It demonstrates practical skills in:

-   Network programming with HTTP and REST
    
-   JSON processing with Jackson
    
-   Dependency injection and modular design
    
-   Error handling and logging best practices
    
-   Building CLI and automated tooling

- Spotify playlists have a maximum practical size where shuffling works well. Going beyond 120 songs leads to frustrating shuffle behavior, so this tool keeps you informed by updating the playlist description dynamically.
