package li.jeffrey.events;

import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VerifyEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;

    public VerifyEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
    }

    public void onGenericMessageReaction(GenericMessageReactionEvent event) {
        try {
            if (!event.getChannel().getName().equals("verify") || event.getUser().isBot())
                return;
            List<Role> memberRoleNames = event.getGuild().getRolesByName("Member", true);
            Role member = null;
            for (Role i : memberRoleNames) {
                if (i.getName().toLowerCase().equals("member")) {
                    member = i;
                    break;
                }
            }
            event.getGuild().addRoleToMember(event.getMember().getId(), member).complete();
            event.getReaction().removeReaction(event.getUser()).complete();
        } catch (Exception e) {
            return;
        }
    }
}
