package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.MusicCommonUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class JoinEvent extends ReceivedEventListener {

    public JoinEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isUserRequestingMusicBotJoin(GuildMessageReceivedEvent event) {
        return event.getMessage().getContentRaw().startsWith(prefix + "join");
    }

    private boolean isMemberNotConnectedToChannel(Member member) {
        return MusicCommonUtil.getInstance().getMemberConnectedVoiceChannel(member) == null;
    }

    private void sendUserMustConnectToVoiceChannelMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("You must be in a channel to play music!");
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendMusicBotAlreadyConnectedMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("I'm already connected to a channel!");
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendBotJoinedChannelMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.YELLOW);
        eb.setTitle("Joined Voice Channel:");
        eb.setDescription(MusicCommonUtil.getInstance().getMemberConnectedVoiceChannel(event.getMember()).getName());
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void joinVoiceChannel(VoiceChannel channel) {
        MusicPlayer.getAudioManager().setSendingHandler(MusicPlayer.getMyHandler());
        MusicPlayer.getAudioManager().openAudioConnection(channel);
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
    	GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        if (isMemberNotConnectedToChannel(event.getMember())) {
            sendUserMustConnectToVoiceChannelMessage(event);
        }
        else if (MusicCommonUtil.getInstance().isBotAlreadyConnectedToVoiceChannel()) {
            sendMusicBotAlreadyConnectedMessage(event);
        } else {
        	VoiceChannel voiceChannel = MusicCommonUtil.getInstance().getMemberConnectedVoiceChannel(event.getMember());
        	joinVoiceChannel(voiceChannel);
        	sendBotJoinedChannelMessage(event);
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isUserRequestingMusicBotJoin((GuildMessageReceivedEvent) genericEvent);
    }
}
