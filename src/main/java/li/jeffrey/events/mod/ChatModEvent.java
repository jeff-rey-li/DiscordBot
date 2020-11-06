package li.jeffrey.events.mod;

import li.jeffrey.events.structure.RecievedEventListener;
import li.jeffrey.util.RoleFinder;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatModEvent extends RecievedEventListener {

    public ChatModEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminMuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "mute");
    }

    private boolean isAdminUnmuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "unmute");
    }

    private void addRoleToUserAndNotifyUser(GuildMessageReceivedEvent event, Role role, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        event.getGuild().addRoleToMember(member, role).complete();
        event.getChannel().sendMessage("Muted " + member.getAsMention() + "!").complete();
    }

    private void removeRoleToUserAndNotifyUser(GuildMessageReceivedEvent event, Role role, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        event.getGuild().removeRoleFromMember(member, role).complete();
        event.getChannel().sendMessage("Unmuted " + member.getAsMention() + "!").complete();

    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        if (isAdminMuting(event)) {
            String[] message = event.getMessage().getContentRaw().split(" ");
            String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
            Role role = RoleFinder.getInstance().getRoleWithNameMember(event.getGuild(), "Muted");
            addRoleToUserAndNotifyUser(event, role, username);
        } else if (isAdminUnmuting(event)) {
            String[] message = event.getMessage().getContentRaw().split(" ");
            String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
            Role role = RoleFinder.getInstance().getRoleWithNameMember(event.getGuild(), "Muted");
            removeRoleToUserAndNotifyUser(event, role, username);
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && (isAdminMuting((GuildMessageReceivedEvent) genericEvent) || isAdminUnmuting((GuildMessageReceivedEvent) genericEvent));
    }
}
