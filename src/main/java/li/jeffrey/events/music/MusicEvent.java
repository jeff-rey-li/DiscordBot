package li.jeffrey.events.music;

import java.awt.Color;
import java.util.*;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * Handles commands relating to playing music
 *
 * @author Jeffrey Li
 */
public class MusicEvent extends ListenerAdapter {

    private JDA jda;
    private String prefix;
    private VoiceChannel channel;
    private TextChannel textChannel;
    private AudioManager manager;
    private AudioPlayerManager playerManager;
    private TrackScheduler trackScheduler;
    private AudioPlayer player;
    private AudioSendHandler myHandler;
    private Timer timer;
    private HashMap<String, List<String>> savedPlaylists;

    public MusicEvent(JDA jda, String prefix) {
        this.jda = jda;
        this.prefix = prefix;
        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(jda);
        player.addListener(trackScheduler);
        myHandler = new AudioPlayerSendHandler(player);
        AudioSourceManagers.registerRemoteSources(playerManager);
        savedPlaylists = new HashMap<String, List<String>>();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith(prefix))
            return;
        String[] msg = event.getMessage().getContentRaw().split(" ");
        EmbedBuilder eb = new EmbedBuilder();

        if (msg[0].equals(prefix + "join")) {
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in a channel to play music!").complete();
                return;
            }
            if (manager != null && manager.getConnectedChannel() != null) {
                event.getChannel().sendMessage("I'm already connected to a channel!").complete();
                return;
            }

            joinChannel(event.getMember().getVoiceState().getChannel());

            eb.setColor(Color.YELLOW);
            eb.setTitle("Joined Voice Channel:");
            eb.addField("", "Joined " + channel.getName(), false);
            eb.setFooter("Made by Jeffrey Li");
            event.getChannel().sendMessage(eb.build()).complete();

