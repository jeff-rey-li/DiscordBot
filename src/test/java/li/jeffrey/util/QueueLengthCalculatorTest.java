package li.jeffrey.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import li.jeffrey.events.music.SongAddData;

public class QueueLengthCalculatorTest {
	
	@Test
	public void testCalculateQueueLengthWithHours() {
		List<SongAddData> queue = new ArrayList<SongAddData>();
		
		SongAddData firstSongAddData = mock(SongAddData.class);
		AudioTrack firstAudioTrack = mock(AudioTrack.class);
		when(firstSongAddData.getSong()).thenReturn(firstAudioTrack);
		when(firstAudioTrack.getDuration()).thenReturn(43199999l);
		queue.add(firstSongAddData);
		
		SongAddData secondSongAddData = mock(SongAddData.class);
		AudioTrack secondAudioTrack = mock(AudioTrack.class);
		when(secondSongAddData.getSong()).thenReturn(secondAudioTrack);
		when(secondAudioTrack.getDuration()).thenReturn(43199999l);
		queue.add(secondSongAddData);
		
		QueueLengthCalculator queueLengthCalculator = new QueueLengthCalculator();
		
		long totalTime = queueLengthCalculator.calculateTotalQueueLength(queue);
		
		String length = queueLengthCalculator.convertTimeToString(totalTime);
		
		assertEquals(length, "23:59:59");
	}
	
	@Test
	public void testCalculateQueueLengthWithMinutes() {
		List<SongAddData> queue = new ArrayList<SongAddData>();
		
		SongAddData firstSongAddData = mock(SongAddData.class);
		AudioTrack firstAudioTrack = mock(AudioTrack.class);
		when(firstSongAddData.getSong()).thenReturn(firstAudioTrack);
		when(firstAudioTrack.getDuration()).thenReturn(1799500l);
		queue.add(firstSongAddData);
		
		SongAddData secondSongAddData = mock(SongAddData.class);
		AudioTrack secondAudioTrack = mock(AudioTrack.class);
		when(secondSongAddData.getSong()).thenReturn(secondAudioTrack);
		when(secondAudioTrack.getDuration()).thenReturn(1799500l);
		queue.add(secondSongAddData);
		
		QueueLengthCalculator queueLengthCalculator = new QueueLengthCalculator();
		
		long totalTime = queueLengthCalculator.calculateTotalQueueLength(queue);
		
		String length = queueLengthCalculator.convertTimeToString(totalTime);
		
		assertEquals(length, "59:59");
	}
	
	@Test
	public void testCalculateQueueLengthWithHoursAndMinutesLessThan10() {
		List<SongAddData> queue = new ArrayList<SongAddData>();
		
		SongAddData firstSongAddData = mock(SongAddData.class);
		AudioTrack firstAudioTrack = mock(AudioTrack.class);
		when(firstSongAddData.getSong()).thenReturn(firstAudioTrack);
		when(firstAudioTrack.getDuration()).thenReturn(32949000l);
		queue.add(firstSongAddData);
		
		QueueLengthCalculator queueLengthCalculator = new QueueLengthCalculator();
		
		long totalTime = queueLengthCalculator.calculateTotalQueueLength(queue);
		
		String length = queueLengthCalculator.convertTimeToString(totalTime);
		
		assertEquals(length, "09:09:09");
	}
}
