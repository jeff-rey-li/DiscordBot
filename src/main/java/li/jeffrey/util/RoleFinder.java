package li.jeffrey.util;

import java.util.List;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

public class RoleFinder {
	private static RoleFinder roleFinder = null; 
	
	private RoleFinder () {
		
	}
	
	public static RoleFinder getInstance() {
		if(roleFinder == null) {
			roleFinder = new RoleFinder();
		}
		
		return roleFinder;
	}
	
	public Role getRoleWithNameMember(GenericMessageReactionEvent event, String roleName) {
	    List<Role> memberRoleNames = event.getGuild().getRolesByName(roleName, true);
	    if(memberRoleNames.size() == 0) {
	    	return null;
	    } else {
	    	return memberRoleNames.get(0);
	    }
	}
	
	public Role getRoleWithNameMember(GuildMessageReceivedEvent event, String roleName) {
    	List<Role> memberRoleNames = event.getGuild().getRolesByName(roleName, true);
    	if(memberRoleNames.size() == 0) {
    		return null;
    	} else {
    		return memberRoleNames.get(0);
    	}
    }
}
