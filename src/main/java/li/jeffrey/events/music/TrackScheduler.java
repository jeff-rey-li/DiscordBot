package li.jeffrey.events.music;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles the ordering of songs and plays the next song when the previous ends
 *
 * @author Jeffrey Li
 */
public class TrackScheduler extends AudioEventAdapter {

    private boolean repeat;
    private boolean shuffle;
    private boolean pause;
    private int removed;
    private JDA jda;
    private TextChannel textChannel;

    List<AudioTrack> queued = new ArrayList<AudioTrack>();
    List<Member> addMembers = new ArrayList<Member>();
    List<AudioTrack> shuffleList = new ArrayList<AudioTrack>();
    List<Member> shuffleMembers = new ArrayList<Member>();

    public TrackScheduler(JDA jda) {
        this.jda = jda;
        repeat = true;
        shuffle = false;
        pause = false;
        removed = -1;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        pause = true;
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        pause = false;
    }

    /**
     * Displays the info for the song when a new song starts
     */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        Member member = null;
        if (shuffle) {
            for (int i = 0; i < shuffleList.size(); i++) {
                if (track.equals(shuffleList.get(i))) {
                    member = shuffleMembers.get(i);
                }
            }
        } else {
            for (int i = 0; i < queued.size(); i++) {
                if (track.equals(queued.get(i))) {
                    member = addMembers.get(i);
                }
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        eb.setColor(Color.BLUE);
        String url = track.getInfo().uri;
        String trackTitle = track.getInfo().title;
        eb.setTitle("Now Playing:");
        String vidID = url;
        vidID = vidID.substring(32);
        eb.setThumbnail("https://img.youtube.com/vi/" + vidID + "/sddefault.jpg");
        eb.addField("", "Playing [" + trackTitle + "](" + url + ")", true);
        eb.addField("", "Added by: " + member.getAsMention(), false);
        eb.setFooter("Made by Jeffrey Li");
        mb.setEmbed(eb.build());
        textChannel.sendMessage(mb.build()).complete();
    }

    /**
     * Starts the next song when previous song ends
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) { // Previous song ended
            if (shuffle) {
                if (removed != -1) {
                    if (removed != shuffleList.size()) {
                        player.playTrack(shuffleList.get(removed));
                    } else {
                        if (repeat) {
                            player.playTrack(shuffleList.get(0));
                        }
                    }
                    removed = -1;
                    return;
                }
                // Start next track
                int nextTrack = -1;
                for (int i = 0; i < shuffleList.size(); i++) {
                    if (shuffleList.get(i).getInfo().title.equals(track.getInfo().title)) {
                        nextTrack = i + 1;
                        shuffleList.set(i, shuffleList.get(i).makeClone());
                        break;
                    }
                }
                if (nextTrack != shuffleList.size()) {
                    player.playTrack(shuffleList.get(nextTrack));
                } else {
                    if (repeat) {
                        player.playTrack(shuffleList.get(0));
                    }
                }
            } else {
                if (removed != -1) {
                    if (removed != queued.size()) {
                        player.playTrack(queued.get(removed));
                    } else {
                        if (repeat) {
                            player.playTrack(queued.get(0));
                        }
                    }
                    removed = -1;
                    return;
                }
                // Start next track
                int nextTrack = -1;
                for (int i = 0; i < queued.size(); i++) {
                    if (queued.get(i).getInfo().title.equals(track.getInfo().title)) {
                        nextTrack = i + 1;
                        queued.set(i, queued.get(i).makeClone());
                        break;
                    }
                }
                if (nextTrack != queued.size()) {
                    player.playTrack(queued.get(nextTrack));
                } else {
                    if (repeat) {
                        player.playTrack(queued.get(0));
                    }
                }
            }
        } else if (endReason == AudioTrackEndReason.STOPPED) { // Song was skipped
            if (shuffle) {
                if (removed != -1) {
                    if (removed != shuffleList.size()) {
                        player.playTrack(shuffleList.get(removed));
                    } else {
                        if (repeat) {
                            player.playTrack(shuffleList.get(0));
                        }
                    }
                    removed = -1;
                    return;
                }
                int nextTrack = -1;
                for (int i = 0; i < shuffleList.size(); i++) {
                    if (shuffleList.get(i).getInfo().title.equals(track.getInfo().title)) {
                        nextTrack = i + 1;
                        shuffleList.set(i, shuffleList.get(i).makeClone());
                        break;
                    }
                }
                if (nextTrack != shuffleList.size()) {
                    player.playTrack(shuffleList.get(nextTrack));
                } else {
                    if (repeat) {
                        player.playTrack(shuffleList.get(0));
                    }
                }
            } else {
                if (removed != -1) {

                    if (removed != queued.size()) {
                        player.playTrack(queued.get(removed));
                    } else {
                        if (repeat) {
                            player.playTrack(queued.get(0));
                        }
                    }
                    removed = -1;
                    return;
                }
                int nextTrack = -1;
                for (int i = 0; i < queued.size(); i++) {
                    if (queued.get(i).getInfo().title.equals(track.getInfo().title)) {
                        nextTrack = i + 1;
                        queued.set(i, queued.get(i).makeClone());
                        break;
                    }
                }
                if (nextTrack != queued.size()) {
                    player.playTrack(queued.get(nextTrack));
                } else {
                    if (repeat) {
                        player.playTrack(queued.get(0));
                    }
                }
            }
        }
        // endReason == FINISHED: A track finished or died by an exception (mayStartNext
        // = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not
        // finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you
        // can put a
        // clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be
        // received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start
        // a new track
    }

    /**
     * Adds a new track to the queue if song is not already in queue
     *
     * @param track     - song to add
     * @param addMember - user that added the song
     */
    public void addSong(AudioTrack track, Member addMember) {
        ArrayList<String> songNames = new ArrayList<String>();
        queued.forEach(e -> {
            songNames.add(e.getInfo().title);
        });
        if (!songNames.contains(track.getInfo().title)) {
            queued.add(track);
            addMembers.add(addMember);
            if (shuffle) {
                shuffleList.add(track);
                shuffleMembers.add(addMember);
            }
            EmbedBuilder eb = new EmbedBuilder();
            MessageBuilder mb = new MessageBuilder();
            eb.setColor(Color.GREEN);
            String url = track.getInfo().uri;
            String trackTitle = track.getInfo().title;
            eb.setTitle("Queued New Song:");
            String vidID = url;
            vidID = vidID.substring(32);
            eb.setThumbnail("https://img.youtube.com/vi/" + vidID + "/sddefault.jpg");
            eb.addField("", "Added [" + trackTitle + "](" + url + ")", false);
            eb.addField("", "Added by: " + addMembers.get(addMembers.size() - 1).getAsMention(), false);
            if (pause) {
                eb.addField("", "The player is currently paused. Do !play to contine playing music.", false);
            }
            eb.setFooter("Made by Jeffrey Li");
            mb.setEmbed(eb.build());
            textChannel.sendMessage(mb.build()).complete();
        } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setTitle("Queue New Song");
            eb.addField("", "This song is already in queue", false);
            eb.setFooter("Made by Jeffrey Li");
            textChannel.sendMessage(eb.build()).complete();
        }

    }

