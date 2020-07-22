package li.jeffrey.events.games;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiceRollEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;

    public DiceRollEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(prefix + "diceroll")) {
            event.getChannel().sendMessage(Integer.toString((int) (Math.random() * 6 + 1))).complete();
        }
    }

}
