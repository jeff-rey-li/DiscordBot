package li.jeffrey.events.games;

import li.jeffrey.events.structure.RecievedEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WouldYouRatherEvent extends RecievedEventListener {
    public WouldYouRatherEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

	@Override
	public void doEvent(GenericEvent genericEvent) {
		GuildMessageReceivedEvent event = (GuildMessageReceivedEvent)genericEvent;
		String[] possibleOptions = getPossibleOptionsArray(event);
        for (String option: possibleOptions) {
        	reactToOptionMessageWithCheckMark(option, event);
        }
	}

	@Override
	public boolean shouldEventTrigger(GenericEvent genericEvent) {
		return ((GuildMessageReceivedEvent)genericEvent).getMessage().getContentRaw().contains(prefix + "wyr");
	}
	
	private String[] getPossibleOptionsArray(GuildMessageReceivedEvent event) {
	    return event.getMessage().getContentRaw().replace(prefix + "wyr", "").trim().split("\"");
	}
	    
	private void reactToOptionMessageWithCheckMark(String option, GuildMessageReceivedEvent event) {
	    event.getChannel().sendMessage(option).complete().addReaction("✅").complete();
	}
}
