package li.jeffrey.util;

import li.jeffrey.events.music.MusicPlayer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class MusicCommonUtil {
	private static MusicCommonUtil musicCommonUtil = null; 
	
	private MusicCommonUtil () {
		
	}
	
	public static MusicCommonUtil getInstance() {
		if(musicCommonUtil == null) {
			musicCommonUtil = new MusicCommonUtil();
		}
		
		return musicCommonUtil;
	}
	
	public boolean isBotAlreadyConnectedToVoiceChannel() {
        return MusicPlayer.getAudioManager().getConnectedChannel() != null;
    }
	
	public VoiceChannel getMemberConnectedVoiceChannel(Member member) {
        return member.getVoiceState().getChannel();
    }
}
