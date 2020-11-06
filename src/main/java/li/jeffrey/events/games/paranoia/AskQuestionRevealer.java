package li.jeffrey.events.games.paranoia;

import li.jeffrey.constants.Constants;
import li.jeffrey.events.structure.PrivateMessageReactionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;

public class AskQuestionRevealer extends PrivateMessageReactionListener {

    public AskQuestionRevealer(JDA jda, String prefix) {
        super(jda, prefix);
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        PrivateMessageReactionAddEvent event = (PrivateMessageReactionAddEvent) genericEvent;

        if (Math.random() < 0.25) {
            sendLuckyMessageOfNotRevealingQuestion(event);
        } else {
            revealQuestionToEveryone(event);
        }
    }

    private void revealQuestionToEveryone(PrivateMessageReactionAddEvent event) {
        event.getChannel().sendMessage("Muwhaha your question will be revealed to everyone").complete();
        String message = event.getChannel().retrieveMessageById(event.getMessageId()).complete()
                .getContentRaw();
        message = message.replace("\nReact when you have finished answering the question", "");
        message += " to " + event.getUser().getName();
        jda.getGuildById(Constants.GUILD_ID).getTextChannelById(Constants.PARANOIA_REVEAL_CHANNEL_ID).sendMessage(message)
                .complete();
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        if (genericEvent instanceof PrivateMessageReactionAddEvent) {
            PrivateMessageReactionAddEvent event = (PrivateMessageReactionAddEvent) genericEvent;
            return !event.getUser().isBot() && event.getReactionEmote().getName().equals("✅");
        } else {
            return false;
        }
    }

    private void sendLuckyMessageOfNotRevealingQuestion(PrivateMessageReactionAddEvent event) {
        event.getChannel().sendMessage("You lucky duckling! You don't have to reveal your question.").complete();
        jda.getGuildById(Constants.GUILD_ID).getTextChannelById(Constants.PARANOIA_REVEAL_CHANNEL_ID)
                .sendMessage(event.getUser().getName() + " is a lucky gamer! SMH").complete();

    }
}
