package li.jeffrey.events.mod;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceModEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;
    private String myID;

    public VoiceModEvent(JDA jda, String prefix, String myID) {
        this.jda = jda;
        this.prefix = prefix;
        this.myID = myID;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith(prefix))
            return;
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (event.getAuthor().getId().equals(myID)) {
            if (msg[0].equals(prefix + "voicemute")) {
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member member = event.getGuild().retrieveMemberById(msg[1]).complete();
                member.mute(true).complete();
                event.getChannel().sendMessage("Voice muted " + member.getAsMention() + "!").complete();
            }
            if (msg[0].equals(prefix + "voiceunmute")) {
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member member = event.getGuild().retrieveMemberById(msg[1]).complete();
                member.mute(false).complete();
                event.getChannel().sendMessage("Voice unmuted " + member.getAsMention() + "!").complete();
            }
            if (msg[0].equals(prefix + "deafen")) {
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member member = event.getGuild().retrieveMemberById(msg[1]).complete();
                member.deafen(true).complete();
                event.getChannel().sendMessage("Deafened " + member.getAsMention() + "!").complete();
            }
            if (msg[0].equals(prefix + "undeafen")) {
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member member = event.getGuild().retrieveMemberById(msg[1]).complete();
                member.deafen(false).complete();
                event.getChannel().sendMessage("Undeafened " + member.getAsMention() + "!").complete();
            }
            if (msg[0].equals(prefix + "disconnect")) {
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member member = event.getGuild().retrieveMemberById(msg[1]).complete();
                if (member.getVoiceState().inVoiceChannel()) {
                    event.getGuild().kickVoiceMember(member).complete();
                }
            }
            if (msg[0].equals(prefix + "lockchannel")) {
                Member member = event.getGuild().getMemberById(event.getAuthor().getId());
                Role role = event.getGuild().getRoleById("657977250771238912");
                member.getVoiceState().getChannel().putPermissionOverride(role).complete();
                member.getVoiceState().getChannel().getPermissionOverride(role).getManager()
                        .deny(Permission.VOICE_CONNECT).complete();
                event.getChannel().sendMessage("The voice channel __" + member.getVoiceState().getChannel().getName() + "__ has been locked.").complete();
            }
            if (msg[0].equals(prefix + "unlockchannel")) {
                Member member = event.getGuild().getMemberById(event.getAuthor().getId());
                Role role = event.getGuild().getRoleById("657977250771238912");
                member.getVoiceState().getChannel().putPermissionOverride(role).complete();
                member.getVoiceState().getChannel().getPermissionOverride(role).getManager()
                        .grant(Permission.VOICE_CONNECT).complete();
                event.getChannel().sendMessage("The voice channel __" + member.getVoiceState().getChannel().getName() + "__ has been unlocked.").complete();
            }
        }
    }

}
