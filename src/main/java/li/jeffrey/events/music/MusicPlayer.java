package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.Bot;
import li.jeffrey.constants.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class MusicPlayer {

    private static MusicPlayer musicPlayer = null;

    private AudioManager manager;
    private AudioPlayerManager playerManager;
    private TrackScheduler trackScheduler;
    private AudioPlayer player;
    private AudioSendHandler myHandler;

    private boolean isShuffle;
    private boolean isRepeat;

    private MusicPlayer() {
        manager = Bot.getJDA().getGuildById(Constants.GUILD_ID).getAudioManager();
        playerManager = new DefaultAudioPlayerManager();
        trackScheduler = new TrackScheduler();
        player = playerManager.createPlayer();
        player.addListener(trackScheduler);
        myHandler = new AudioPlayerSendHandler(player);
        AudioSourceManagers.registerRemoteSources(playerManager);
        isShuffle = false;
        isRepeat = true;
    }

    public static MusicPlayer getInstance() {
        if (musicPlayer == null) {
            musicPlayer = new MusicPlayer();
        }

        return musicPlayer;
    }

    public void playSong(AudioTrack song, Member memberRequestingSong, TextChannel notificationChannel) {
        player.playTrack(song.makeClone());
        player.setPaused(false);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.BLUE);
        eb.setTitle("Now Playing");
        if (song instanceof YoutubeAudioTrack) {
            String vidID = song.getInfo().uri;
            vidID = vidID.substring(32);
            eb.setThumbnail("https://img.youtube.com/vi/" + vidID + "/sddefault.jpg");
        }
        eb.setDescription("[" + song.getInfo().title + "](" + song.getInfo().uri + ")\nAdded by: " + memberRequestingSong.getAsMention());
        notificationChannel.sendMessage(eb.build()).queue();
    }

    public AudioTrack getNowPlaying() {
        return player.getPlayingTrack();
    }

    public AudioManager getAudioManager() {
        return manager;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public AudioSendHandler getMyHandler() {
        return myHandler;
    }

    public boolean getIsShuffle() {
        return isShuffle;
    }

    public boolean toggleIsShuffle() {
        return isShuffle = !isShuffle;
    }

    public boolean getIsRepeat() {
        return isRepeat;
    }

    public boolean toggleIsRepeat() {
        return isRepeat = !isRepeat;
    }

}
