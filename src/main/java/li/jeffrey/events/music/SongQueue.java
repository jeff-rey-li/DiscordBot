package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongQueue {

    private static SongQueue songQueue;
//    private boolean isShuffle;
    private List<SongAddData> queue;
    private List<SongAddData> shuffledQueue;

    private SongQueue() {
//        isShuffle = false;
        queue = new ArrayList<SongAddData>();
        shuffledQueue = new ArrayList<SongAddData>();
    }

    public static SongQueue getInstance() {
        if (songQueue == null) {
            songQueue = new SongQueue();
        }
        return songQueue;
    }

    public boolean addSong(AudioTrack song, Member addMember) {
        SongAddData newEntry = new SongAddData(song, addMember);
        for (SongAddData i : queue) {
            if (i.getSong().getInfo().title.equals(song.getInfo().title)) {
                return false;
            }
        }
        queue.add(newEntry);
        if (isShuffle()) {
            shuffledQueue.add(newEntry);
        }
        return true;
    }

    public SongAddData removeSong(int index) {
        if (index > queue.size()) {
            return null;
        }
        SongAddData toRemove;
        if (isShuffle()) {
            toRemove = shuffledQueue.remove(index - 1);
            queue.remove(toRemove);
        } else {
            toRemove = queue.remove(index - 1);
        }
        toRemove.setLastQueueIndexWhenRemoved(index - 1);
        return toRemove;
    }

    public SongAddData removeSong(String keyPhrase) {
        SongAddData removedSong = null;
        for (SongAddData i : queue) {
            if (i.getSong().getInfo().title.toLowerCase().contains(keyPhrase.toLowerCase())) {
                if (isShuffle()) {
                    i.setLastQueueIndexWhenRemoved(shuffledQueue.indexOf(i));
                    shuffledQueue.remove(i);
                } else {
                    i.setLastQueueIndexWhenRemoved(queue.indexOf(i));
                }
                queue.remove(i);
                removedSong = i;
                break;
            }
        }
        return removedSong;
    }

    public SongAddData getNextSong(AudioTrack lastSong) {
        if (queue.isEmpty()) {
            return null;
        }
        List<SongAddData> toTraverse;
        if (isShuffle()) {
            toTraverse = shuffledQueue;
        } else {
            toTraverse = queue;
        }
        int indexOfLastSong = -1;
        for (int i = 0; i < toTraverse.size(); i++) {
            if (toTraverse.get(i).getSong().getInfo().title.equals(lastSong.getInfo().title)) {
                toTraverse.get(i).cloneSongAfterFinish();
                indexOfLastSong = i;
                break;
            }
        }
        if (indexOfLastSong == toTraverse.size() - 1 && MusicPlayer.getInstance().getIsRepeat()) {
            return toTraverse.get(0);
        } else {
            return toTraverse.get(indexOfLastSong + 1);
        }
    }

    public SongAddData getSongAtIndex(int index) {
        if (queue.isEmpty()) {
            return null;
        }
        if (isShuffle()) {
            if (index == shuffledQueue.size()) {
                return shuffledQueue.get(0);
            } else {
                return shuffledQueue.get(index);
            }
        } else {
            if (index == queue.size()) {
                return queue.get(0);
            } else {
                return queue.get(index);
            }
        }
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public List<SongAddData> getQueue() {
        if (isShuffle()) {
            return shuffledQueue;
        } else {
            return queue;
        }
    }

    public SongAddData shuffleQueue(AudioTrack lastPlaying) {
        SongAddData lastPlayingSongData = null;
        for (SongAddData i : queue) {
            if (i.getSong().getInfo().title.equals(lastPlaying.getInfo().title)) {
                lastPlayingSongData = i;
            }
        }
        lastPlayingSongData.getSong().makeClone();
        shuffledQueue = new ArrayList<SongAddData>();
        queue.forEach(e -> shuffledQueue.add(e));
        Collections.shuffle(shuffledQueue);
        return shuffledQueue.get(0);
    }

    public SongAddData deleteShuffleQueue(AudioTrack lastPlaying) {
        SongAddData lastPlayingSongData = null;
        for (SongAddData i : shuffledQueue) {
            if (i.getSong().getInfo().title.equals(lastPlaying.getInfo().title)) {
                lastPlayingSongData = i;
            }
        }
        lastPlayingSongData.getSong().makeClone();
        shuffledQueue = null;
        return queue.get(0);
    }

    private boolean isShuffle() {
        return MusicPlayer.getInstance().getIsShuffle();
    }

    public void resetQueue() {
        songQueue = null;
    }

}
