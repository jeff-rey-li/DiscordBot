package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.MusicCommonUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class SongChoiceListener extends ReceivedEventListener {

    public SongChoiceListener(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isPlaySong(GuildMessageReactionAddEvent event) {
        List<MessageEmbed> embededMessages = event.retrieveMessage().complete().getEmbeds();

        return !embededMessages.isEmpty() && embededMessages.get(0).getTitle().equalsIgnoreCase("Play Song");
    }

    private boolean isNotBot(GuildMessageReactionAddEvent event) {
        return !event.getUser().isBot();
    }

    private void searchSongOnline(String searchPhrase, Member memberRequestingSong, TextChannel notificationChannel) {
        MusicPlayer.getInstance().getPlayerManager().loadItem(searchPhrase, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                boolean success = SongQueue.getInstance().addSong(audioTrack, memberRequestingSong);
                if (success) {
                    MusicCommonUtil.getInstance().sendNewSongQueuedMessage(audioTrack, memberRequestingSong,
                            notificationChannel);
                    if (MusicPlayer.getInstance().getPlayer().getPlayingTrack() == null) {
                        MusicPlayer.getInstance().playSong(audioTrack, memberRequestingSong, notificationChannel);
                    }
                } else {
                    MusicCommonUtil.getInstance().sendSongAlreadyInQueueMessage(audioTrack, notificationChannel);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReactionAddEvent event = (GuildMessageReactionAddEvent) genericEvent;
        Message retrievedMessage = event.retrieveMessage().complete();
        String reactionEmote = event.getReactionEmote().getName();
        String toPlay;
        switch (reactionEmote) {
            case "1️⃣":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(0).getValue();
                break;
            case "2️⃣":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(1).getValue();
                break;
            case "3️⃣":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(2).getValue();
                break;
            case "4️⃣":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(3).getValue();
                break;
            case "5️⃣":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(4).getValue();
                break;
            default:
                return;
        }
        String songLink = toPlay.substring(toPlay.indexOf("https://"), toPlay.length() - 1);
        searchSongOnline(songLink, event.getMember(), event.getChannel());
        retrievedMessage.delete().queue();
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        if (!(genericEvent instanceof GuildMessageReactionAddEvent)) {
            return false;
        }

        GuildMessageReactionAddEvent event = (GuildMessageReactionAddEvent) genericEvent;


        return isNotBot(event) && isPlaySong(event);
    }
}
