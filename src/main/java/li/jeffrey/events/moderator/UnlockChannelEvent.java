package li.jeffrey.events.moderator;

import li.jeffrey.constants.Constants;
import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UnlockChannelEvent extends ReceivedEventListener {

    public UnlockChannelEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminUnlockingChannel(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "unlockchannel");
    }

    private void unlockVoiceChannelAndNotifyChannel(GuildMessageReceivedEvent event) {
        Member member = event.getGuild().getMemberById(event.getAuthor().getId());
        Role role = event.getGuild().getRoleById(Constants.EVERYONE_ROLE_ID);
        member.getVoiceState().getChannel().putPermissionOverride(role).complete();
        member.getVoiceState().getChannel().getPermissionOverride(role).getManager().grant(Permission.VOICE_CONNECT).complete();
        event.getChannel().sendMessage("The voice channel __" + member.getVoiceState().getChannel().getName() + "__ " + "has been unlocked.").complete();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        unlockVoiceChannelAndNotifyChannel(event);
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isAdminUnlockingChannel((GuildMessageReceivedEvent) genericEvent);
    }
}
