package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.MusicCommonUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.function.Consumer;

public class PlaySongEvent extends ReceivedEventListener {

    public PlaySongEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isUserPlayingSong(GuildMessageReceivedEvent event) {

        return event.getMessage().getContentRaw().trim().split(" ")[0].equals(prefix + "play");
    }

    private String determineSearchWebsite(String searchPhrase) {
        if (!searchPhrase.contains("scsearch:") && !searchPhrase.contains("www.") && !searchPhrase.contains("https" + "://")) {
            searchPhrase = "ytsearch:" + searchPhrase;
        }
        return searchPhrase;
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
                createSongOptionsMessage(searchPhrase.replace("ytsearch:", ""), audioPlaylist, memberRequestingSong,
                        notificationChannel);
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    private void createSongOptionsMessage(String searchPhrase, AudioPlaylist songOptions, Member memberRequestingSong
            , TextChannel notificationChannel) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Play Song");
        eb.setColor(Color.BLUE);
        for (int i = 0; i < 5; i++) {
            AudioTrack song = songOptions.getTracks().get(i);
            String title = song.getInfo().title;
            String url = song.getInfo().uri;
            if (i == 0) {
                eb.addField("Search results:", "1. [" + title + "](" + url + ")", false);
            } else {
                eb.addField("", (i + 1) + ". [" + title + "](" + url + ")", false);
            }
        }
        eb.addField("Searched by:", memberRequestingSong.getAsMention(), false);
        sendSongOptionsMessage(eb, notificationChannel);
    }

    private void sendSongOptionsMessage(EmbedBuilder eb, TextChannel notificationChannel) {
        RestAction<Message> optionMessage = notificationChannel.sendMessage(eb.build());
        Consumer<Message> addReactionsToMessage = message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
            message.addReaction("4️⃣").queue();
            message.addReaction("5️⃣").queue();
        };
        optionMessage.queue(addReactionsToMessage);
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        if (MusicCommonUtil.getInstance().isMemberNotConnectedToChannel(event.getMember())) {
            MusicCommonUtil.getInstance().sendUserMustConnectToVoiceChannelMessage(event.getChannel());
            return;
        } else if (!MusicCommonUtil.getInstance().isBotAlreadyConnectedToVoiceChannel()) {
            VoiceChannel voiceChannel = MusicCommonUtil.getInstance().getMemberConnectedVoiceChannel(event.getMember());
            MusicCommonUtil.getInstance().joinVoiceChannel(voiceChannel);
            MusicCommonUtil.getInstance().sendBotJoinedChannelMessage(event.getChannel(), event.getMember());
        } else if (!MusicCommonUtil.getInstance().isMemberConnectedToSameChannel(event.getMember())) {
            MusicCommonUtil.getInstance().sendUserNotConnectedToSameChannelMessage(event.getChannel());
            return;
        }
        String songSearch =
                determineSearchWebsite(event.getMessage().getContentRaw().replace(prefix + "play", "").trim());
        searchSongOnline(songSearch, event.getMember(), event.getChannel());
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isUserPlayingSong((GuildMessageReceivedEvent) genericEvent);
    }
}
