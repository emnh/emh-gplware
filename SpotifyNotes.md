# Introduction #

# Wishlist for Spotify client #
  * Queue tracks by launching spotify.exe.
  * Starring in Spotify client should love track on last.fm
Libspotify or despotify is a solution, but they require Spotify Premium, which will wait until I have a job.

# Spotify external play control for among other things Last.fm browsing via Spotify #

## Desired features ##
  * Follow what people are currently playing. Each time a new song is played in Spotify, pick (override) song according to configuration.
  * Define probabilities for picking from different sources:
    * Spotify:
      * Allow to continue on current play queue.
      * Mix different playlists.
    * Last.fm:
      * Your own history.
      * Friends' history.
      * What friends are currently playing.

## Implementation ##
  * Detect when Spotify changes song (easy, mostly done in commercial-detection code already) and perform new play action.
  * Change Spotify song by regular launch of spotify.exe with URL.
  * Last.fm probably has web API, perhaps even python library that can be used to query information from friends.
  * Spotify playlists: Can they be queried via Spotify meta or viewed on web page? I assume so.