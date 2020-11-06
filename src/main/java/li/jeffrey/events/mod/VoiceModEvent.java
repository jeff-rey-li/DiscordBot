package li.jeffrey.events.mod;

import li.jeffrey.constants.Constants;
import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceModEvent extends ReceivedEventListener {

    public VoiceModEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminVoiceMuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "voicemute");
    }

    private boolean isAdminVoiceUnmuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "voiceunmute");
    }

    private boolean isAdminDeafening(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "deafen");
    }

    private boolean isAdminUndeafening(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "undeafen");
    }

    private boolean isAdminDisconnecting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "disconnect");
    }

    private boolean isAdminLockingChannel(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "lockchannel");
    }

    private boolean isAdminUnlockingChannel(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "unlockchannel");
    }

    private void voiceMuteUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        member.mute(true).complete();
        event.getChannel().sendMessage("Voice muted " + member.getAsMention() + "!").complete();
    }

    private void voiceUnmuteUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        member.mute(false).complete();
        event.getChannel().sendMessage("Voice unmuted " + member.getAsMention() + "!").complete();
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

    private void disconnectUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        if (member.getVoiceState().inVoiceChannel()) {
            event.getGuild().kickVoiceMember(member).complete();
        }
    }

    private void lockVoiceChannelAndNotifyChannel(GuildMessageReceivedEvent event) {
        Member member = event.getGuild().getMemberById(event.getAuthor().getId());
        Role role = event.getGuild().getRoleById(Constants.EVERYONE_ROLE_ID);
        member.getVoiceState().getChannel().putPermissionOverride(role).complete();
        member.getVoiceState().getChannel().getPermissionOverride(role).getManager().deny(Permission.VOICE_CONNECT).complete();
        event.getChannel().sendMessage("The voice channel __" + member.getVoiceState().getChannel().getName() + "__ " + "has been locked.").complete();
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
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = new String();
        if (message.length > 1) {
            username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        }
        if (isAdminVoiceMuting(event)) {
            voiceMuteUserAndNotifyChannel(event, username);
        } else if (isAdminVoiceUnmuting(event)) {
            voiceUnmuteUserAndNotifyChannel(event, username);
        } else if (isAdminDeafening(event)) {
            deafenUserAndNotifyChannel(event, username);
        } else if (isAdminUndeafening(event)) {
            undeafenUserAndNotifyChannel(event, username);
        } else if (isAdminDisconnecting(event)) {
            disconnectUserAndNotifyChannel(event, username);
        } else if (isAdminLockingChannel(event)) {
            lockVoiceChannelAndNotifyChannel(event);
        } else if (isAdminUnlockingChannel(event)) {
            unlockVoiceChannelAndNotifyChannel(event);
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent &&
                (isAdminVoiceMuting((GuildMessageReceivedEvent) genericEvent) ||
                        isAdminVoiceUnmuting((GuildMessageReceivedEvent) genericEvent) ||
                        isAdminDeafening((GuildMessageReceivedEvent) genericEvent) ||
                        isAdminUndeafening((GuildMessageReceivedEvent) genericEvent) ||
                        isAdminDisconnecting((GuildMessageReceivedEvent) genericEvent) ||
                        isAdminLockingChannel((GuildMessageReceivedEvent) genericEvent) ||
                        isAdminUnlockingChannel((GuildMessageReceivedEvent) genericEvent));
    }
}
