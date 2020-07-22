package li.jeffrey.events.mod;

import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatModEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;
    private String myID;

    public ChatModEvent(JDA jda, String prefix, String myID) {
        this.jda = jda;
        this.prefix = prefix;
        this.myID = myID;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith(prefix))
            return;
        if (event.getAuthor().getId().equals(myID)) {
            String[] msg = event.getMessage().getContentRaw().split(" ");
            if (msg[0].equals("!mute")) {
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member member = event.getGuild().retrieveMemberById(msg[1]).complete();
                List<Role> muteRoleNames = event.getGuild().getRolesByName("Muted", true);
                Role muted = null;
                for (Role i : muteRoleNames) {
                    if (i.getName().toLowerCase().equals("muted")) {
                        muted = i;
                        break;
                    }
                }
                event.getGuild().addRoleToMember(member, muted).complete();
                event.getChannel().sendMessage("Muted " + member.getAsMention() + "!").complete();
            }
            if (msg[0].equals("!unmute")) {
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member member = event.getGuild().retrieveMemberById(msg[1]).complete();
                List<Role> muteRoleNames = event.getGuild().getRolesByName("Muted", true);
                Role muted = null;
                for (Role i : muteRoleNames) {
                    if (i.getName().toLowerCase().equals("muted")) {
                        muted = i;
                        break;
                    }
                }
                event.getGuild().removeRoleFromMember(member, muted).complete();
                event.getChannel().sendMessage("Unmuted " + member.getAsMention() + "!").complete();
            }
        }
    }

}
