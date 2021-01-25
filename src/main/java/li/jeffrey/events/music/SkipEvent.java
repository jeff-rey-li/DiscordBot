package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SkipEvent extends ReceivedEventListener {

    public SkipEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        MusicPlayer.getInstance().getPlayer().stopTrack();
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().trim().equals(prefix + "skip");
    }
}
