package li.jeffrey.events.music;

import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class GetQueueEvent extends ReceivedEventListener {
	private QueueMessageBuilder queueMessageBuilder;
	
    public GetQueueEvent(JDA jda, String prefix) {
        super(jda, prefix);
        queueMessageBuilder = new QueueMessageBuilder();
    }

    public boolean isUserGettingQueue(GuildMessageReceivedEvent event) {
        return event.getMessage().getContentRaw().trim().equals(prefix + "q") || event.getMessage().getContentRaw().trim().equals(prefix + "queue");
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        List<SongAddData> queue = SongQueue.getInstance().getQueue();
        int numMessagesNeeded = (int) Math.ceil(queue.size() / 5.0);
        for (int i = 0; i < numMessagesNeeded; i++) {
            if (i != numMessagesNeeded - 1) {
                ((GuildMessageReceivedEvent) genericEvent).getChannel().sendMessage(queueMessageBuilder.createQueueMessage(queue, i * 5,
                        (i + 1) * 5).build()).queue();
            } else {
                ((GuildMessageReceivedEvent) genericEvent).getChannel().sendMessage(queueMessageBuilder.createQueueMessage(queue, i * 5,
                        queue.size()).build()).queue();
            }
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isUserGettingQueue((GuildMessageReceivedEvent) genericEvent);
    }
}
