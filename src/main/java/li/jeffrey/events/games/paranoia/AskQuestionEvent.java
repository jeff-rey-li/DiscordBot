package li.jeffrey.events.games.paranoia;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AskQuestionEvent extends ListenerAdapter {

    private JDA jda;

    public AskQuestionEvent(JDA jda, String prefix) {
        this.jda = jda;
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String message = event.getMessage().getContentRaw().trim();
        ;
        String[] content = message.split(" ");
        if (content[0] == null) {
            return;
        }
        if (content[1] == null) {
            return;
        }
        content[1] = content[1].replaceAll("[<>/@!]", "");
        User toMessage = jda.getUserByTag(content[0]);
        message = message.replace(content[0], "");
        message = message.trim();
        Message question = toMessage.openPrivateChannel().complete().sendMessage(event.getAuthor().getName()
                + " asked: \"" + message + "\"" + "\nReact when you have finished answering the question").complete();
        question.addReaction("✅").complete();
        event.getChannel().sendMessage("You asked " + content[0] + ": " + message).complete();
    }

    public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        if (event.getReactionEmote().getName().equals("✅")) {
            if (Math.random() < 0.4) {
                event.getChannel().sendMessage("You lucky duckling! You don't have to reveal your question.")
                        .complete();
                jda.getGuildById("657977250771238912").getTextChannelById("711067180313214976")
                        .sendMessage(event.getUser().getName() + " is a lucky gamer! SMH").complete();
            } else {
                event.getChannel().sendMessage("Muwhaha your question will be revealed to everyone").complete();
                String message = event.getChannel().retrieveMessageById(event.getMessageId()).complete()
                        .getContentRaw();
                message = message.replace("\nReact when you have finished answering the question", "");
                message += " to " + event.getUser().getName();
                jda.getGuildById("657977250771238912").getTextChannelById("711067180313214976").sendMessage(message)
                        .complete();
            }

        }
    }

}
