package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class ToggleRepeatEvent extends ReceivedEventListener {

    public ToggleRepeatEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private void sendRepeatTurnedOnMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.LIGHT_GRAY);
        eb.setDescription("Now looping queue");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRepeatTurnedOffMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.LIGHT_GRAY);
        eb.setDescription("Music will now stop playing after the last song in the queue");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        if (MusicPlayer.getInstance().toggleIsRepeat()) {
            sendRepeatTurnedOnMessage((GuildMessageReceivedEvent) genericEvent);
            if (MusicPlayer.getInstance().getPlayer().getPlayingTrack() == null && !SongQueue.getInstance().isQueueEmpty()) {
                SongAddData playNext = SongQueue.getInstance().getSongAtIndex(0);
                MusicPlayer.getInstance().playSong(playNext.getSong().makeClone(), playNext.getMemberAdded(),
                        ((GuildMessageReceivedEvent) genericEvent).getChannel());
            }
        } else {
            sendRepeatTurnedOffMessage((GuildMessageReceivedEvent) genericEvent);
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "repeat") && !SongQueue.getInstance().isQueueEmpty();
    }
}
