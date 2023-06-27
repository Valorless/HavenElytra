package valorless.havenelytra;

import valorless.valorlessutils.config.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class Lang {
	
	public enum Language { english, danish, german, spanish, turkish, french }
	
	public static Config messages;
		
	public static class Placeholders{
		public static String plugin = "§7[§aHaven§bElytra§7]§r";
	}
	
	public static String Parse(String text) {
		if(!Utils.IsStringNullOrEmpty(text)) {
			text = text.replace("&", "§");
			text = text.replace("\\n", "\n");
			if(text.contains("%plugin%")) { text = text.replace("%plugin%", Placeholders.plugin); }
		}
		return hex(text);
	}
	
	public static String Get(String key) {
		if(messages.Get(key) == null) {
			Log.Error(Main.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(messages.GetString(key));
	}
	
	public static String Get(String key, Object arg) {
		if(messages.Get(key) == null) {
			Log.Error(Main.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(messages.GetString(key), arg.toString()));
	}
	
	public static String Get(String key, Object arg1, Object arg2) {
		if(messages.Get(key) == null) {
			Log.Error(Main.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(messages.GetString(key), arg1.toString(), arg2.toString()));
	}
	
	public static String hex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
           
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }
           
            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
