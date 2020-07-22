package li.jeffrey.events.mod;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerModEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;
    private String myID;

    public ServerModEvent(JDA jda, String prefix, String myID) {
        this.jda = jda;
        this.prefix = prefix;
        this.myID = myID;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith(prefix))
            return;
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (event.getAuthor().getId().equals(myID)) {
            if (msg[0].equals("!kick")) {
//				msg[1] = msg[1].substring(3, msg[1].length() - 1);
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member a = event.getGuild().retrieveMemberById(msg[1]).complete();
                if (msg.length >= 3) {
                    String reason = "";
                    for (int i = 2; i < msg.length; i++) {
                        reason += msg[i] + " ";
                    }
                    event.getChannel().sendMessage(a.getAsMention() + " has been kicked! Reason: " + reason + ".")
                            .complete();
                    a.kick(reason).complete();
                } else {
                    event.getChannel().sendMessage(a.getAsMention() + " has been kicked! Reason: No reason given.")
                            .complete();
                    a.kick().complete();
                }
            }
            if (msg[0].equals("!ban")) {
//				msg[1] = msg[1].substring(3, msg[1].length() - 1);
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                Member a = event.getGuild().retrieveMemberById(msg[1]).complete();
                if (msg.length >= 3) {
                    String reason = "";
                    for (int i = 2; i < msg.length; i++) {
                        reason += msg[i] + " ";
                    }
                    event.getChannel().sendMessage(a.getAsMention() + " has been banned! Reason: " + reason + ".")
                            .complete();
                    a.ban(0, reason).complete();
                } else {
                    event.getChannel().sendMessage(a.getAsMention() + " has been banned! Reason: No reason given.")
                            .complete();
                    a.ban(0).complete();
                }
            }
            if (msg[0].equals("!unban")) {
                msg[1] = msg[1].replaceAll("[<>/@!]", "");
                User a = jda.retrieveUserById(msg[1]).complete();
                if (msg.length >= 3) {
                    String reason = "";
                    for (int i = 2; i < msg.length; i++) {
                        reason += msg[i] + " ";
                    }
                    event.getChannel().sendMessage(a.getAsMention() + " has been unbanned! Reason: " + reason + ".")
                            .complete();
                    event.getGuild().unban(a).complete();
                } else {
                    event.getChannel().sendMessage(a.getAsMention() + " has been unbanned! Reason: No reason given.")
                            .complete();
                    event.getGuild().unban(a).complete();
                }
            }
        }
    }

}
