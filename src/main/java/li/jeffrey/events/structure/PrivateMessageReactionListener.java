package li.jeffrey.events.structure;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;

public abstract class PrivateMessageReactionListener extends ListenerEvent {
	public PrivateMessageReactionListener(JDA jda, String prefix) {
		super(jda, prefix);
	}
	
	@Override
    public final void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) { 
		if(shouldEventTrigger(event)) {
			doEvent(event);
		}
	}
	
}
