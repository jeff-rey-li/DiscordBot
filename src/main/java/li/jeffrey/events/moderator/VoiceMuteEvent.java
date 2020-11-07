package li.jeffrey.events.moderator;

import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.UserDetermination;
import li.jeffrey.util.UsernameSanitizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class VoiceMuteEvent extends ReceivedEventListener {

    public VoiceMuteEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private boolean isAdminVoiceMuting(GuildMessageReceivedEvent event) {
        return UserDetermination.getInstance().isAdmin(event) && event.getMessage().getContentRaw().startsWith(prefix + "voicemute");
    }

    private void voiceMuteUserAndNotifyChannel(GuildMessageReceivedEvent event, String username) {
        Member member = event.getGuild().getMemberById(event.getAuthor().getId());
        member.mute(true).queue();
        event.getChannel().sendMessage("Voice muted " + member.getAsMention() + "!").queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        String[] message = event.getMessage().getContentRaw().split(" ");
        String username = UsernameSanitizer.getInstance().sanitizeUsername(message[1]);
        voiceMuteUserAndNotifyChannel(event, username);
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && (isAdminVoiceMuting((GuildMessageReceivedEvent) genericEvent));
    }
}
