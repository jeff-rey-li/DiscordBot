package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DisconnectEvent extends ReceivedEventListener {

    public DisconnectEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminDisconnecting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "disconnect");
    }

    private void disconnectUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().retrieveMemberById(username).complete();
        if (member.getVoiceState().inVoiceChannel()) {
            event.getGuild().kickVoiceMember(member).complete();
        }
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = "";
        if (message.length > 1) {
            username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        }
        if (isAdminDisconnecting(event)) {
            disconnectUserAndNotifyChannel(event, username);
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isAdminDisconnecting((GuildMessageReceivedEvent) genericEvent);
    }
}
