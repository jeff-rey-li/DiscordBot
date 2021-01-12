package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class ViewAllPlaylistsEvent extends ReceivedEventListener {

    public ViewAllPlaylistsEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        List<String> playlistNames = SavedPlaylists.getInstance().getPlaylistNames();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLACK);
        eb.setTitle("Saved Playlists");
        String playlists = "";
        if (playlistNames.isEmpty()) {
            playlists += "(*none*)";
        } else {
            for (int i = 0; i < playlistNames.size(); i++) {
                if (i == 0) {
                    playlists += (i + 1) + ". " + playlistNames.get(i);
                } else {
                    playlists += "\n" + (i + 1) + ". " + playlistNames.get(i);
                }
            }
        }
        eb.setDescription(playlists + "\n---");
        eb.addField("To save a playlist, type", prefix + "saveplaylist " + "[new playlist name]", false);
        eb.addField("To view a playlist, type", prefix + "viewplaylist " + "[playlist name] (case sensitive)", false);
        eb.addField("To delete a playlist, type", prefix + "deleteplaylist " + "[playlist name] (case sensitive)", false);
        eb.addField("To load a playlist, type", prefix + "loadplaylist " + "[playlist name] (case sensitive)", false);
        ((GuildMessageReceivedEvent) genericEvent).getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "playlists");
    }
}
