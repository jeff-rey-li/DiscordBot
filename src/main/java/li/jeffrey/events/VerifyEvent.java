package li.jeffrey.events;

import java.util.List;

import li.jeffrey.util.RoleFinder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VerifyEvent extends ListenerAdapter {
    public VerifyEvent(JDA jda, String prefix) {
    	
    }
    
    public boolean shouldAddMemberRole(GenericMessageReactionEvent event) {
    	return event.getChannel().getName().equals("verify") && !event.getUser().isBot();
    }
    
   

    public void onGenericMessageReaction(GenericMessageReactionEvent event) {
    	if (shouldAddMemberRole(event)) {
            Role memberRole = RoleFinder.getInstance().getRoleWithNameMember(event.getGuild(), "Member");
            if(memberRole != null) {
            	addRoleToCurrentUserAndRemoveReaction(event, memberRole);
            }
        }
    }

	private void addRoleToCurrentUserAndRemoveReaction(GenericMessageReactionEvent event, Role role) {
		event.getGuild().addRoleToMember(event.getMember().getId(), role).complete();
        event.getReaction().removeReaction(event.getUser()).complete();
	}
}