    /**
     * Returns the queue
     *
     * @return List of the AudioTracks in order
     */
    public List<AudioTrack> getQueue() {
        if (shuffle) {
            return shuffleList;
        } else {
            return queued;
        }
    }

    /**
     * Empties the queue
     */
    public void emptyQueue() {
        queued = new ArrayList<AudioTrack>();
        addMembers = new ArrayList<Member>();
        shuffle = false;
        shuffleList = new ArrayList<AudioTrack>();
        shuffleMembers = new ArrayList<Member>();
    }

    /**
     * Toggles repeat
     *
     * @return true if repeat is turned on, false if repeat is turned off
     */
    public boolean setRepeat() {
        repeat = !repeat;
        return repeat;
    }

    /**
     * Toggles shuffle and updates the queue
     *
     * @return true if shuffle is turned on, false if shuffle is turned off
     */
    public boolean toggleShuffle() {
        shuffle = !shuffle;
        for (int i = 0; i < queued.size(); i++) {
            queued.set(i, queued.get(i).makeClone());
        }
        if (shuffle) {
            shuffleList = new ArrayList<AudioTrack>();
            queued.forEach(e -> shuffleList.add(e));
            Collections.shuffle(shuffleList);
            shuffleMembers = new ArrayList<Member>();
            for (int i = 0; i < shuffleList.size(); i++) {
                int a = queued.indexOf(shuffleList.get(i));
                shuffleMembers.add(addMembers.get(a));
            }
        } else {
            shuffleList = null;
            shuffleMembers = null;
        }

        return shuffle;
    }

    /**
     * Removes song at given index in queue
     *
     * @param song         - index of song to remove
     * @param removeMember - member that requested to remove the song
     * @return title of the song
     */
    public String removeSong(int song, Member removeMember) {
        EmbedBuilder eb = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        AudioTrack track = null;
        if (shuffle) {
            track = shuffleList.get(song);
        } else {
            track = queued.get(song);
        }
        eb.setColor(Color.RED);
        String url = track.getInfo().uri;
        String trackTitle = track.getInfo().title;
        eb.setTitle("Removed Song:");
        String vidID = url;
        vidID = vidID.substring(32);
        eb.setThumbnail("https://img.youtube.com/vi/" + vidID + "/sddefault.jpg");
        eb.addField("", "Removed [" + trackTitle + "](" + url + ")", false);
        eb.addField("", "Added by: " + addMembers.get(song).getAsMention(), false);
        eb.addField("", "Removed by: " + removeMember.getAsMention(), false);
        eb.setFooter("Made by Jeffrey Li");
        mb.setEmbed(eb.build());
        textChannel.sendMessage(mb.build()).complete();
        if (shuffle) {
            removed = song;
            shuffleList.remove(song);
            shuffleMembers.remove(song);
            addMembers.remove(queued.indexOf(track));
            queued.remove(track);
            return trackTitle;
        } else {
            removed = song;
            queued.remove(song);
            addMembers.remove(song);
            return trackTitle;
        }
    }

    /**
     * Makes a clone of all of the audio tracks inside the queue
     */
    public void resetSongs() {
        for (int i = 0; i < queued.size(); i++) {
            queued.set(i, queued.get(i).makeClone());
            if (shuffle) {
                shuffleList.set(i, shuffleList.get(i).makeClone());
            }
        }
    }

    /**
     * Sends an error message notifying the user that a track could not be found
     */
    public void sendErrorMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("Error");
        eb.addField("", "Track could not be found or could not be loaded.", false);
        eb.setFooter("Made by Jeffrey Li");
        textChannel.sendMessage(eb.build()).complete();
    }

    /**
     * Sets the text channel to send messages to
     *
     * @param textChannel - channel to send bot messages
     */
    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    /**
     * Reset the index of removed to outside of the queue
     */
    public void resetRemoved() {
        this.removed = -1;
    }

    /**
     * Returns the value of repeat
     *
     * @return true if repeat is on, false if repeat is off
     */
    public boolean getRepeat() {
        return repeat;
    }

    /**
     * Returns the value of shuffle
     *
     * @return true if shuffle is on, false if shuffle is off
     */
    public boolean getShuffle() {
        return shuffle;
    }

}
