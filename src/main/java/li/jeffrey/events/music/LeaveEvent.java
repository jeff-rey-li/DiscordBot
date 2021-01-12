package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.MusicCommonUtil;
import net.dv8tion.jda.api.JDA;
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

    private void leaveVoiceChannel() {
        MusicPlayer.getInstance().getAudioManager().closeAudioConnection();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
    	GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        if (!MusicCommonUtil.getInstance().isBotAlreadyConnectedToVoiceChannel()) {
            MusicCommonUtil.getInstance().sendMusicBotNotConnectedMessage(event.getChannel());
        } else if (!MusicCommonUtil.getInstance().isMemberConnectedToSameChannel(event.getMember())) {
            MusicCommonUtil.getInstance().sendUserNotConnectedToSameChannelMessage(event.getChannel());
        } else {
            SongQueue.getInstance().resetQueue();
            MusicPlayer.getInstance().getPlayer().stopTrack();
        	leaveVoiceChannel();
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isUserRequestingMusicBotLeave((GuildMessageReceivedEvent) genericEvent);
    }
}
