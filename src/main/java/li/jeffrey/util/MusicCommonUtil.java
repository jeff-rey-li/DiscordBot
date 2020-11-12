package li.jeffrey.util;

import li.jeffrey.events.music.MusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
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

    public void sendMusicBotNotConnectedMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("I'm not connected to a channel!");
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    public void sendUserNotConnectedToSameChannelMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("You must be connected to the same channel!");
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    public void sendUserMustConnectToVoiceChannelMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("You must be in a channel to play music!");
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    public void sendMusicBotAlreadyConnectedMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("I'm already connected to a channel!");
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    public void sendBotJoinedChannelMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.YELLOW);
        eb.setTitle("Joined Voice Channel:");
        eb.setDescription(musicCommonUtil.getMemberConnectedVoiceChannel(event.getMember()).getName());
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
