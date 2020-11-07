package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UnbanEvent extends ReceivedEventListener {

    public UnbanEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminUnbanning(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "unban");
    }

    private void unbanToUserAndNotifyChannel(GuildMessageReceivedEvent event, String username, String reason) {
        User user = jda.getUserById(username);
        if (reason.isEmpty()) {
            event.getChannel().sendMessage(user.getAsMention() + " has been unbanned! Reason: No reason given.").complete();
        } else {
            event.getChannel().sendMessage(user.getAsMention() + " has been unbanned! Reason: " + reason + ".").complete();
        }
        event.getGuild().unban(user).complete();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        String reason = event.getMessage().getContentRaw().replace(message[0], "").replace(message[1], "").trim();
        unbanToUserAndNotifyChannel(event, username, reason);

    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isAdminUnbanning((GuildMessageReceivedEvent) genericEvent);
    }
}
