package li.jeffrey.util;

import li.jeffrey.constants.Constants;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UserDetermination {
	private static UserDetermination userDetermination = null; 
	
	private UserDetermination () {
		
	}
	
	public static UserDetermination getInstance() {
		if(userDetermination == null) {
			userDetermination = new UserDetermination();
		}
		
		return userDetermination;
	}
	
	public boolean isAdmin(GuildMessageReceivedEvent event) {
        return event.getAuthor().getId().equals(Constants.ADMIN_ID);
    }
}
