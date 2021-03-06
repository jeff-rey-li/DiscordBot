package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.RoleFinder;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ChatUnmuteEvent extends ReceivedEventListener {

    public ChatUnmuteEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminUnmuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "unmute");
    }

    private void removeMutedRoleToUserAndNotifyChannel(GuildMessageReceivedEvent event, Role role, String username) {
        Member member = event.getGuild().getMemberById(event.getAuthor().getId());
        event.getGuild().removeRoleFromMember(member, role).queue();
        event.getChannel().sendMessage("Unmuted " + member.getAsMention() + "!").queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        Role role = RoleFinder.getInstance().getRoleWithNameMember(event.getGuild(), "Muted");
        removeMutedRoleToUserAndNotifyChannel(event, role, username);
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isAdminUnmuting((GuildMessageReceivedEvent) genericEvent);
    }
}
