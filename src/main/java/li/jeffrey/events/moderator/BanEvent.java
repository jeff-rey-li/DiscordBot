package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanEvent extends ReceivedEventListener {

    public BanEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminBanning(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "ban");
    }

    private void banUserAndNotifyChannel(GuildMessageReceivedEvent event, String username, String reason) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        if (reason.isEmpty()) {
            event.getChannel().sendMessage(member.getAsMention() + " has been banned! Reason: No reason given.").complete();
            member.ban(0).complete();
        } else {
            event.getChannel().sendMessage(member.getAsMention() + " has been banned! Reason: " + reason + ".").complete();
            member.ban(0, reason).complete();
        }
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        String reason = event.getMessage().getContentRaw().replace(message[0], "").replace(message[1], "").trim();
        banUserAndNotifyChannel(event, username, reason);
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isAdminBanning((GuildMessageReceivedEvent) genericEvent);
    }
}