            return;
        }
        if (msg[0].equals(prefix + "leave")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel.").complete();
                return;
            }

            leaveChannel();

            return;
        }
        if (msg[0].equals(prefix + "play")) {
            if (channel == null) {
                channel = event.getMember().getVoiceState().getChannel();
                manager = event.getGuild().getAudioManager();
                if (event.getMember().getVoiceState().getChannel() == null) {
                    event.getChannel().sendMessage("You must be in a channel to play music!").complete();
                    return;
                }
                manager.setSendingHandler(myHandler);
                manager.openAudioConnection(channel);
                eb.setColor(Color.YELLOW);
                eb.setTitle("Joined Voice Channel:");
                eb.addField("", "Joined " + channel.getName(), false);
                eb.setFooter("Made by Jeffrey Li");
                event.getChannel().sendMessage(eb.build()).complete();
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to play songs.").complete();
                return;
            }
            if (msg.length == 1) {
                if (player.isPaused()) {
                    event.getChannel().sendMessage("Pause is set to " + togglePause()).complete();
                }
                return;
            }
            trackScheduler.setTextChannel(event.getChannel()); // Sets the text channel for the bot to send messages to
            String input = event.getMessage().getContentRaw();
            input = input.replace(prefix + "play", "").trim();
            textChannel = event.getChannel();
            playSong(input, event.getMember());
            return;
        }
        if (msg[0].equals(prefix + "remove")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in the same voice channel to remove songs.").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to remove songs.").complete();
                return;
            }
            if (msg.length == 1)
                return;
            String toRemove = event.getMessage().getContentRaw();
            toRemove = toRemove.replace(prefix + "remove", "").trim();
            remove(toRemove, event.getMember());
            return;
        }
        if (msg[0].equals(prefix + "queue") || msg[0].equals(prefix + "q")) {
            eb = queue();
            event.getChannel().sendMessage(eb.build()).complete();
            return;
        }
        if (msg[0].equals(prefix + "save")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in the same voice channel to save a playlist.").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to save a playlist.").complete();
                return;
            }
            if (msg.length == 1) {
                event.getChannel().sendMessage("Please follow the command with a name to save the playlist as.").complete();
                return;
            }
            String playlistName = event.getMessage().getContentRaw().replace(prefix + "save", "").trim();
            boolean saved = savePlaylist(playlistName);
            eb.setTitle("Save Playlist");
            if (saved) {
                eb.setColor(Color.ORANGE);
                eb.addField("", "Saved current songs as: " + playlistName, false);
                eb.addField("", "Load with the command: " + prefix + "load" + " " + playlistName, false);
            } else {
                eb.setColor(Color.RED);
                eb.addField("", "There are no songs to be saved.", false);
            }
            eb.setFooter("Made by Jeffrey Li");
            event.getChannel().sendMessage(eb.build()).complete();
            return;
        }
        if (msg[0].equals(prefix + "load")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (channel == null) {
                channel = event.getMember().getVoiceState().getChannel();
                manager = event.getGuild().getAudioManager();
                if (event.getMember().getVoiceState().getChannel() == null) {
                    event.getChannel().sendMessage("You must be in a channel to play music!").complete();
                    return;
                }
                manager.setSendingHandler(myHandler);
                manager.openAudioConnection(channel);
                eb = new EmbedBuilder();
                eb.setColor(Color.YELLOW);
                eb.setTitle("Joined Voice Channel:");
                eb.addField("", "Joined " + channel.getName(), false);
                eb.setFooter("Made by Jeffrey Li");
                event.getChannel().sendMessage(eb.build()).complete();
            }
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in the same voice channel to load a playlist.").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to load a playlist.").complete();
                return;
            }
            if (msg.length == 1) {
                event.getChannel().sendMessage("Please follow the command with the playlist name to load the playlist.").complete();
                return;
            }
            String playlistName = event.getMessage().getContentRaw().replace(prefix + "load", "").trim();
            boolean loaded = loadPlayList(playlistName, event.getMember());
            eb = new EmbedBuilder();
            eb.setTitle("Load Playlist");
            if (loaded) {
                eb.setColor(Color.ORANGE);
                eb.addField("", "Loaded Playlist: " + playlistName, false);
            } else {
                eb.setColor(Color.RED);
                eb.addField("", "Playlist does not exist: " + playlistName, false);
            }
            eb.setFooter("Made by Jeffrey Li");
            event.getChannel().sendMessage(eb.build()).complete();
            return;
        }
        if (msg[0].equals(prefix + "playlists")) {
            eb = new EmbedBuilder();
            eb.setTitle("Saved Playlists");
            eb.setColor(Color.ORANGE);
            for (String i : savedPlaylists.keySet()) {
                eb.addField("", i, false);
            }
            eb.addBlankField(false);
            eb.setFooter("Made by Jeffrey Li");
            event.getChannel().sendMessage(eb.build()).complete();
            return;
        }
        if (msg[0].equals(prefix + "delete")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (msg.length == 1) {
                event.getChannel().sendMessage("Please follow the command with the name of the playlist you want to delete.").complete();
                return;
            }
            String playlistName = event.getMessage().getContentRaw().replace(prefix + "delete", "").trim();
            boolean deleted = deletePlaylist(playlistName);
            eb = new EmbedBuilder();
            eb.setTitle("Delete Playlist");
            if (deleted) {
                eb.setColor(Color.ORANGE);
                eb.addField("", "Deleted Playlist: " + playlistName, false);
                eb.setFooter("Made by Jeffrey Li");
                event.getChannel().sendMessage(eb.build()).complete();
                return;
            } else {
                eb.setColor(Color.RED);
                eb.addField("", "Unable to find playlist: " + playlistName, false);
                eb.setFooter("Made by Jeffrey Li");
                event.getChannel().sendMessage(eb.build()).complete();
                return;
            }
        }
        if (msg[0].equals(prefix + "repeat")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in a channel to change music settings!").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to change music settings.")
                        .complete();
                return;
            }
            if (msg.length == 1) {
                event.getChannel().sendMessage("Repeat is set to " + toggleRepeat()).complete();
                return;
            } else {
                if (msg[1].equals("on")) {
                    if (!trackScheduler.getRepeat()) {
                        event.getChannel().sendMessage("Repeat is set to " + toggleRepeat()).complete();
                    } else {
                        event.getChannel().sendMessage("Repeat is already turned on").complete();
                    }
                } else if (msg[1].equals("off")) {
                    if (trackScheduler.getRepeat()) {
                        event.getChannel().sendMessage("Repeat is set to " + toggleRepeat()).complete();
                    } else {
                        event.getChannel().sendMessage("Repeat is already turned off").complete();
                    }
                }
            }
        }
        if (msg[0].equals(prefix + "pause")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in a channel to change music settings!").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to change music settings.")
                        .complete();
                return;
            }
            event.getChannel().sendMessage("Pause is set to " + togglePause()).complete();
            return;
        }
        if (msg[0].equals(prefix + "skip")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in a channel to skip songs.").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to skip songs.")
                        .complete();
                return;
            }
            skip();
        }
        if (msg[0].equals(prefix + "shuffle")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in a channel to change music settings!").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to change music settings.")
                        .complete();
                return;
            }
            trackScheduler.setTextChannel(event.getChannel());
            eb = toggleShuffle();
            event.getChannel().sendMessage(eb.build()).complete();
            trackScheduler.resetSongs();
            player.playTrack(trackScheduler.getQueue().get(0));
            return;
        }
        if (msg[0].equals(prefix + "volume")) {
            if (manager == null) {
                event.getChannel().sendMessage("The bot is not connected to a channel.").complete();
                return;
            }
            if (event.getMember().getVoiceState().getChannel() == null) {
                event.getChannel().sendMessage("You must be in a channel to change music settings!").complete();
                return;
            }
            if (manager.getConnectedChannel() != null
                    && !manager.getConnectedChannel().equals(event.getMember().getVoiceState().getChannel())) {
                event.getChannel().sendMessage("You must be in the same voice channel to change music settings.")
                        .complete();
                return;
            }
            try {
                eb = setVolume(Integer.parseInt(msg[1]));
                event.getChannel().sendMessage(eb.build()).complete();
            } catch (NumberFormatException e) {
                return;
            }
        }


    }

    /**
     * Joins the specified voice channel
     *
     * @param channel - voice channel to join
     */
    private void joinChannel(VoiceChannel channel) {
        this.channel = channel;
        manager = jda.getGuildById("657977250771238912").getAudioManager();
        manager.setSendingHandler(myHandler);
        manager.openAudioConnection(channel);
    }

    /**
     * Leaves the voice channel and empties the queue
     */
    private void leaveChannel() {
        trackScheduler.emptyQueue();
        player.stopTrack();
        manager.closeAudioConnection();
        channel = null;
    }

    /**
     * Searches and plays the top song with the given search phrase
     *
     * @param searchPhrase - song to search for
     * @param addMember    - user that searched the song
     */
    private void playSong(String searchPhrase, Member addMember) {
        String search; // Put the searchPhrase into the correct search format
        if (!searchPhrase.contains("scsearch") && !searchPhrase.contains("www.")
                && !searchPhrase.contains("https://")) {
            search = "ytsearch:" + searchPhrase;
        } else {
            search = searchPhrase;
        }

        // Load the searched song
        playerManager.loadItem(search, new AudioLoadResultHandler() {

            public void trackLoaded(AudioTrack track) {
                trackScheduler.addSong(track, addMember);
                if (player.getPlayingTrack() == null) {
                    player.playTrack(track);
                    player.setPaused(false);
                }
            }

            public void playlistLoaded(AudioPlaylist playlist) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Play Song");
                eb.setColor(Color.BLUE);
                for (int i = 0; i < 5; i++) {
                    AudioTrack song = playlist.getTracks().get(i);
                    String title = song.getInfo().title;
                    String url = song.getInfo().uri;
                    if (i == 0) {
                        eb.addField("Search Results:", "1. [" + title + "](" + url + ")", false);
                    } else {
                        eb.addField("", (i + 1) + ". [" + title + "](" + url + ")", false);
                    }
                }
                eb.addField("", "Searched by: " + addMember.getAsMention(), false);
                Message searchResults = textChannel.sendMessage(eb.build()).complete();
                searchResults.addReaction("1️⃣").complete();
                searchResults.addReaction("2️⃣").complete();
                searchResults.addReaction("3️⃣").complete();
                searchResults.addReaction("4️⃣").complete();
                searchResults.addReaction("5️⃣").complete();


                class Reaction extends ListenerAdapter {
                    public void onGenericMessageReaction(GenericMessageReactionEvent event) {
                        if (event.getUser().isBot()) {
                            return;
                        }
                        if (!event.retrieveMessage().complete().getEmbeds().isEmpty()) {
                            if (event.retrieveMessage().complete().getEmbeds().get(0).getTitle().equals("Play Song")) {
                                String reactionEmote = event.getReactionEmote().getName();
                                String toPlay;
                                AudioTrack song = null;
                                switch (reactionEmote) {
                                    case "1️⃣":
//                                    song = playlist.getTracks().get(0);
                                        toPlay = event.retrieveMessage().complete().getEmbeds().get(0).getFields().get(0).getValue();
                                        break;
                                    case "2️⃣":
//                                    song = playlist.getTracks().get(1);
                                        toPlay = event.retrieveMessage().complete().getEmbeds().get(0).getFields().get(1).getValue();
                                        break;
                                    case "3️⃣":
//                                    song = playlist.getTracks().get(2);
                                        toPlay = event.retrieveMessage().complete().getEmbeds().get(0).getFields().get(2).getValue();
                                        break;
                                    case "4️⃣":
//                                    song = playlist.getTracks().get(3);
                                        toPlay = event.retrieveMessage().complete().getEmbeds().get(0).getFields().get(3).getValue();
                                        break;
                                    case "5️⃣":
//                                    song = playlist.getTracks().get(4);
                                        toPlay = event.retrieveMessage().complete().getEmbeds().get(0).getFields().get(4).getValue();
                                        break;
                                    default:
                                        return;
                                }

                                int indexLink = toPlay.indexOf("https:");
                                toPlay = toPlay.substring(indexLink);
                                toPlay = toPlay.substring(0, toPlay.length() - 1);
                                List<MessageEmbed.Field> fields = event.retrieveMessage().complete().getEmbeds().get(0).getFields();
                                String userID = fields.get(fields.size() - 1).getValue().replace("Searched by: ", "").replaceAll("[<>/@!]", "").trim();
                                Member member = event.getGuild().getMemberById(userID);
                                event.retrieveMessage().complete().delete().complete();
                                playSong(toPlay, member);
                            }
                        }
                    }
                }
                List<Object> listeners = jda.getRegisteredListeners();
                for (int i = 0; i < listeners.size(); i++) {
                    if (listeners.get(i).toString().startsWith("li.jeffrey.events.music.MusicEvent$1$1Reaction")) {
                        return;
                    }
                }
                jda.addEventListener(new Reaction());
            }

            public void noMatches() {
                trackScheduler.sendErrorMessage();
            }

            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
                trackScheduler.sendErrorMessage();
            }
        });
    }

    /**
     * Removes a song from queue with given search phrase
     *
     * @param toRemove     - song to remove (number in queue or song name)
     * @param removeMember - user that requested the song to be removed
     */
    private void remove(String toRemove, Member removeMember) {
        List<AudioTrack> queue = trackScheduler.getQueue();
        try { // Remove by number in queue
            int a = Integer.parseInt(toRemove); // Throws NumberFormatException if toRemove isn't a number
            String trackName = trackScheduler.removeSong(a - 1, removeMember);
            if (player.getPlayingTrack().getInfo().title.equals(trackName)) {
                player.stopTrack();
            } else {
                trackScheduler.resetRemoved();
            }
            return;
        } catch (NumberFormatException e) { // Remove by song title
            for (int i = 0; i < queue.size(); i++) {
                if (queue.get(i).getInfo().title.toLowerCase().contains(toRemove)) {
                    String trackName = trackScheduler.removeSong(i, removeMember);
                    if (player.getPlayingTrack().getInfo().title.equals(trackName)) {
                        player.stopTrack();
                    } else {
                        trackScheduler.resetRemoved();
                    }
                    return;
                }
            }
        }
    }

    /**
     * Returns the queue as a EmbedBuilder
     *
     * @return EmbedBuilder (call build() to display queue)
     */
    private EmbedBuilder queue() {
        List<AudioTrack> queue = trackScheduler.getQueue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Music");
        eb.setColor(Color.BLUE);
        String current = "";
        String queueList = "";
        int totalHours = 0;
        int totalMinutes = 0;
        int totalSeconds = 0;
        for (int i = 0; i < queue.size(); i++) {
            AudioTrack song = queue.get(i);
            String songName = song.getInfo().title;
            long songSecondsLong = song.getDuration() / 1000;
            int songMinutes = (int) songSecondsLong / 60;
            int songSeconds = (int) songSecondsLong % 60;
            String minuteString = (songMinutes < 10) ? "0" + songMinutes : Integer.toString(songMinutes);
            String secondString = (songSeconds < 10) ? "0" + songSeconds : Integer.toString(songSeconds);
            totalMinutes += songMinutes;
            totalSeconds += songSeconds;
            if (player.getPlayingTrack().getInfo().title.equals(song.getInfo().title)) {
                String vidID = song.getInfo().uri;
                vidID = vidID.substring(32);
                eb.setThumbnail("https://img.youtube.com/vi/" + vidID + "/sddefault.jpg");
                current = "[" + song.getInfo().title + "](" + song.getInfo().uri + ")";
                if (songName.length() > 44) {
                    songName = songName.substring(0, 44) + " ...";
                }
                queueList += "\n:arrow_right: " + (i + 1) + ". [" + songName + "](" + song.getInfo().uri + ")" + " *"
                        + minuteString + ":" + secondString + "*";
                continue;
            }
            if (songName.length() > 44) {
                songName = songName.substring(0, 45) + " ...";
            }
            queueList += "\n" + (i + 1) + ". [" + songName + "](" + song.getInfo().uri + ")" + " *" + minuteString + ":"
                    + secondString + "*";

        }
        totalMinutes += totalSeconds / 60;
        totalSeconds = totalSeconds % 60;
        totalHours += totalMinutes / 60;
        totalMinutes = totalMinutes % 60;
        String hourString = (totalHours < 10) ? "0" + totalHours : Integer.toString(totalHours);
        String minuteString = (totalMinutes < 10) ? "0" + totalMinutes : Integer.toString(totalMinutes);
        String secondString = (totalSeconds < 10) ? "0" + totalSeconds : Integer.toString(totalSeconds);
        String totalTime = (totalHours > 0) ? hourString + ":" + minuteString + ":" + secondString
                : minuteString + ":" + secondString;

        eb.addField("Queue (" + totalTime + "):", queueList, false);
        eb.addBlankField(false);
        eb.addField("Now Playing:", current, false);
        String info = "Repeat: ";
        if (trackScheduler.getRepeat()) {
            info += ":white_check_mark:";
        } else {
            info += ":x:";
        }
        info += "\nShuffle: ";
        if (trackScheduler.getShuffle()) {
            info += ":white_check_mark:";
        } else {
            info += ":x:";
        }
        info += "\nVolume: " + player.getVolume() + "%";
        if (player.isPaused()) {
            info += "\n\nThe bot is currently paused. Type !play to continue playing music.";
        }
        eb.addField("", info, false);
        eb.setFooter("Made by Jeffrey Li");
        return eb;
    }

    /**
     * Saves all songs in the queue as a playlist
     *
     * @param playlistName - name to identify the playlist
     * @return true if playlist was saved, false if playlist was not saved
     */
    private boolean savePlaylist(String playlistName) {
        List<AudioTrack> songs = trackScheduler.getQueue();
        if (songs.size() == 0) {
            return false;
        }
        List<String> songLinks = new ArrayList<String>();
        songs.forEach(e -> songLinks.add(e.getInfo().uri));
        savedPlaylists.put(playlistName, songLinks);
        return true;
    }

    /**
     * Loads a saved playlist and plays all the songs
     *
     * @param playlistName - name of the playlist to load
     * @param addMember    - user that requested to play the playlist
     * @return true if playlist was loaded, false if playlist was not found
     */
    private boolean loadPlayList(String playlistName, Member addMember) {
        List<String> songs = savedPlaylists.get(playlistName);
        if (songs == null) {
            return false;
        }
        songs.forEach(e -> playSong(e, addMember));
        return true;
    }

    /**
     * Deletes a saved playlist with the given name
     *
     * @param playlistName - name of the playlist to delete
     * @return true if playlist was successfully deleted, false if playlist was not found
     */
    private boolean deletePlaylist(String playlistName) {
        List<String> deletedPlaylist = savedPlaylists.remove(playlistName);
        if (deletedPlaylist == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Toggles the repeat function
     *
     * @return true if repeat is set to true, false if repeat is set to false
     */
    private boolean toggleRepeat() {
        return trackScheduler.setRepeat();
    }

    /**
     * Toggles the pause function
     *
     * @return true if pause is set to true, false if pause is set to false
     */
    private boolean togglePause() {
        player.setPaused(!player.isPaused());
        return player.isPaused();
    }

    /**
     * Stops the current track which skips to the next track
     */
    private void skip() {
        player.stopTrack();
    }

    /**
     * Toggles shuffle and returns result as a EmbedBuilder
     *
     * @return EmbedBuilder (call build() to display result)
     */
    private EmbedBuilder toggleShuffle() {
        boolean shuffle = trackScheduler.toggleShuffle();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Shuffle");
        eb.setColor(Color.GREEN);
        if (shuffle) {
            eb.addField("", "Shuffle has been turned on", false);
            eb.setFooter("Made by Jeffrey Li");
        } else {
            eb.addField("", "Shuffle has been turned off", false);
            eb.setFooter("Made by Jeffrey Li");
        }
        return eb;
    }

    /**
     * Sets the volume of the bot and returns result as a EmbedBuilder
     *
     * @param volume - Volume to set the bot (1-200)
     * @return EmbedBuilder (call build() to display result)
     */
    private EmbedBuilder setVolume(int volume) {
        player.setVolume(volume);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Volume");
        eb.setColor(Color.YELLOW);
        if (volume > 200 || volume <= 0) {
            eb.addField("", "Volume must be between 1-200%.", false);
            eb.setFooter("Made by Jeffrey Li");
        } else {
            eb.addField("", "Volume has been set to " + volume + "%", false);
            eb.setFooter("Made by Jeffrey Li");
        }
        return eb;
    }

    /**
     * Checks if no user is left in the voice channel and if so, starts a 30 second
     * timer to leave the voice channel
     */
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() != null && manager != null && manager.getConnectedChannel() != null) {
            if (event.getChannelLeft().getName().equals(manager.getConnectedChannel().getName())) {
                if (event.getChannelLeft().getMembers().size() == 1) {
                    timer = new Timer();
                    timer.schedule(new NoUserDisconnect(), 30000);
                    jda.getGuildById("657977250771238912").getTextChannelById("659141214129356810")
                            .sendMessage("No members detected in voice chat. Leaving in 30 seconds.").complete();
                }
            }
        }
    }

    /**
     * Checks if there are any users left in the voice channel and if not, leaves
     * the voice channel
     *
     * @author Jeffrey Li
     */
    class NoUserDisconnect extends TimerTask {
        public void run() {
            if (manager.getConnectedChannel().getMembers().size() == 1) {
                trackScheduler.emptyQueue();
                player.stopTrack();
                manager.closeAudioConnection();
                channel = null;
                jda.getGuildById("657977250771238912").getTextChannelById("659141214129356810")
                        .sendMessage("Left the channel due to inactivity.").complete();
            }
            timer.cancel();
        }

    }

}
