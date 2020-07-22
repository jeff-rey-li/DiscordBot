package li.jeffrey;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Debug extends ListenerAdapter {

    private String myID;
    private JDA jda;

    public Debug(String myID, JDA jda) {
        this.myID = myID;
        this.jda = jda;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

    }

}
