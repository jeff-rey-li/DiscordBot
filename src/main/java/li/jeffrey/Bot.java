package li.jeffrey;

import java.util.List;
import java.util.concurrent.TimeUnit;

import li.jeffrey.events.*;
import li.jeffrey.events.games.*;
import li.jeffrey.events.games.paranoia.*;
import li.jeffrey.events.moderator.*;
import li.jeffrey.events.moderator.ChatMuteEvent;
import li.jeffrey.events.music.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * A Discord Bot that includes moderator, music, and other miscellaneous commands
 *
 * @author Jeffrey Li
 */
public class Bot extends ListenerAdapter {

    private static String prefix = "!";
    private static String myID = ""; // User ID of server owner
    private static String botToken = ""; // Bot token
    private static JDA jda;

    public static void main(String[] args) throws Exception {
        jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.GUILD_MEMBERS).build();
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.playing(prefix + "help"));
        addListeners();
        TimeUnit.SECONDS.sleep(1);
        JSONPlaylistReader.getInstance().readPreviousPlaylistsFile();
    }

    public static void addListeners() {
        jda.addEventListener(new Bot());
        jda.addEventListener(new Debug(myID, jda));
        jda.addEventListener(new VerifyEvent(jda, prefix));
        jda.addEventListener(new SpamPingEvent(jda, prefix));

        jda.addEventListener(new BanEvent(jda, prefix));
        jda.addEventListener(new ChatMuteEvent(jda, prefix));
        jda.addEventListener(new ChatUnmuteEvent(jda, prefix));
        jda.addEventListener(new DeafenEvent(jda, prefix));
        jda.addEventListener(new DisconnectEvent(jda, prefix));
        jda.addEventListener(new KickMemberEvent(jda, prefix));
        jda.addEventListener(new LockChannelEvent(jda, prefix));
        jda.addEventListener(new UnbanEvent(jda, prefix));
        jda.addEventListener(new UndeafenEvent(jda, prefix));
        jda.addEventListener(new UnlockChannelEvent(jda, prefix));
        jda.addEventListener(new VoiceMuteEvent(jda, prefix));
        jda.addEventListener(new VoiceUnmuteEvent(jda, prefix));
        jda.addEventListener(new KickMemberEvent(jda, prefix));

        jda.addEventListener(new HelpEvent(jda, prefix));
        jda.addEventListener(new JoinHomeworkEvent(jda, prefix));
        jda.addEventListener(new SongRequestEvent(jda, prefix));
        jda.addEventListener(new DiceRollEvent(jda, prefix));
        jda.addEventListener(new AskQuestionEvent(jda, prefix));
        jda.addEventListener(new AskQuestionRevealer(jda, prefix));
        jda.addEventListener(new WouldYouRatherEvent(jda, prefix));

        jda.addEventListener(new JoinEvent(jda, prefix));
        jda.addEventListener(new LeaveEvent(jda, prefix));
        jda.addEventListener(new PlaySongEvent(jda, prefix));
        jda.addEventListener(new SongChoiceListener(jda, prefix));
        jda.addEventListener(new SkipEvent(jda, prefix));
        jda.addEventListener(new GetQueueEvent(jda, prefix));
        jda.addEventListener(new RemoveSongEvent(jda, prefix));
        jda.addEventListener(new ToggleShuffleEvent(jda, prefix));
        jda.addEventListener(new ToggleRepeatEvent(jda, prefix));
        jda.addEventListener(new SavePlaylistEvent(jda, prefix));
        jda.addEventListener(new LoadPlaylistEvent(jda, prefix));
        jda.addEventListener(new DeletePlaylistEvent(jda, prefix));
        jda.addEventListener(new ViewPlaylistEvent(jda, prefix));
        jda.addEventListener(new ViewAllPlaylistsEvent(jda, prefix));
    }
    
    public static JDA getJDA() {
    	return jda;
    }

    public static void updatePrefix(String newPrefix) {
        prefix = newPrefix;
        jda.getPresence().setActivity(Activity.playing(prefix + "help"));
        updateListeners();
    }

    public static void updateListeners() {
        List<Object> listeners = jda.getRegisteredListeners();
        for (Object i : listeners) {
            jda.removeEventListener(i);
        }
        addListeners();
    }


    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().contains("discord.gg") && !event.getAuthor().isBot()) {
            event.getChannel().deleteMessageById(event.getMessageId()).complete();
        }
        if (event.getMessage().getContentRaw().equals(prefix + "invite")) {
            String invite = "https://discord.gg/u2BjRFF"; // Discord Invite Link
            event.getChannel().sendMessage("Use this link to invite members to the server: " + invite).complete();
        }
        if (event.getMessage().getContentRaw().contains(prefix + "update") && event.getAuthor().getId().equals(myID)) {
            String[] newChar = event.getMessage().getContentRaw().split(" ");
            if (newChar.length == 1)
                return;
            event.getChannel().sendMessage("New prefix is now: " + newChar[1]).complete();
            updatePrefix(newChar[1]);
        }
    }

}

