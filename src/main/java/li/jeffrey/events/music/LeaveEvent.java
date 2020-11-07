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

public class LeaveEvent extends ReceivedEventListener {

    public LeaveEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isUserRequestingMusicBotLeave(GuildMessageReceivedEvent event) {
        return event.getMessage().getContentRaw().startsWith(prefix + "leave");
    }

    private boolean isMemberConnectedToSameChannel(Member member) {
        return MusicPlayer.getAudioManager().getConnectedChannel().equals(MusicCommonUtil.getInstance().getMemberConnectedVoiceChannel(member));
    }

    private void sendMusicBotNotConnectedMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("I'm not connected to a channel!");
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendUserNotConnectedToSameChannelMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("You must be connected to the same channel!");
        eb.setFooter("Made by Jeffrey Li");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void leaveVoiceChannel() {
        MusicPlayer.getAudioManager().closeAudioConnection();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
    	GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        if (!MusicCommonUtil.getInstance().isBotAlreadyConnectedToVoiceChannel()) {
            sendMusicBotNotConnectedMessage(event);
        } else if (!isMemberConnectedToSameChannel(event.getMember())) {
            sendUserNotConnectedToSameChannelMessage(event);
        } else {
        	leaveVoiceChannel();
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isUserRequestingMusicBotLeave((GuildMessageReceivedEvent) genericEvent);
    }
}
