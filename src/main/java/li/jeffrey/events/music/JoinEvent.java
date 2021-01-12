package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.MusicCommonUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoinEvent extends ReceivedEventListener {

    public JoinEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isUserRequestingMusicBotJoin(GuildMessageReceivedEvent event) {
        return event.getMessage().getContentRaw().startsWith(prefix + "join");
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        if (MusicCommonUtil.getInstance().isMemberNotConnectedToChannel(event.getMember())) {
            MusicCommonUtil.getInstance().sendUserMustConnectToVoiceChannelMessage(event.getChannel());
        } else if (MusicCommonUtil.getInstance().isBotAlreadyConnectedToVoiceChannel()) {
            MusicCommonUtil.getInstance().sendMusicBotAlreadyConnectedMessage(event.getChannel());
        } else {
            VoiceChannel voiceChannel = MusicCommonUtil.getInstance().getMemberConnectedVoiceChannel(event.getMember());
            MusicCommonUtil.getInstance().joinVoiceChannel(voiceChannel);
            MusicCommonUtil.getInstance().sendBotJoinedChannelMessage(event.getChannel(), event.getMember());
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isUserRequestingMusicBotJoin((GuildMessageReceivedEvent) genericEvent);
    }
}
