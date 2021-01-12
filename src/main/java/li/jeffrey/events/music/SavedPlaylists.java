package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedPlaylists {

    private static SavedPlaylists savedPlaylists;
    private HashMap<String, List<AudioTrack>> playlists;

    private SavedPlaylists() {
        playlists = new HashMap<String, List<AudioTrack>>();
    }

    public static SavedPlaylists getInstance() {
        if (savedPlaylists == null) {
            savedPlaylists = new SavedPlaylists();
        }

        return savedPlaylists;
    }

    public void saveNewPlaylist(String playlistName, List<AudioTrack> playlist) {
        playlists.put(playlistName, playlist);
    }

    public List<AudioTrack> getPlaylist(String playlistName) {
        return playlists.get(playlistName);
    }

    public void deletePlaylist(String playlistName) {
        playlists.remove(playlistName);
    }

    public List<String> getPlaylistNames() {
        List<String> playlistNames = new ArrayList<String>();
        playlists.forEach((playlistName, playlist) -> playlistNames.add(playlistName));
        return playlistNames;
    }

}
