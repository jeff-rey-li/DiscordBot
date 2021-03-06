package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UndeafenEvent extends ReceivedEventListener {

    public UndeafenEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminUndeafening(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "undeafen");
    }

    private void undeafenUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().getMemberById(event.getAuthor().getId());
        member.deafen(false).queue();
        event.getChannel().sendMessage("Undeafened " + member.getAsMention() + "!").queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        undeafenUserAndNotifyChannel(event, username);
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isAdminUndeafening((GuildMessageReceivedEvent) genericEvent);
    }
}
