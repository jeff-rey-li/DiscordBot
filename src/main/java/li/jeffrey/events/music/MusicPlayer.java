package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import li.jeffrey.Bot;
import li.jeffrey.constants.Constants;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicPlayer {

    private static AudioManager manager = Bot.getJDA().getGuildById(Constants.GUILD_ID).getAudioManager();
    private static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private static TrackScheduler trackScheduler = new TrackScheduler(Bot.getJDA());
    private static AudioPlayer player = playerManager.createPlayer();
    private static AudioSendHandler myHandler = new AudioPlayerSendHandler(player);

    public static AudioManager getAudioManager() {
        return manager;
    }

    public static AudioPlayer getPlayer() {
        return player;
    }

    public static AudioSendHandler getMyHandler() {
        return myHandler;
    }

}
