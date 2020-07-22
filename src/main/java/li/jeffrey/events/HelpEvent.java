package li.jeffrey.events;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HelpEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;

    public HelpEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().trim().equals(prefix + "help")) {
            EmbedBuilder eb = mainHelp();
            Message msg = event.getChannel().sendMessage(eb.build()).complete();
            msg.addReaction("1️⃣").complete();
            msg.addReaction("2️⃣").complete();
            msg.addReaction("3️⃣").complete();
            msg.addReaction("4️⃣").complete();
            msg.addReaction("5️⃣").complete();
            msg.addReaction("❌").complete();
        }
    }

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        if (msg.getAuthor().isBot() && !event.getUser().isBot()) {
            MessageEmbed a = msg.getEmbeds().get(0);
            if (a.getTitle().equals("Help Options:")) {
                if (event.getReactionEmote().getName().equals("1️⃣")) {
                    EmbedBuilder eb = generalHelp();
                    msg.editMessage(eb.build()).complete();
                    msg.clearReactions().complete();
                    msg.addReaction("↩️").complete();
                    msg.addReaction("❌").complete();
                    return;
                } else if (event.getReactionEmote().getName().equals("2️⃣")) {
                    EmbedBuilder eb = musicHelp();
                    msg.editMessage(eb.build()).complete();
                    msg.clearReactions().complete();
                    msg.addReaction("↩️").complete();
                    msg.addReaction("❌").complete();
                    return;
                } else if (event.getReactionEmote().getName().equals("3️⃣")) {
                    EmbedBuilder eb = miscHelp();
                    msg.editMessage(eb.build()).complete();
                    msg.clearReactions().complete();
                    msg.addReaction("↩️").complete();
                    msg.addReaction("❌").complete();
                    return;
                } else if (event.getReactionEmote().getName().equals("4️⃣")) {
                    EmbedBuilder eb = chatModHelp();
                    msg.editMessage(eb.build()).complete();
                    msg.clearReactions().complete();
                    msg.addReaction("↩️").complete();
                    msg.addReaction("❌").complete();
                    return;
                } else if (event.getReactionEmote().getName().equals("5️⃣")) {
                    EmbedBuilder eb = voiceModHelp();
                    msg.editMessage(eb.build()).complete();
                    msg.clearReactions().complete();
                    msg.addReaction("↩️").complete();
                    msg.addReaction("❌").complete();
                    return;
                } else if (event.getReactionEmote().getName().equals("❌")) {
                    msg.delete().complete();
                }
            } else if (a.getTitle().contains("Help")) {
                if (event.getReactionEmote().getName().equals("↩️")) {
                    EmbedBuilder eb = mainHelp();
                    msg.editMessage(eb.build()).complete();
                    msg.clearReactions().complete();
                    msg.addReaction("1️⃣").complete();
                    msg.addReaction("2️⃣").complete();
                    msg.addReaction("3️⃣").complete();
                    msg.addReaction("4️⃣").complete();
                    msg.addReaction("5️⃣").complete();
                    msg.addReaction("❌").complete();
                    return;
                } else if (event.getReactionEmote().getName().equals("❌")) {
                    msg.delete().complete();
                }
            }
        }
    }

    public EmbedBuilder mainHelp() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Help Options:").setColor(Color.CYAN);
        eb.addField("**Universal Commands**", "-------------------------", false);
        eb.addField("**1. General**", "Lists help for commonly asked questions", false);
        eb.addField("**2. Music**", "Lists help for music commands", false);
        eb.addField("**3. Misc**", "Lists help for miscellaneous commands", false);
        eb.addBlankField(false);
        eb.addField("**Moderator-only Commands**", "---------------------------------", false);
        eb.addField("**4. Chat Mod**", "Lists help for chat moderation commands", false);
        eb.addField("**5. Voice Mod**", "Lists help for voice moderation commands", false);
        eb.setFooter("Made by Jeffrey Li");
        return eb;
    }

    public EmbedBuilder generalHelp() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("General Help:").setColor(Color.CYAN);
        eb.addField("**How to join a Homework Channel**",
                "Join the voice channel: \"Join a Homework Channel\". Then, react to the reaction in #join-homework that corresponds to the Homework Channel number",
                false);
        eb.setFooter("Made by Jeffrey Li");
        return eb;
    }

    public EmbedBuilder musicHelp() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Music Help:").setColor(Color.CYAN);
        eb.addField("**" + prefix + "join**", "Connects the bot to the voice channel that the user is connected to",
                false);
        eb.addField("**" + prefix + "leave**", "Disconnects the bot from the voice channel", false);
        eb.addField("**" + prefix + "play**", "Continues to play the song if bot was paused", false);
        eb.addField("**" + prefix + "pause**", "Pauses the bot", false);
        eb.addField("**" + prefix + "play <song>**",
                "Plays the searched song (without <>). Include \"scsearch:\" in front of the song name to search on soundcloud",
                false);
        eb.addField("**" + prefix + "queue**", "Returns the queue", false);
        eb.addField("**" + prefix + "skip**", "Skips the current song", false);
        eb.addField("**" + prefix + "remove <song>**", "Removes song from queue (without <>)", false);
        eb.addField("**" + prefix + "repeat**", "Toggles repeat function", false);
        eb.addField("**" + prefix + "shuffle**", "Toggles shuffle function", false);
        eb.addField("**" + prefix + "volume " + "[number]" + "**", "Sets the volume of the bot (0-200)", false);
        eb.setFooter("Made by Jeffrey Li");
        return eb;
    }

    public EmbedBuilder miscHelp() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Miscellaneous Help:").setColor(Color.CYAN);
        eb.addField("**" + prefix + "wyr \"option 1\"\"option 2\"**",
                "Creates a Would You Rather with reacts (include \"\" for each option that you would like to add",
                false);
        eb.addField("**" + prefix + "diceroll**",
                "Rolls a dice (1-6)",
                false);
        eb.setFooter("Made by Jeffrey Li");
        return eb;
    }

    public EmbedBuilder chatModHelp() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Chat Mod Help:").setColor(Color.CYAN);
        eb.addField("**" + prefix + "mute**", "Mutes a user in chat", false);
        eb.addField("**" + prefix + "unmute**", "Unmutes a user in chat", false);
        eb.addField("**" + prefix + "kick**", "Kicks a user from the server", false);
        eb.addField("**" + prefix + "ban**", "Bans a user from the server", false);
        eb.addField("**" + prefix + "unban**", "Unbans a user from the server", false);
        eb.setFooter("Made by Jeffrey Li");
        return eb;
    }

    public EmbedBuilder voiceModHelp() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Voice Mod Help:").setColor(Color.CYAN);
        eb.addField("**" + prefix + "voicemute**", "Mutes the voice of a user", false);
        eb.addField("**" + prefix + "voiceunmute**", "Unmutes the voice of a user", false);
        eb.addField("**" + prefix + "deafen**", "Deafens a user", false);
        eb.addField("**" + prefix + "undeafen**", "Undeafens a user", false);
        eb.addField("**" + prefix + "disconnect**", "Disconnects a user from any voice channel", false);
        eb.addField("**" + prefix + "lockchannel**", "Locks the current voice channel that you are in", false);
        eb.addField("**" + prefix + "unlockchannel**", "Unlocks the current voice channel that you are in", false);
        eb.setFooter("Made by Jeffrey Li");
        return eb;
    }

}
