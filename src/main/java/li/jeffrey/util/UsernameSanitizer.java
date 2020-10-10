package li.jeffrey.util;

public class UsernameSanitizer {
	private static UsernameSanitizer usernameSanitizer = null; 
	
	private UsernameSanitizer () {
		
	}
	
	public static UsernameSanitizer getInstance() {
		if(usernameSanitizer == null) {
			usernameSanitizer = new UsernameSanitizer();
		}
		
		return usernameSanitizer;
	}
	
	public String sanitizeUsername(String message) {
        return message.replaceAll("[.<>/@!]", "");
    }
}
