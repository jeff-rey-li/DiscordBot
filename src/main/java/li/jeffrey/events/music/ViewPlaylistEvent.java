package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class ViewPlaylistEvent extends ReceivedEventListener {

    public ViewPlaylistEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private void sendSpecifyPlaylistNameMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("Please specify a playlist name");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendPlaylistDoesNotExistMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("This playlist does not exist");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private EmbedBuilder createViewPlaylistMessage(List<AudioTrack> playlist, String playlistName, int startIndex,
                                                   int endIndex) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLACK);
        boolean firstMessage = false;
        if (startIndex == 0) {
            eb.setTitle("View Playlist");
            firstMessage = true;
        }
        String songList = "";
        for (int i = startIndex; i < endIndex; i++) {
            String songName = playlist.get(i).getInfo().title;
            if (songName.length() > 55) {
                songName = songName.substring(0, 56) + " ...";
            }
            songList += "\n" + (i + 1) + ". " + songName;
        }
        if (firstMessage) {
            eb.addField(playlistName, songList, false);
        } else {
            eb.setDescription(songList);
        }
        return eb;
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        if (((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().split(" ").length == 1) {
            sendSpecifyPlaylistNameMessage((GuildMessageReceivedEvent) genericEvent);
            return;
        }
        String playlistName =
                ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().replace(prefix +
                        "viewplaylist", "").trim();
        List<AudioTrack> playlist = SavedPlaylists.getInstance().getPlaylist(playlistName);
        if (playlist == null) {
            sendPlaylistDoesNotExistMessage((GuildMessageReceivedEvent) genericEvent);
            return;
        } else {
            int numMessagesNeeded = (int) Math.ceil(playlist.size() / 10.0);
            for (int i = 0; i < numMessagesNeeded; i++) {
                if (i != numMessagesNeeded - 1) {
                    ((GuildMessageReceivedEvent) genericEvent).getChannel().sendMessage(createViewPlaylistMessage(playlist, playlistName, i * 10, (i + 1) * 10).build()).queue();
                } else {
                    ((GuildMessageReceivedEvent) genericEvent).getChannel().sendMessage(createViewPlaylistMessage(playlist, playlistName, i * 10, playlist.size()).build()).queue();
                }
            }
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "viewplaylist");
    }
}
