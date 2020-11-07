package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DeafenEvent extends ReceivedEventListener {

    public DeafenEvent(JDA jda, String prefix) { super(jda, prefix); }

    private boolean isAdminDeafening(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "deafen");
    }

    private boolean isAdminUndeafening(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "undeafen");
    }

    private void deafenUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        member.deafen(true).complete();
        event.getChannel().sendMessage("Deafened " + member.getAsMention() + "!").complete();
    }

    private void undeafenUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        member.deafen(false).complete();
        event.getChannel().sendMessage("Undeafened " + member.getAsMention() + "!").complete();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = "";
        if (message.length > 1) {
            username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        }
        if (isAdminDeafening(event)) {
            deafenUserAndNotifyChannel(event, username);
        } else if (isAdminUndeafening(event)) {
            undeafenUserAndNotifyChannel(event, username);
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && (isAdminDeafening((GuildMessageReceivedEvent) genericEvent) || isAdminUndeafening((GuildMessageReceivedEvent) genericEvent));
    }

}
