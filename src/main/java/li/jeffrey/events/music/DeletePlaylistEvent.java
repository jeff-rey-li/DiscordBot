package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class DeletePlaylistEvent extends ReceivedEventListener {

    public DeletePlaylistEvent(JDA jda, String prefix) {
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

    private void sendPlaylistDeletedSuccessfullyMessage(GuildMessageReceivedEvent event, String playlistName) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        eb.setDescription("The playlist **" + playlistName + "** was deleted successfully!");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        if (((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().split(" ").length == 1) {
            sendSpecifyPlaylistNameMessage((GuildMessageReceivedEvent) genericEvent);
            return;
        }
        String playlistName =
                ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().replace(prefix +
                        "deleteplaylist", "").trim();
        if (SavedPlaylists.getInstance().getPlaylist(playlistName) == null) {
            sendPlaylistDoesNotExistMessage((GuildMessageReceivedEvent) genericEvent);
            return;
        } else {
            SavedPlaylists.getInstance().deletePlaylist(playlistName);
            sendPlaylistDeletedSuccessfullyMessage((GuildMessageReceivedEvent) genericEvent, playlistName);
            JSONPlaylistWriter.getInstance().savePlaylistsToJSON();
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "deleteplaylist");
    }
}
