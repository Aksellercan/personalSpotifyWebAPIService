# Spotify WebAPI Service 

Using Spotify Web API get a playlist's amount of songs in number then update description accordingly $"{x}/120" if it reaches 120 stop. 120 song limit to avoid terrible Shuffling. Useful for mobile as you can't see the number of songs in a playlist.

Using Systemd service can run at specific times:  
Thrice a day
1. 1st at 12:00
2. 2nd at 18:00
3. 3rd at 00:00  
Each run it queries as explained in description then decides if it has to update or not. Since queries are spaced with 6-hour breaks it won't run into too many queries error from the API.

- Spotify playlist id can be obtained from: https://open.spotify.com/playlist/**playlist_id**?si=user_id
