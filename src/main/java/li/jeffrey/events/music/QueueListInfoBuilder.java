package li.jeffrey.events.music;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import li.jeffrey.util.QueueLengthCalculator;

public class QueueListInfoBuilder {
	private QueueLengthCalculator queueLengthCalculator;
	
	public QueueListInfoBuilder () {
		this.queueLengthCalculator = new QueueLengthCalculator();
	}
	
	private boolean isCurrentSong(String songName) {
		return MusicPlayer.getInstance().getPlayer().getPlayingTrack().getInfo().title.equals(songName);
	}
	
	public String buildListInfoString(List<SongAddData> queue, int startIndex, int endIndex) {
		String queueList = "";
        for (int i = startIndex; i < endIndex; i++) {
            AudioTrack song = queue.get(i).getSong();
            String songName = song.getInfo().title;
            songName = songName.replaceAll("\\[", "").replaceAll("\\]", "");
            
            String songTimeString = queueLengthCalculator.convertTimeToString(song.getDuration());
            
            if (songName.length() > 55) {
                songName = songName.substring(0, 56) + " ...";
            }
            if (isCurrentSong(song.getInfo().title)) {
                queueList += "\n:arrow_right: " + (i + 1) + ". [" + songName + "](" + song.getInfo().uri + ")" + " *" + songTimeString + "*";
            } else {
                queueList += "\n" + (i + 1) + ". [" + songName + "](" + song.getInfo().uri + ")" + " *" + songTimeString + "*";
            }
        }
        
        return queueList;
	}
}
