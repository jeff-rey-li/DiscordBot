package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SavePlaylistEvent extends ReceivedEventListener {

    public SavePlaylistEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private void sendNoSongsInQueueMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("There is no queue currently playing");
        event.getChannel().sendMessage(eb.build()).queue();

    }

    private void sendNoPlaylistNameMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("You must give the playlist a name");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendSavedPlaylistMessage(GuildMessageReceivedEvent event, String playlistName) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        eb.setDescription("Saved queue as **" + playlistName + "**. To view playlists, type " + prefix + "playlists");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        List<SongAddData> queue = SongQueue.getInstance().getQueue();
        String[] userMessage = ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().split(" ");
        if (queue.isEmpty()) {
            sendNoSongsInQueueMessage((GuildMessageReceivedEvent) genericEvent);
        } else if (userMessage.length == 1) {
            sendNoPlaylistNameMessage((GuildMessageReceivedEvent) genericEvent);
        } else {
            List<AudioTrack> tracks = new ArrayList<AudioTrack>();
            queue.forEach(e -> tracks.add(e.getSong()));
            String playlistName =
                    ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().replace(prefix +
                            "saveplaylist", "").trim();
            SavedPlaylists.getInstance().saveNewPlaylist(playlistName, tracks);
            sendSavedPlaylistMessage((GuildMessageReceivedEvent) genericEvent, playlistName);
            JSONPlaylistWriter.getInstance().savePlaylistsToJSON();
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "saveplaylist");
    }
}
