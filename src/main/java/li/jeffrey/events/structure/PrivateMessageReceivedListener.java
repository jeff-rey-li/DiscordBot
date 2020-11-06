package li.jeffrey.events.structure;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public abstract class PrivateMessageReceivedListener extends ListenerEvent {
    public PrivateMessageReceivedListener(JDA jda, String prefix) {
        super(jda, prefix);
    }

    @Override
    public final void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (shouldEventTrigger(event)) {
            doEvent(event);
        }
    }

}
