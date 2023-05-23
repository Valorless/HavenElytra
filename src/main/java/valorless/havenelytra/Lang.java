package valorless.havenelytra;

import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class Lang {
	
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
		return text;
	}
	
	public static String Get(String key) {
		if(messages.Get(key) == null) {
			Log.Error(HavenElytra.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(messages.GetString(key));
	}
	
	public static String Get(String key, Object arg) {
		if(messages.Get(key) == null) {
			Log.Error(HavenElytra.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(messages.GetString(key), arg.toString()));
	}
	
	public static String Get(String key, Object arg1, Object arg2) {
		if(messages.Get(key) == null) {
			Log.Error(HavenElytra.plugin, String.format("Messages.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(messages.GetString(key), arg1.toString(), arg2.toString()));
	}
}
