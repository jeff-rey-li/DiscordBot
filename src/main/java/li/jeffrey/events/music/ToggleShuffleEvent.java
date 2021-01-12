package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class ToggleShuffleEvent extends ReceivedEventListener {

    public ToggleShuffleEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private void sendShuffleTurnedOnMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.LIGHT_GRAY);
        eb.setDescription("Shuffling queue...");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendShuffleTurnedOffMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.LIGHT_GRAY);
        eb.setDescription("Reverting queue back to original add order...");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        if (MusicPlayer.getInstance().toggleIsShuffle()) {
            SongAddData firstSong = SongQueue.getInstance().shuffleQueue(MusicPlayer.getInstance().getNowPlaying());
            sendShuffleTurnedOnMessage((GuildMessageReceivedEvent) genericEvent);
            MusicPlayer.getInstance().playSong(firstSong.getSong().makeClone(), firstSong.getMemberAdded(),
                    ((GuildMessageReceivedEvent) genericEvent).getChannel());
        } else {
            SongAddData firstSong =
                    SongQueue.getInstance().deleteShuffleQueue(MusicPlayer.getInstance().getNowPlaying());
            sendShuffleTurnedOffMessage((GuildMessageReceivedEvent) genericEvent);
            MusicPlayer.getInstance().playSong(firstSong.getSong().makeClone(), firstSong.getMemberAdded(),
                    ((GuildMessageReceivedEvent) genericEvent).getChannel());
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "shuffle") && !SongQueue.getInstance().isQueueEmpty();
    }
}
