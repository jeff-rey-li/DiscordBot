package li.jeffrey.util;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.events.music.MusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class MusicCommonUtil {
    private static MusicCommonUtil musicCommonUtil = null;

    private MusicCommonUtil() {

    }

    public static MusicCommonUtil getInstance() {
        if (musicCommonUtil == null) {
            musicCommonUtil = new MusicCommonUtil();
        }

        return musicCommonUtil;
    }

    public boolean isBotAlreadyConnectedToVoiceChannel() {
        return MusicPlayer.getInstance().getAudioManager().getConnectedChannel() != null;
    }

    public VoiceChannel getMemberConnectedVoiceChannel(Member member) {
        return member.getVoiceState().getChannel();
    }

    public boolean isMemberConnectedToSameChannel(Member member) {
        return MusicPlayer.getInstance().getAudioManager().getConnectedChannel().equals(musicCommonUtil.getMemberConnectedVoiceChannel(member));
    }

    public boolean isMemberNotConnectedToChannel(Member member) {
        return musicCommonUtil.getMemberConnectedVoiceChannel(member) == null;
    }

    public void joinVoiceChannel(VoiceChannel channel) {
        MusicPlayer.getInstance().getAudioManager().setSendingHandler(MusicPlayer.getInstance().getMyHandler());
        MusicPlayer.getInstance().getAudioManager().openAudioConnection(channel);
    }

    public void sendMusicBotNotConnectedMessage(TextChannel channelToSendMessage) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("I'm not connected to a channel!");
        channelToSendMessage.sendMessage(eb.build()).queue();
    }

    public void sendUserNotConnectedToSameChannelMessage(TextChannel channelToSendMessage) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("You must be connected to the same channel!");
        channelToSendMessage.sendMessage(eb.build()).queue();
    }

    public void sendUserMustConnectToVoiceChannelMessage(TextChannel channelToSendMessage) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("You must be in a channel to play music!");
        channelToSendMessage.sendMessage(eb.build()).queue();
    }

    public void sendMusicBotAlreadyConnectedMessage(TextChannel channelToSendMessage) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("I'm already connected to a channel!");
        channelToSendMessage.sendMessage(eb.build()).queue();
    }

    public void sendBotJoinedChannelMessage(TextChannel channelToSendMessage, Member memberRequestingBot) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.YELLOW);
        eb.setTitle("Joined Voice Channel:");
        eb.setDescription(musicCommonUtil.getMemberConnectedVoiceChannel(memberRequestingBot).getName());
        channelToSendMessage.sendMessage(eb.build()).queue();
    }

    public void sendNewSongQueuedMessage(AudioTrack song, Member memberRequestingSong, TextChannel channelToSendMessage) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        String url = song.getInfo().uri;
        String trackTitle = song.getInfo().title;
        eb.setTitle("Queued Song");
        String vidID = url;
        vidID = vidID.substring(32);
        if (song instanceof YoutubeAudioTrack) {
            eb.setThumbnail("https://img.youtube.com/vi/" + vidID + "/sddefault.jpg");
        }
        eb.setDescription("[" + trackTitle + "](" + url + ")\nAdded by: " + memberRequestingSong.getAsMention());
        if (MusicPlayer.getInstance().getPlayer().isPaused()) {
            eb.addField("", "The player is currently paused. Do !play to contine playing music.", false);
        }
        channelToSendMessage.sendMessage(eb.build()).complete();
    }

    public void sendSongAlreadyInQueueMessage(AudioTrack song, TextChannel channelToSendMessage) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("Error");
        String url = song.getInfo().uri;
        String trackTitle = song.getInfo().title;
        String vidID = url;
        vidID = vidID.substring(32);
        eb.setDescription("This song is already in the queue!\n[" + trackTitle + "](" + url + ")");
        channelToSendMessage.sendMessage(eb.build()).complete();
    }

}
