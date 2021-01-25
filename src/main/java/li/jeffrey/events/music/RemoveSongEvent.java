package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RemoveSongEvent extends ReceivedEventListener {

    public RemoveSongEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private void sendRemovedSongMessage(SongAddData removedSong, GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Removed Song");
        AudioTrack song = removedSong.getSong();
        if (song instanceof YoutubeAudioTrack) {
            String vidID = removedSong.getSong().getInfo().uri;
            vidID = vidID.substring(32);
            eb.setThumbnail("https://img.youtube.com/vi/" + vidID + "/sddefault.jpg");
        }
        eb.setDescription("[" + song.getInfo().title + "](" + song.getInfo().uri + ")\nAdded by: " + removedSong.getMemberAdded().getAsMention() + "\nRemoved by: " + event.getMember().getAsMention());
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        String removePhrase =
                ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().replace(prefix + "remove",
                        "").trim();
        SongAddData removedData;
        try {
            int songNumberToRemove = Integer.parseInt(removePhrase);
            removedData = SongQueue.getInstance().removeSong(songNumberToRemove);
        } catch (NumberFormatException e) {
            removedData = SongQueue.getInstance().removeSong(removePhrase);
        }
        if (removedData == null) {
            // TODO: Send error message, no song was found
        } else {
            sendRemovedSongMessage(removedData, (GuildMessageReceivedEvent) genericEvent);
            if (MusicPlayer.getInstance().getNowPlaying().getInfo().title.equals(removedData.getSong().getInfo().title)) {
                if (!SongQueue.getInstance().isQueueEmpty()) {
                    SongAddData nextSongToPlayIfRemovedSongWasPlaying =
                            SongQueue.getInstance().getSongAtIndex(removedData.getLastQueueIndexWhenRemoved());
                    MusicPlayer.getInstance().playSong(nextSongToPlayIfRemovedSongWasPlaying.getSong(),
                            nextSongToPlayIfRemovedSongWasPlaying.getMemberAdded(),
                            ((GuildMessageReceivedEvent) genericEvent).getChannel());
                } else {
                    MusicPlayer.getInstance().getPlayer().stopTrack();
                }
            }
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "remove");
    }

}
