package li.jeffrey.events.structure;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class ReceivedEventListener extends ListenerEvent {
    public ReceivedEventListener(JDA jda, String prefix) {
        super(jda, prefix);
    }

    @Override
    public final void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (shouldEventTrigger(event)) {
            doEvent(event);
        }
    }

}
