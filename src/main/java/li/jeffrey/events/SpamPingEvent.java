package li.jeffrey.events;

import java.util.TreeMap;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SpamPingEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;
    private TreeMap<String, Integer> pingCount;

    public SpamPingEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
        pingCount = new TreeMap<String, Integer>();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() && (event.getMessage().getContentRaw().startsWith("<")
                || event.getMessage().getContentRaw().startsWith("Now pinging:"))) {
            String[] msg = event.getMessage().getContentRaw().split(" ");
            if (msg.length == 1) {
                msg[0] = msg[0].replaceAll("[<>/@!]", "");
                User a = jda.retrieveUserById(msg[0]).complete();
                int pings = pingCount.get(msg[0]) + 1;
                pingCount.put(msg[0], pings);
                event.getChannel().sendMessage(a.getAsMention()).complete();
            } else if (msg.length == 3) {
                msg[2] = msg[2].replaceAll("[<>/@!]", "");
                User a = jda.retrieveUserById(msg[2]).complete();
                int pings = pingCount.get(msg[2]) + 1;
                pingCount.put(msg[2], pings);
                event.getChannel().sendMessage(a.getAsMention()).complete();

            }
        } else if (event.getAuthor().getId().equals("220645828405100545")) {
            try {
                String[] msg = event.getMessage().getContentRaw().split(" ");
                if (msg[0].equals(prefix + "ping")) {
                    if (msg[1].equals("stop")) {
                        msg[2] = msg[2].replaceAll("[<>/@!]", "");
                        User a = jda.retrieveUserById(msg[2]).complete();
                        event.getChannel().sendMessage(
                                "Stopped pinging " + a.getAsMention() + ". Pinged " + pingCount.get(msg[2]) + " times!")
                                .complete();
                        pingCount.remove(msg[2]);
                    } else {
                        msg[1] = msg[1].replaceAll("[<>/@!]", "");
                        User a = jda.retrieveUserById(msg[1]).complete();
                        event.getChannel().sendMessage("Now pinging: " + a.getAsMention()).complete();
                        pingCount.put(msg[1], 1);
                    }
                }
            } catch (Exception e) {
                return;
            }
        }
    }
}
