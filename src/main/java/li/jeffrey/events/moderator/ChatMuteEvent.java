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

public class ChatMuteEvent extends ReceivedEventListener {

    public ChatMuteEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminMuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "mute");
    }

    private void addMutedRoleToUserAndNotifyChannel(GuildMessageReceivedEvent event, Role role, String username) {
        Member member = event.getGuild().getMemberById(event.getAuthor().getId());
        event.getGuild().addRoleToMember(member, role).queue();
        event.getChannel().sendMessage("Muted " + member.getAsMention() + "!").queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        Role role = RoleFinder.getInstance().getRoleWithNameMember(event.getGuild(), "Muted");
        addMutedRoleToUserAndNotifyChannel(event, role, username);
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isAdminMuting((GuildMessageReceivedEvent) genericEvent);
    }
}
