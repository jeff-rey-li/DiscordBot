package li.jeffrey.events;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SongRequestEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;
    
    private static final String TITLE = "Song Request";

    public SongRequestEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
    }
    
    private void createSongRequest(GuildMessageReceivedEvent event) {
    	EmbedBuilder eb = new EmbedBuilder().setColor(Color.blue)
				.setTitle(TITLE)
				.addField("", event.getMessage().getContentRaw().substring(9).trim(), false)
				.addField("", "Requested by: " + event.getAuthor().getAsMention(), false);

    	event.getChannel().deleteMessageById(event.getMessageId()).complete();
    	event.getChannel().sendMessage(eb.build()).complete();
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!event.getChannel().getName().equals("song-requests") || event.getAuthor().isBot()) {
            return;
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equals(prefix + "request")) {
        	createSongRequest(event);
            
        }
    }
}
