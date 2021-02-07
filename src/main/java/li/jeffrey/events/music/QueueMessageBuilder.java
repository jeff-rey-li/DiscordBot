package li.jeffrey.events.music;

import java.awt.Color;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import li.jeffrey.util.QueueLengthCalculator;
import net.dv8tion.jda.api.EmbedBuilder;

public class QueueMessageBuilder {
	private QueueLengthCalculator queueLengthCalculator;
	private QueueListInfoBuilder queueListInfoBuilder;
	
	public QueueMessageBuilder() {
		this.queueLengthCalculator = new QueueLengthCalculator();
		this.queueListInfoBuilder = new QueueListInfoBuilder();
	}
	
	private boolean isFirstSong(int songNumber) {
		return songNumber == 0;
	}
	
	private boolean isLastMessage(int endIndex, List<SongAddData> queue) {
		return SongQueue.getInstance().getQueue().size() == endIndex;
	}
	
	public EmbedBuilder createQueueMessage(List<SongAddData> queue, int startIndex, int endIndex) {
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setColor(Color.BLUE);
        
        String queueList = queueListInfoBuilder.buildListInfoString(queue, startIndex, endIndex);
        long totalQueueTime = queueLengthCalculator.calculateTotalQueueLength(queue);
        String totalTime = queueLengthCalculator.convertTimeToString(totalQueueTime);
        if (isFirstSong(startIndex)) {
            eb.setTitle("Music");
            eb.addField("Queue (" + totalTime + "):", queueList, false);
        } else {
        	eb.setDescription(queueList);
        }

        if (isLastMessage(endIndex, queue)) {
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

}
