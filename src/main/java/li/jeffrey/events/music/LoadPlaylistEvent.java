package li.jeffrey.events.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import li.jeffrey.events.structure.ReceivedEventListener;
import li.jeffrey.util.MusicCommonUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class LoadPlaylistEvent extends ReceivedEventListener {

    public LoadPlaylistEvent(JDA jda, String prefix) {
        super(jda, prefix);
    }

    private void sendSpecifyPlaylistNameMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("Please specify a playlist name");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendPlaylistDoesNotExistMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("This playlist does not exist");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void doEvent(GenericEvent genericEvent) {
        GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
        if (event.getMessage().getContentRaw().split(" ").length == 1) {
            sendSpecifyPlaylistNameMessage((GuildMessageReceivedEvent) genericEvent);
        }
        List<AudioTrack> playlist =
                SavedPlaylists.getInstance().getPlaylist(event.getMessage().getContentRaw().replace(prefix +
                        "loadplaylist", "").trim());
        if (playlist == null) {
            sendPlaylistDoesNotExistMessage((GuildMessageReceivedEvent) genericEvent);
        } else {
            if (MusicCommonUtil.getInstance().isMemberNotConnectedToChannel(event.getMember())) {
                MusicCommonUtil.getInstance().sendUserMustConnectToVoiceChannelMessage(event.getChannel());
                return;
            } else if (!MusicCommonUtil.getInstance().isBotAlreadyConnectedToVoiceChannel()) {
                VoiceChannel voiceChannel =
                        MusicCommonUtil.getInstance().getMemberConnectedVoiceChannel(event.getMember());
                MusicCommonUtil.getInstance().joinVoiceChannel(voiceChannel);
                MusicCommonUtil.getInstance().sendBotJoinedChannelMessage(event.getChannel(), event.getMember());
            } else if (!MusicCommonUtil.getInstance().isMemberConnectedToSameChannel(event.getMember())) {
                MusicCommonUtil.getInstance().sendUserNotConnectedToSameChannelMessage(event.getChannel());
                return;
            }
            for (int i = 0; i < playlist.size(); i++) {
                boolean success = SongQueue.getInstance().addSong(playlist.get(i).makeClone(), event.getMember());
                if (success) {
                    MusicCommonUtil.getInstance().sendNewSongQueuedMessage(playlist.get(i), event.getMember(),
                            event.getChannel());
                    if (MusicPlayer.getInstance().getPlayer().getPlayingTrack() == null) {
                        MusicPlayer.getInstance().playSong(playlist.get(i), event.getMember(), event.getChannel());
                    }
                } else {
                    MusicCommonUtil.getInstance().sendSongAlreadyInQueueMessage(playlist.get(i), event.getChannel());
                }
            }
        }
    }

    @Override
    public boolean shouldEventTrigger(GenericEvent genericEvent) {
        return genericEvent instanceof GuildMessageReceivedEvent && ((GuildMessageReceivedEvent) genericEvent).getMessage().getContentRaw().startsWith(prefix + "loadplaylist");
    }
}
