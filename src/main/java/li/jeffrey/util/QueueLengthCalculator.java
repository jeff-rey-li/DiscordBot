package li.jeffrey.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import li.jeffrey.events.music.SongAddData;

public class QueueLengthCalculator {
	public long calculateTotalQueueLength(List<SongAddData> queue) {
		long totalTime = 0;
		
		for(SongAddData songAddData: queue) {
			totalTime += songAddData.getSong().getDuration();
		}
		
		return totalTime;
	}
	
	public String convertTimeToString(long totalTime) {
		long timeRemaining = totalTime;
		
		long hours = TimeUnit.MILLISECONDS.toHours(timeRemaining);
		timeRemaining -= TimeUnit.HOURS.toMillis(hours);
		
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
		timeRemaining -= TimeUnit.MINUTES.toMillis(minutes);
		
		long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(getHours(hours));
		stringBuilder.append(getMinutes(minutes));
		stringBuilder.append(getSeconds(seconds));
		
		return stringBuilder.toString();
	}

	private String getHours(long hours) {
		if(hours == 0) {
			return "";
		} else if(hours < 10) {
			return "0" + hours + ":";
		}else {
			return hours + ":";
		}
	}
	
	private String getSeconds(long seconds) {
		if(seconds < 10) {
			return "0" + seconds;
		} else {
			return "" + seconds;
		}
	}
	
	private String getMinutes(long minutes) {
		if(minutes < 10) {
			return "0" + minutes + ":";
		} else {
			return minutes + ":";
		}
	}
}
