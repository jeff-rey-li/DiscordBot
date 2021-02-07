package li.jeffrey.events.music;

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
    
    private boolean playListIsNotSpecified(GenericEvent genericEvent) {
    	return ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().split(" ").length == 1;
    }
    
    private String extractPlaylistNameFromEvent(GenericEvent genericEvent) {
    	return ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().replace(prefix +
                        "deleteplaylist", "").trim();
    }
    
    private boolean playListExists(String playlistName) {
    	return SavedPlaylists.getInstance().getPlaylist(playlistName) != null;
    }
    
    @Override
    public void doEvent(GenericEvent genericEvent) {
        if (playListIsNotSpecified(genericEvent)) {
            sendSpecifyPlaylistNameMessage((GuildMessageReceivedEvent) genericEvent);
        }
        
        else {
        	String playlistName = extractPlaylistNameFromEvent(genericEvent);
        	 if (playListExists(playlistName)) {
        		 SavedPlaylists.getInstance().deletePlaylist(playlistName);
                 sendPlaylistDeletedSuccessfullyMessage((GuildMessageReceivedEvent) genericEvent, playlistName);
                 JSONPlaylistWriter.getInstance().savePlaylistsToJSON();
             } else {
            	 sendPlaylistDoesNotExistMessage((GuildMessageReceivedEvent) genericEvent);
             }
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "deleteplaylist");
    }
}
