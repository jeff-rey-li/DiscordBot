package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONPlaylistWriter {

    private static JSONPlaylistWriter writer = null;

    private JSONPlaylistWriter() {
    }

    public static JSONPlaylistWriter getInstance() {
        if (writer == null) {
            writer = new JSONPlaylistWriter();
        }
        return writer;
    }

    private void writeJSONToFile(JSONObject jsonObject) {
        FileWriter file = null;
        try {
            file = new FileWriter("./playlists.txt");
            file.write(jsonObject.toString(4));
        } catch (IOException e) {
        } finally {
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
            }
        }
    }

    public void savePlaylistsToJSON() {
        List<String> playlists = SavedPlaylists.getInstance().getPlaylistNames();
        JSONObject jsonObject = new JSONObject();
        JSONArray allPlaylists = new JSONArray();
        for (int i = 0; i < playlists.size(); i++) {
            JSONArray songsInPlaylist = new JSONArray();
            List<AudioTrack> songList = SavedPlaylists.getInstance().getPlaylist(playlists.get(i));
            for (int j = 0; j < songList.size(); j++) {
                songsInPlaylist.put(songList.get(j).getInfo().uri);
            }
            JSONObject newObject = new JSONObject();
            newObject.put(playlists.get(i), songsInPlaylist);
            allPlaylists.put(newObject);
        }
        jsonObject.put("playlists", allPlaylists);
        writeJSONToFile(jsonObject);
    }

}
