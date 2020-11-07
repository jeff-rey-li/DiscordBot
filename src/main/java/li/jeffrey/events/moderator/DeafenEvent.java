package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DeafenEvent extends ReceivedEventListener {

    public DeafenEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminDeafening(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "deafen");
    }

    private void deafenUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        member.deafen(true).complete();
        event.getChannel().sendMessage("Deafened " + member.getAsMention() + "!").complete();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        deafenUserAndNotifyChannel(event, username);
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isAdminDeafening((GuildMessageReceivedEvent) genericEvent);
    }

}
