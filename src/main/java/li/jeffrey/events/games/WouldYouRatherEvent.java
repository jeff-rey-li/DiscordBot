package li.jeffrey.events.games;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class WouldYouRatherEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;

    public WouldYouRatherEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().contains(prefix + "wyr")) {
            String[] wyr = event.getMessage().getContentRaw().replace(prefix + "wyr", "").trim().split("\"");
            for (int i = 0; i < wyr.length; i++) {
                if (wyr[i] == null)
                    continue;
                if (wyr[i].replace(" ", "").equals(""))
                    continue;
                event.getChannel().sendMessage(wyr[i]).complete().addReaction("✅").complete();
            }
        }
    }

}
