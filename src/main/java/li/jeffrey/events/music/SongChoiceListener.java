package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class SongChoiceListener extends ReceivedEventListener {
    public SongChoiceListener(JDA jda, String prefix) {
        super(jda, prefix);
    }

    

    @Override
    public void doEvent(GenericEvent genericEvent) {
    	GenericMessageReactionEvent event = (GenericMessageReactionEvent)genericEvent;
    	
    	Message retrievedMessage = event.retrieveMessage().complete();
    	
    	String reactionEmote = event.getReactionEmote().getName();
        String toPlay;
        switch (reactionEmote) {
            case "1ï¸�âƒ£":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(0).getValue();
                break;
            case "2ï¸�âƒ£":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(1).getValue();
                break;
            case "3ï¸�âƒ£":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(2).getValue();
                break;
            case "4ï¸�âƒ£":
                toPlay = retrievedMessage.getEmbeds().get(0).getFields().get(3).getValue();
                break;
            case "5ï¸�âƒ£":
                toPlay =retrievedMessage.getEmbeds().get(0).getFields().get(4).getValue();
                break;
            default:
                return;
        }
    }

	@Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        if(!(genericEvent instanceof GenericMessageReactionEvent)) {
        	return false;
        }
        
        GenericMessageReactionEvent event = (GenericMessageReactionEvent)genericEvent;
    	
        
    	return isNotBot(event) && isPlaySong(event);
    }

	private boolean isPlaySong(GenericMessageReactionEvent event) {
		List<MessageEmbed> embededMessages = event.retrieveMessage().complete().getEmbeds();
		
		return !embededMessages.isEmpty() && embededMessages.get(0).getTitle().equalsIgnoreCase("Play Song");
	}

	private boolean isNotBot(GenericMessageReactionEvent event) {
		return !event.getUser().isBot();
	}
}
