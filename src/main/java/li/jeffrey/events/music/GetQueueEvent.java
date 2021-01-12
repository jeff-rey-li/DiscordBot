package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.events.structure.ReceivedEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class GetQueueEvent extends ReceivedEventListener {

    public GetQueueEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    public boolean isUserGettingQueue(GuildMessageReceivedEvent event) {
        return event.getMessage().getContentRaw().trim().equals(prefix + "q") || event.getMessage().getContentRaw().trim().equals(prefix + "queue");
    }

    private String calculateQueueLength(List<SongAddData> queue) {
        int totalHours = 0;
        int totalMinutes = 0;
        int totalSeconds = 0;
        for (SongAddData i : queue) {
            int songLengthInSeconds = (int) (i.getSong().getDuration() / 1000);
            totalHours += songLengthInSeconds / 3600;
            totalMinutes += songLengthInSeconds / 60;
            totalSeconds += songLengthInSeconds % 60;
        }
        totalMinutes += totalSeconds / 60;
        totalSeconds = totalSeconds % 60;
        totalHours += totalMinutes / 60;
        totalMinutes = totalMinutes % 60;
        String hourString = (totalHours < 10) ? "0" + totalHours : Integer.toString(totalHours);
        String minuteString = (totalMinutes < 10) ? "0" + totalMinutes : Integer.toString(totalMinutes);
        String secondString = (totalSeconds < 10) ? "0" + totalSeconds : Integer.toString(totalSeconds);
        String totalTime = (totalHours > 0) ? hourString + ":" + minuteString + ":" + secondString
                : minuteString + ":" + secondString;
        return totalTime;
    }

    private EmbedBuilder createQueueMessage(List<SongAddData> queue, int startIndex, int endIndex) {
        EmbedBuilder eb = new EmbedBuilder();
        if (startIndex == 0) {
            eb.setTitle("Music");
        }
        eb.setColor(Color.BLUE);
        String queueList = "";
        for (int i = startIndex; i < endIndex; i++) {
            AudioTrack song = queue.get(i).getSong();
            String songName = song.getInfo().title;
            songName = songName.replaceAll("\\[", "").replaceAll("\\]", "");
            long songSecondsLong = song.getDuration() / 1000;
            int songMinutes = (int) songSecondsLong / 60;
            int songSeconds = (int) songSecondsLong % 60;
            String minuteString = (songMinutes < 10) ? "0" + songMinutes : Integer.toString(songMinutes);
            String secondString = (songSeconds < 10) ? "0" + songSeconds : Integer.toString(songSeconds);
            if (songName.length() > 55) {
                songName = songName.substring(0, 56) + " ...";
            }
            if (MusicPlayer.getInstance().getPlayer().getPlayingTrack().getInfo().title.equals(song.getInfo().title)) {
                queueList += "\n:arrow_right: " + (i + 1) + ". [" + songName + "](" + song.getInfo().uri + ")" + " *"
                        + minuteString + ":" + secondString + "*";
            } else {
                queueList += "\n" + (i + 1) + ". [" + songName + "](" + song.getInfo().uri + ")" + " *" + minuteString + ":"
                        + secondString + "*";
            }
        }
        String totalTime = calculateQueueLength(queue);
        if (startIndex == 0) {
            eb.addField("Queue (" + totalTime + "):", queueList, false);
        } else {
            eb.setDescription(queueList);
        }

        if (SongQueue.getInstance().getQueue().size() == endIndex) {
            String toggles = "Repeat: ";
            if (MusicPlayer.getInstance().getIsRepeat()) {
                toggles += ":white_check_mark:";
            } else {
                toggles += ":x:";
            }
            toggles += "\n Shuffle: ";
            if (MusicPlayer.getInstance().getIsShuffle()) {
                toggles += ":white_check_mark:";
            } else {
                toggles += ":x:";
            }
            toggles += "\nVolume: " + MusicPlayer.getInstance().getPlayer().getVolume() + "%";
            eb.addField("Toggles:", toggles, false);
        }
        return eb;
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        List<SongAddData> queue = SongQueue.getInstance().getQueue();
        int numMessagesNeeded = (int) Math.ceil(queue.size() / 5.0);
        for (int i = 0; i < numMessagesNeeded; i++) {
            if (i != numMessagesNeeded - 1) {
                ((GuildMessageReceivedEvent) genericEvent).getChannel().sendMessage(createQueueMessage(queue, i * 5, (i + 1) * 5).build()).queue();
            } else {
                ((GuildMessageReceivedEvent) genericEvent).getChannel().sendMessage(createQueueMessage(queue, i * 5, queue.size()).build()).queue();
            }
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && isUserGettingQueue((GuildMessageReceivedEvent) genericEvent);
    }
}
