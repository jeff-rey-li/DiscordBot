package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class VoiceMuteEvent extends ReceivedEventListener {

    public VoiceMuteEvent(JDA jda, String prefix) { super(jda, prefix); }

    private boolean isAdminVoiceMuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "voicemute");
    }

    private boolean isAdminVoiceUnmuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "voiceunmute");
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

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = "";
        if (message.length > 1) {
            username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        }
        if (isAdminVoiceMuting(event)) {
            voiceMuteUserAndNotifyChannel(event, username);
        } else if (isAdminVoiceUnmuting(event)) {
            voiceUnmuteUserAndNotifyChannel(event, username);
        }
    }
    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
            return genericEvent instanceof GuildMessageReceivedEvent && (isAdminVoiceMuting((GuildMessageReceivedEvent) genericEvent) || isAdminVoiceUnmuting((GuildMessageReceivedEvent) genericEvent));
    }
}
