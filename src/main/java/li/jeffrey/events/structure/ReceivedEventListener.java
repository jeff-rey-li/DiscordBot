package li.jeffrey.events.structure;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;

public abstract class ReceivedEventListener extends ListenerEvent {
    public ReceivedEventListener(JDA jda, String prefix) {
        super(jda, prefix);
    }

    @Override
    public final void onGenericEvent(GenericEvent event) {
        if (shouldEventTrigger(event)) {
            doEvent(event);
        }
    }

}
