package li.jeffrey.events.games;

import java.util.Random;

import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiceRollEvent extends ReceivedEventListener {
    public DiceRollEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private String getDiceRoll() {
        Random random = new Random();
        int diceRollResult = random.nextInt(5) + 1;

        return Integer.toString(diceRollResult);
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        ((GuildMessageReceivedEvent) genericEvent).getChannel().sendMessage(getDiceRoll()).complete();
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().equals(prefix + "diceroll");
    }

}
