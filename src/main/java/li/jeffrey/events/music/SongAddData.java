package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;

public class SongAddData {

    private AudioTrack song;
    private Member memberAdded;
    private int lastQueueIndexWhenRemoved;

    public SongAddData(AudioTrack song, Member memberAdded) {
        this.song = song;
        this.memberAdded = memberAdded;
        lastQueueIndexWhenRemoved = -1;
    }

    public AudioTrack getSong() {
        return song;
    }

    public Member getMemberAdded() {
        return memberAdded;
    }

    public int getLastQueueIndexWhenRemoved() { return lastQueueIndexWhenRemoved; }

    public void setLastQueueIndexWhenRemoved(int indexOfSongWhenRemoved) { lastQueueIndexWhenRemoved = indexOfSongWhenRemoved; }

    public void cloneSongAfterFinish() {
        song = song.makeClone();
    }

}
