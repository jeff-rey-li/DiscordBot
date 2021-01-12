package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JSONPlaylistReader {

    private static JSONPlaylistReader writer = null;

    private JSONPlaylistReader() {
    }

    public static JSONPlaylistReader getInstance() {
        if (writer == null) {
            writer = new JSONPlaylistReader();
        }
        return writer;
    }

    private void addSongToTracks(String url, List<AudioTrack> tracks) {
        MusicPlayer.getInstance().getPlayerManager().loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                tracks.add(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
            }

            @Override
            public void noMatches() {
            }

            @Override
            public void loadFailed(FriendlyException e) {
            }
        });
    }

    public void readPreviousPlaylistsFile() {
        File file = new File("./playlists.txt");
        String data = "";
        try {
            data = Files.readString(Paths.get("./playlists.txt"));
        } catch (IOException e) {
            return;
        }
        JSONObject jsonObject = new JSONObject(data);
        JSONArray playlists = (JSONArray) jsonObject.get("playlists");
        for (int i = 0; i < playlists.length(); i++) {
            JSONObject playlist = (JSONObject) playlists.get(i);
            String playlistName = (String) playlist.keySet().toArray()[0];
            JSONArray songs = (JSONArray) playlist.get(playlistName);
            List<AudioTrack> tracks = new ArrayList<AudioTrack>();
            for (int j = 0; j < songs.length(); j++) {
                addSongToTracks((String) songs.get(j), tracks);
            }
            SavedPlaylists.getInstance().saveNewPlaylist(playlistName, tracks);
        }
    }

}
