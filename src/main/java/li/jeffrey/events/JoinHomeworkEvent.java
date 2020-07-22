package li.jeffrey.events;

import java.awt.Color;
import java.util.ArrayList;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinHomeworkEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;

    public JoinHomeworkEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
    }

    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() == null || !event.getChannelJoined().getName().contains("Homework")) {
            if (event.getChannelLeft() == null) {
                return;
            }
            if (event.getChannelLeft().getName().equals("Join a Homework Channel")) {
                Message a = jda.getGuildById("657977250771238912").getTextChannelById("707439772234154055")
                        .retrieveMessageById(jda.getGuildById("657977250771238912")
                                .getTextChannelById("707439772234154055").getLatestMessageId())
                        .complete();
                if (a.getEmbeds().get(0).getFields().get(4).getValue().substring(10).trim()
                        .equals(event.getEntity().getAsMention().trim())) {
                    a.delete().complete();
                    return;
                }
                return;
            } else {
                return;
            }
        }
        if (event.getChannelJoined().getName().equals("Join a Homework Channel")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.ORANGE);
            eb.setTitle(event.getEntity().getEffectiveName()
                    + ", it looks like you're trying to join a homework voice chat.");
            eb.addField("", "You must be doing homework to join these channels.", true);
            eb.addField("", "If you are not doing homework, please leave this channel.", true);
            eb.addField("", "Not doing homework will lead you to be blacklisted from the channels.", true);
            eb.addField("", "React to the icon corresponding to the channel name if you are actually doing homework.",
                    true);
            eb.addField("", "User tag: " + event.getEntity().getAsMention(), false);
//			ArrayList<Emote> a = new ArrayList<Emote>(
//					jda.getGuildById("657977250771238912").getEmotesByName("homework", true));
//			Emote emote = a.get(0);
            ArrayList<Emote> a1 = new ArrayList<Emote>(
                    jda.getGuildById("657977250771238912").getEmotesByName("num1", true));
            Emote emote1 = a1.get(0);
            ArrayList<Emote> a2 = new ArrayList<Emote>(
                    jda.getGuildById("657977250771238912").getEmotesByName("num2", true));
            Emote emote2 = a2.get(0);
            ArrayList<Emote> a3 = new ArrayList<Emote>(
                    jda.getGuildById("657977250771238912").getEmotesByName("num3", true));
            Emote emote3 = a3.get(0);
            Message b = jda.getGuildById("657977250771238912").getTextChannelById("707439772234154055")
                    .sendMessage(eb.build()).complete();
//			b.addReaction(emote).complete();
            b.addReaction(emote1).complete();
            b.addReaction(emote2).complete();
            b.addReaction(emote3).complete();

        }
    }

    public void onGenericMessageReaction(GenericMessageReactionEvent event) {
        try {
            if (event.getUser().isBot())
                return;
            Message a = jda.getGuildById("657977250771238912").getTextChannelById("707439772234154055")
                    .retrieveMessageById(event.getMessageId()).complete();
            if (event.getChannel().getName().equals("join-homework")) {
                ArrayList<MessageEmbed> embed = new ArrayList<MessageEmbed>(a.getEmbeds());
                ArrayList<Field> fields = new ArrayList<Field>(embed.get(0).getFields());

                if (fields.get(4).getValue().contains(event.getMember().getAsMention())
                        && jda.getGuildById("657977250771238912").getMemberById(event.getMember().getId())
                        .getVoiceState().getChannel() != null) {
                    try {
                        event.getReactionEmote().getEmote();
                    } catch (IllegalStateException e) {
                        return;
                    }
                    if (event.getReactionEmote().getEmote().getName().equals("num1")) {
                        jda.getGuildById("657977250771238912").moveVoiceMember(event.getMember(),
                                jda.getGuildById("657977250771238912").getVoiceChannelById("683150976706019339"))
                                .complete();
                        jda.getGuildById("657977250771238912").getTextChannelById("707439772234154055")
                                .deleteMessageById(event.getMessageId()).complete();
                    }
                    if (event.getReactionEmote().getEmote().getName().equals("num2")) {
                        jda.getGuildById("657977250771238912").moveVoiceMember(event.getMember(),
                                jda.getGuildById("657977250771238912").getVoiceChannelById("707721862787891212"))
                                .complete();
                        jda.getGuildById("657977250771238912").getTextChannelById("707439772234154055")
                                .deleteMessageById(event.getMessageId()).complete();
                    }
                    if (event.getReactionEmote().getEmote().getName().equals("num3")) {
                        jda.getGuildById("657977250771238912").moveVoiceMember(event.getMember(),
                                jda.getGuildById("657977250771238912").getVoiceChannelById("707727030699163739"))
                                .complete();
                        jda.getGuildById("657977250771238912").getTextChannelById("707439772234154055")
                                .deleteMessageById(event.getMessageId()).complete();
                    }
                }

            }
        } catch (Exception e) {
            return;
        }
    }

}
