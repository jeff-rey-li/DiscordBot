package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.HashSet;
import java.util.function.Consumer;

public class SongChoiceListener extends ReceivedEventListener {

    public SongChoiceListener(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isUserChoosingSong(GuildMessageReactionAddEvent event) {
        RestAction<Message> messageWithUserReaction = event.retrieveMessage();
        boolean isMusicSelectionMessage = true;
        Consumer<Message> checkIfMessageIsSongSelectionMessage = message -> {
            if (message.getEmbeds().isEmpty())
                isMusicSelectionMessage = false;
            if (!message.getEmbeds().get(0).getTitle().equals("Play Song"))
                isMusicSelectionMessage = false;
        };
        System.out.println(isMusicSelectionMessage);
        messageWithUserReaction.queue(checkIfMessageIsSongSelectionMessage);
        boolean isUserReactionValid = true;
        HashSet<String> validReactions = new HashSet<String>();
        validReactions.add("1️⃣");
        validReactions.add("2️⃣");
        validReactions.add("3️⃣");
        validReactions.add("4️⃣");
        validReactions.add("5️⃣");
         if (!validReactions.contains(event.getReactionEmote().getName()))
             isUserReactionValid = false;
        return !event.getUser().isBot() && isMusicSelectionMessage && isUserReactionValid;
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {

    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return false;
    }
}
