package li.jeffrey.events;

import java.util.HashMap;
import java.util.Map;

import li.jeffrey.constants.Constants;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SpamPingEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;
    private Map<String, Integer> pingCount;

    public SpamPingEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
        pingCount = new HashMap<String, Integer>();
    }

    private boolean isBotPingMessage(GuildMessageReceivedEvent event) {
        return event.getAuthor().isBot() && event.getMessage().getContentRaw().startsWith("<");
    }

    private boolean isUserPingMessage(GuildMessageReceivedEvent event) {
        return event.getMessage().getContentRaw().startsWith("Now pinging:");
    }

    private String getUserName(GuildMessageReceivedEvent event) {
        String[] sentence = event.getMessage().getContentRaw().split(" ");

        UsernameSanitizer usernameSanitizer = UsernameSanitizer.getInstance();
        
        if (sentence.length == 1) {
            return usernameSanitizer.sanitizeUsername(sentence[0]);
        } else if (sentence.length == 3) {
            return usernameSanitizer.sanitizeUsername(sentence[2]);
        } else {
            return null;
        }
    }
    
    private boolean shouldContinuePinging(String username) {
    	return pingCount.keySet().contains(username);
    }

    private void sendMessageToUser(String username, GuildMessageReceivedEvent event, String message) {
        if(shouldContinuePinging(username)) {
	    	User userBeingPinged = jda.retrieveUserById(username).complete();
	
	        String finalMessage = String.format(message, userBeingPinged.getAsMention());
	        event.getChannel().sendMessage(finalMessage).complete();
	        
	        try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
    
    private void createIncrementPingCount(String username) {
    	pingCount.put(username, 1);
    }

    private void incrementPingCount(String username) {
    	if(pingCount.keySet().contains(username)) {
    		int pings = pingCount.get(username) + 1;
            pingCount.put(username, pings);
    	}
    }
  

    private boolean isAdminPinging(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + Constants.PING);
    }

    private boolean pingingShouldStop(String[] sentence) {
        return sentence[1].equals(Constants.STOP);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    	if (isBotPingMessage(event) || isUserPingMessage(event)) {
            String username = getUserName(event);
            if (username != null) {
                sendMessageToUser(username, event, "%s");
                incrementPingCount(username);
            }

        } else if (isAdminPinging(event)) {
        	UsernameSanitizer usernameSanitizer = UsernameSanitizer.getInstance();
        	
            try {
                String[] sentence = event.getMessage().getContentRaw().split(" ");
                if (pingingShouldStop(sentence)) {
                    String username = usernameSanitizer.sanitizeUsername(sentence[2]);
                    String message = "Stopped pinging %s. Pinged " + pingCount.get(username) + " times!";
                    sendMessageToUser(username, event, message);
                    removeIncrementPingCount(username);
                } else {
                    String username = usernameSanitizer.sanitizeUsername(sentence[1]);
                    sendMessageToUser(username, event, "Now pinging: %s.");
                    createIncrementPingCount(username);
                }
            } catch (Exception e) {
                return;
            }
        }
    }

	private void removeIncrementPingCount(String username) {
		pingCount.remove(username);
	}
}
