package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import li.jeffrey.Bot;
import li.jeffrey.constants.Constants;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicPlayer {

    private static MusicPlayer musicPlayer = null;

    private AudioManager manager;
    private AudioPlayerManager playerManager;
    private TrackScheduler trackScheduler;
    private AudioPlayer player;
    private AudioSendHandler myHandler;

    private MusicPlayer() {
        manager = Bot.getJda().getGuildById(Constants.GUILD_ID).getAudioManager();
        playerManager = new DefaultAudioPlayerManager();
        trackScheduler = new TrackScheduler(Bot.getJda());
        player = playerManager.createPlayer();
        player.addListener(trackScheduler);
        myHandler = new AudioPlayerSendHandler(player);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public static MusicPlayer getInstance() {
        if (musicPlayer == null) {
            musicPlayer = new MusicPlayer();
        }

        return musicPlayer;
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

}
