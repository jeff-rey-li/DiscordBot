package li.jeffrey.events;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class ListenerEvent extends ListenerAdapter{
	public abstract void doEvent(GenericEvent genericEvent);
	public abstract boolean shouldEventTrigger(GenericEvent genericEvent);
	
    protected JDA jda;
    protected String prefix;

    public ListenerEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
    }
	
	@Override 
	public final void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if(shouldEventTrigger(event)) {
			doEvent(event);
		}
	}
}
