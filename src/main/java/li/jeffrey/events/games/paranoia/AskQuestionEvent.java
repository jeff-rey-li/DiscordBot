package li.jeffrey.events.games.paranoia;

import li.jeffrey.events.structure.PrivateMessageReceivedListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AskQuestionEvent extends PrivateMessageReceivedListener {
    public AskQuestionEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

	@Override
	public void doEvent(GenericEvent genericEvent) {
		PrivateMessageReceivedEvent event = (PrivateMessageReceivedEvent)genericEvent;
		
		String message = event.getMessage().getContentRaw().trim();
		String username = message.substring(0, message.indexOf(" "));
		User userRecievingQuestion = jda.getUserByTag(username);
		message = message.substring(message.indexOf(" ")).trim();
		
		String botMessageToUserToExpressQuestion = "%s asked: \"%s\"\nReact when you have finished answering the question.";
		
		String finalBotMessageToUserToExpressQuestion = String.format(botMessageToUserToExpressQuestion, event.getAuthor().getName(), message);
		
		Message messageSentToUserRecievingQuestion = userRecievingQuestion.openPrivateChannel().complete().sendMessage(finalBotMessageToUserToExpressQuestion).complete();
		messageSentToUserRecievingQuestion.addReaction("✅").complete();
		
		event.getChannel().sendMessage("You asked " + username + ": " + message).complete();
	}

	@Override
	public boolean shouldEventTrigger(GenericEvent genericEvent) {
		return genericEvent instanceof PrivateMessageReceivedEvent && !((PrivateMessageReceivedEvent)genericEvent).getAuthor().isBot();
	}
}
